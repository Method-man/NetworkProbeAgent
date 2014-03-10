
package cz.uhk.thesis.modules;

import cz.uhk.thesis.interfaces.ProbeService;
import cz.uhk.thesis.interfaces.Probe;
import cz.uhk.thesis.core.Core;
import cz.uhk.thesis.core.LogService;
import cz.uhk.thesis.interfaces.DeviceObserver;
import cz.uhk.thesis.model.Device;
import cz.uhk.thesis.model.Parser;
import cz.uhk.thesis.interfaces.Stateful;
import static cz.uhk.thesis.interfaces.Stateful.STATE_INITIAL;
import cz.uhk.thesis.model.IcmpPacket;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Icmp.IcmpType;
import org.jnetpcap.protocol.network.Ip4;

/**
 *
 * @author Filip Valenta
 */
public class TracerouteProbeService extends Stateful implements ProbeService, DeviceObserver {

    public static final int STATE_HAS_GATEWAY = 1;
    public static final int STATE_PROCESS_TRACEROUTE = 2;
    public static final int STATE_TRACEROUTE_DONE = 3;
    
    private final Probe probe;
    private final Core core;
    
    private int tracerouteTtl = 1;
    private int tracerouteTimeoutSeconds = 15; // default traceroute timeout 4 s
    private byte[] ip;
    
    public TracerouteProbeService(Core core, Probe probe)
    {
        this.probe = probe;
        this.core = core;
    }
    
    @Override
    public void packetParse(JPacket packet) {
        // function A - Obtain gateway MAC
        TracerouteProbe probeTraceroute = ((TracerouteProbe)probe);
        if(probeTraceroute.useThisModuleObtainGatewayMac(packet)) {
            
            // TODO: presunout cely obsah do metody
            
            Ethernet e = packet.getHeader(new Ethernet());
            Device d = core.GetDeviceManager().getDevice(e.source());
            d.setIsGateway(true);
            if(IsInState(STATE_INITIAL)) {
                SetState(STATE_HAS_GATEWAY);
            }
            
        }
        // function B - Traceroute
        if(probeTraceroute.useThisModuleTraceroute(packet)) {
            byte[] destination = ((Ip4)packet.getHeader(new Ip4())).source();
            boolean requiredDestination = destination == GetTracerouteHostTestIp();
            int type = ((Icmp)packet.getHeader(new Icmp())).type();
            if(type == IcmpType.TIME_EXCEEDED_ID && !requiredDestination) { // vyprselo TTL a neni to cilova destinace
                tracerouteTtl++;
                TracerouteSend();
                addIp2RouteFromPacket(packet, destination);
            } else if(requiredDestination || type == IcmpType.ECHO_REPLY_ID) { // je to cilova destinace nebo odpovedel nalezeno
                SetState(STATE_TRACEROUTE_DONE);
                addIp2RouteFromPacket(packet, destination);
            }
        }
    }
    
    private void addIp2RouteFromPacket(JPacket packet, byte[] destination)
    {
        Ethernet e = packet.getHeader(new Ethernet());
        Device d = core.GetDeviceManager().getDevice(e.source());
        d.AddRoute2internet(destination);
    }
    
    private void addIp2Route(byte[] destination)
    {
        Device d = core.GetDeviceManager().GetGateway();
        if(d != null) {
            d.AddRoute2internet(destination);
        } else {
            LogService.Log2Console(this, "pokus o pridani traceroute ip do null device");
        }
    }

    @Override
    public void packetCompare(String ip, byte[] mac, Parser parser) {
        
    }

    @Override
    public void packetCompare(String ip, byte[] mac) {
        
    }

    @Override
    public void probeSend() {
        
        try {
            new URL("http://"+GetTracerouteHostTestHostname()).openStream().close();
        } catch (IOException ex) {
            LogService.Log2ConsoleError(this, ex);
        }
    }
    
    private void TracerouteSend()
    {
        if(IsInState(STATE_TRACEROUTE_DONE)) return;
        
        if((!IsInState(STATE_PROCESS_TRACEROUTE) || tracerouteTtl > 1)) {
            SetState(STATE_PROCESS_TRACEROUTE);
            LogService.Log2Console(this, "odesilam traceroute hop "+tracerouteTtl);
            try {
                Device gateway = core.GetDeviceManager().GetGateway();
                if(gateway != null) {
                    PcapIf activeDevice = core.getNetworkManager().getActiveDevice();
                    
                    IcmpPacket icmp = new IcmpPacket(core);
                    icmp.ethernetDestination(gateway.getMacAsByte());
                    icmp.ipSource(core.getNetworkManager().GetActiveDeviceIPasByte());
                    icmp.ipDestination(GetTracerouteHostTestIp());
                    icmp.TimeToLive(tracerouteTtl);
                    icmp.recalculateAllChecksums();
                    
                    int state_icmp = Pcap.OK;
                    state_icmp += core.getNetworkManager().SendPacket(activeDevice, icmp);
                    if(state_icmp == Pcap.OK) {
                        LogService.Log2Console(probe.GetModuleName(), "Traceroute Packet (hop "+icmp.TimeToLive()+") odeslán");
                    }
                }
            } catch (UnknownHostException ex) {
                LogService.Log2ConsoleError(this, ex);
            }
        }
    }

    @Override
    public void Notify() {
        if(IsInState(STATE_HAS_GATEWAY)) {
            tracerouteTtl = 1;
            TracerouteSend();
            checkTraceroute();
        }
    }

    /**
     * Check and set allowed state
     * 
     * @param state 
     */
    @Override
    public void SetState(int state) {
        switch(state) {
            case STATE_INITIAL:
            case STATE_HAS_GATEWAY:
            case STATE_PROCESS_TRACEROUTE:
            case STATE_TRACEROUTE_DONE:
            {
                this.state = state;
            } break;
            default: this.state = STATE_INITIAL; break;
        }
        LogService.Log2Console(this, "nastavuji stav "+this.state);
        core.getProbeLoader().NotifyAllModules();
        
        // TODO: prozatim odesilam vzdy, potom se to bude odesialt pri jine prilezitosti
        core.getAdapterService().ServerXmlSend(core);
    }    

    @Override
    public boolean IsInState(int state) {
        return super.IsInState(state);
    }
    
    /**
     * Obtain traceroute primary destination IP
     * 
     * @return 
     */
    public byte[] GetTracerouteHostTestIp()
    {
        if(ip == null) {
            ip = null;
            try {
                ip = new byte[4];
                ip = InetAddress.getByName(GetTracerouteHostTestHostname()).getAddress();
            } catch (UnknownHostException ex) {
                LogService.Log2ConsoleError(this, ex);
            }
        }
        return ip;
    }
    
    public String GetTracerouteHostTestHostname()
    {
        return core.getAdapterService().getTracerouteHostname();
    }
    
    private void checkTraceroute()
    {
        ScheduledExecutorService mainLoopExecutor = Executors.newScheduledThreadPool(1);
        mainLoopExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                
                if(tracerouteTtl > 1) return; // traceroute je OK
                
                LogService.Log2Console(this, "traceroute bez odpovedi, nahrazuji defaultni cestou");
                try {
                    for(String sIp: core.getAdapterService().getTracerouteDefault()) {
                        InetAddress ip = InetAddress.getByName(sIp);
                        addIp2Route(ip.getAddress());
                    }
                    SetState(STATE_TRACEROUTE_DONE);
                } catch (UnknownHostException ex) {
                    LogService.Log2ConsoleError(this, ex);
                }
            }
        }, tracerouteTimeoutSeconds, TimeUnit.SECONDS);
    }
    
}

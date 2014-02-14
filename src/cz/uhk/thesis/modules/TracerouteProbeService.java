
package cz.uhk.thesis.modules;

import cz.uhk.thesis.interfaces.ProbeService;
import cz.uhk.thesis.interfaces.Probe;
import cz.uhk.thesis.core.Core;
import cz.uhk.thesis.core.Logger;
import cz.uhk.thesis.interfaces.DeviceObserver;
import cz.uhk.thesis.model.Device;
import cz.uhk.thesis.model.Parser;
import cz.uhk.thesis.interfaces.Stateful;
import cz.uhk.thesis.model.IcmpPacket;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
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
    private byte[] ip;
    
    public TracerouteProbeService(Core core, Probe probe)
    {
        this.probe = probe;
        this.core = core;
    }
    
    @Override
    public void packetParse(JPacket packet) {
        // function A - Obtain gateway MAC
        if(((TracerouteProbe)probe).useThisModuleObtainGatewayMac(packet)) {
            Ethernet e = packet.getHeader(new Ethernet());
            Device d = core.GetDeviceManager().getDevice(e.source());
            d.setIsGateway(true);
            if(IsInState(STATE_INITIAL)) {
                SetState(STATE_HAS_GATEWAY);
                Logger.Log2Console(this, "nastavuji stav "+STATE_HAS_GATEWAY);
            }
            core.getProbeLoader().NotifyAllModules();
        }
        // function B - Traceroute
        if(((TracerouteProbe)probe).useThisModuleTraceroute(packet)) {
            boolean requiredDestination = ((Ip4)packet.getHeader(new Ip4())).source() == GetTracerouteHostTestIp();
            int type = ((Icmp)packet.getHeader(new Icmp())).type();
            if(type == IcmpType.TIME_EXCEEDED_ID && !requiredDestination) { // vyprselo TTL a neni to cilova destinace
                Logger.Log2Console(this, "ano");
                tracerouteTtl++;
                TracerouteSend();
            } else if(requiredDestination || type == IcmpType.ECHO_REPLY_ID) { // je to cilova destinace nebo odpovedel nalezeno
                SetState(STATE_TRACEROUTE_DONE);
                Logger.Log2Console(this, "traceroute done");
            }
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
            Logger.Log2ConsoleError(this, ex);
        }
    }
    
    private void TracerouteSend()
    {
        if(IsInState(STATE_TRACEROUTE_DONE)) return;
        
        if((!IsInState(STATE_PROCESS_TRACEROUTE) || tracerouteTtl > 1)) {
            SetState(STATE_PROCESS_TRACEROUTE);
            Logger.Log2Console(this, "odesilam traceroute hop "+tracerouteTtl);
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
                    state_icmp += probe.getCore().getNetworkManager().SendPacket(activeDevice, icmp);
                    if(state_icmp == Pcap.OK) {
                        Logger.Log2Console(probe.GetModuleName(), "Traceroute Packet (hop "+icmp.TimeToLive()+") odeslán");
                    }
                }
            } catch (UnknownHostException ex) {
                Logger.Log2ConsoleError(this, ex);
            }
        }
    }

    @Override
    public void Notify() {
        if(IsInState(STATE_HAS_GATEWAY)) {
            tracerouteTtl = 1;
            TracerouteSend();
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
                Logger.Log2ConsoleError(this, ex);
            }
        }
        return ip;
    }
    
    public String GetTracerouteHostTestHostname()
    {
        // TODO: move to settings
        return "www.seznam.cz";
    }
    
}

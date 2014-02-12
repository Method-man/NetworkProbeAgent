
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

/**
 *
 * @author Filip Valenta
 */
public class TracerouteProbeService extends Stateful implements ProbeService, DeviceObserver {

    public static final int STATE_HAS_GATEWAY = 1;
    public static final int STATE_PROCESS_TRACEROUTE = 2;
    
    private final Probe probe;
    private final Core core;
    
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
        // TODO:
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
        if(!IsInState(STATE_PROCESS_TRACEROUTE)) {
            SetState(STATE_PROCESS_TRACEROUTE);
            Logger.Log2Console(this, "odesilam traceroute");
            try {
                Device gateway = core.GetDeviceManager().GetGateway();
                if(gateway != null) {
                    PcapIf activeDevice = core.getNetworkManager().getActiveDevice();
                    
                    IcmpPacket icmp = new IcmpPacket(core);
                    icmp.ethernetDestination(gateway.getMacAsByte());
                    icmp.ipSource(core.getNetworkManager().GetActiveDeviceIPasByte());
                    icmp.ipDestination(GetTracerouteHostTestIp());
                    icmp.TimeToLive(10);
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
            case STATE_HAS_GATEWAY:
            case STATE_PROCESS_TRACEROUTE:
            case STATE_INITIAL:
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
        byte[] ip = new byte[4];
        try {
            ip = InetAddress.getByName(GetTracerouteHostTestHostname()).getAddress();
        } catch (UnknownHostException ex) {
            Logger.Log2ConsoleError(this, ex);
        }
        return ip;
    }
    
    public String GetTracerouteHostTestHostname()
    {
        // TODO: move to settings
        return "www.seznam.cz";
    }
    
}

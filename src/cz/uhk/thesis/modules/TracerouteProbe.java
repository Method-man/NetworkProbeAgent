
package cz.uhk.thesis.modules;

import cz.uhk.thesis.interfaces.ProbeService;
import cz.uhk.thesis.interfaces.Probe;
import cz.uhk.thesis.core.Core;
import cz.uhk.thesis.core.Logger;
import java.util.Arrays;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Icmp.IcmpType;
import org.jnetpcap.protocol.network.Ip4;

/**
 *
 * @author Filip Valenta
 */
public class TracerouteProbe implements Probe {
    
    ProbeService probeService;
    Core core;
    
    public TracerouteProbe(Core core)
    {
        this.core = core;
    }
    
    @Override
    public Core getCore() {
        return core;
    }

    @Override
    public String GetModuleName() {
        return "Traceroute";
    }

    @Override
    public ProbeService GetProbeService() {
        return probeService;
    }

    @Override
    public boolean useThisModule(JPacket packet) {
        boolean use = false;
        use |= useThisModuleObtainGatewayMac(packet);
        use |= useThisModuleTraceroute(packet);
        return use;
    }
    
    /**
     * Is this packet form gateway (in obtain test)
     * 
     * @param packet
     * @return 
     */
    public boolean useThisModuleObtainGatewayMac(JPacket packet)
    {
        boolean use = false;
        if(packet.hasHeader(new Ip4())) { // 4 obtain gateway mac
            // source IP == origin host IP
            use |= Arrays.equals(
                       ((Ip4)packet.getHeader(new Ip4())).source(),
                       ((TracerouteProbeService)probeService).GetTracerouteHostTestIp()
                   );
            // ethernet source MAC != interface MAC
            use &= !Arrays.equals(
                        ((Ethernet)packet.getHeader(new Ethernet())).source(), 
                        core.getNetworkManager().GetActiveDeviceMACasByte()
                    );
            // service in initial state
            use &= ((TracerouteProbeService)probeService).IsInState(TracerouteProbeService.STATE_INITIAL);
        }
        return use;
    }
    
    public boolean useThisModuleTraceroute(JPacket packet)
    {
        boolean use = false;
        if(packet.hasHeader(new Icmp())) {
            Icmp icmp = packet.getHeader(new Icmp());
            switch(icmp.type()) {
                case IcmpType.TIME_EXCEEDED_ID: 
                case IcmpType.DESTINATION_UNREACHABLE_ID: 
                case IcmpType.ECHO_REPLY_ID: 
                {
                    use = true;
                } break;
                default: use = false; break;
            }
        }
        return use;
    }

    @Override
    public void InitBefore() {
        probeService = new TracerouteProbeService(getCore(), this);
    }

    @Override
    public void InitAfter() {

    }
    
}

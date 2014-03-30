
package org.hkfree.topoagent.modules;

import org.hkfree.topoagent.interfaces.Probe;
import org.hkfree.topoagent.core.Core;
import java.util.Arrays;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Icmp.IcmpType;
import org.jnetpcap.protocol.network.Ip4;

/**
 *
 * @author Filip Valenta
 */
public class TracerouteProbe extends Probe {
    
    public TracerouteProbe(Core core) {
        super(core);
    }

    @Override
    public String GetModuleName() {
        return "Traceroute";
    }

    @Override
    public boolean useThisModule(JPacket packet) {
        boolean use = false;
        use |= useThisModuleObtainGatewayMac(packet);
        use |= useThisModuleTraceroute(packet);
        
        // do not track icmp packets any more, those packets are probably for another (ping) module
        use &= !((TracerouteProbeService)probeService).IsInState(TracerouteProbeService.STATE_TRACEROUTE_DONE);
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
        
    }

    @Override
    public void InitAfter() {

    }

    @Override
    public String GetTcpdumpFilter() {
        return "icmp or host "+((TracerouteProbeService)probeService).GetTracerouteHostTestHostname();
    }
    
}

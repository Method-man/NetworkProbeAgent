
package cz.uhk.thesis.modules;

import cz.uhk.thesis.core.Core;
import cz.uhk.thesis.model.Device;
import cz.uhk.thesis.model.Parser;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.lan.Ethernet;

/**
 *
 * @author Filip Valenta
 */
public class TracerouteProbeService implements ProbeService {

    private final Probe probe;
    private final Core core;
    
    public TracerouteProbeService(Core core, Probe probe)
    {
        this.probe = probe;
        this.core = core;
    }
    
    @Override
    public void packetParse(JPacket packet) {
        // function A
        if(((TracerouteProbe)probe).useThisModuleObtainGatewayMac(packet)) {
            Ethernet e = packet.getHeader(new Ethernet());
            Device d = core.GetDeviceManager().getDevice(FormatUtils.mac(e.source()));
            d.setIsGateway(true);
            core.GetDeviceManager().deviceListChanged();
        }
    }

    @Override
    public void packetCompare(String ip, String mac, Parser parser) {
        
    }

    @Override
    public void packetCompare(String ip, String mac) {
        
    }

    @Override
    public void probeSend() {
        
        // DELETE:
            /* try {
            IcmpPacket t = new IcmpPacket(core);
            Logger.Log2ConsolePacket(t);
            } catch (UnknownHostException ex) {
            Logger.Log2ConsoleError(this, ex);
            } */

    }
    
}

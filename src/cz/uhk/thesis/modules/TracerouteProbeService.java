
package cz.uhk.thesis.modules;

import cz.uhk.thesis.core.Core;
import cz.uhk.thesis.core.Logger;
import cz.uhk.thesis.model.Parser;
import cz.uhk.thesis.model.IcmpPacket;
import org.jnetpcap.packet.JPacket;

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
        
    }

    @Override
    public void packetCompare(String ip, String mac, Parser parser) {
        
    }

    @Override
    public void packetCompare(String ip, String mac) {
        
    }

    @Override
    public void probeSend() {
        IcmpPacket t = new IcmpPacket(core);
        Logger.Log2ConsolePacket(t);
    }
    
    
    
}

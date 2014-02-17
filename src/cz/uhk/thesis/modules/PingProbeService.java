
package cz.uhk.thesis.modules;

import cz.uhk.thesis.core.Core;
import cz.uhk.thesis.interfaces.Probe;
import cz.uhk.thesis.interfaces.ProbeService;
import cz.uhk.thesis.model.Parser;
import org.jnetpcap.packet.JPacket;

/**
 *
 * @author Filip Valenta
 */
public class PingProbeService implements ProbeService {

    private final Probe probe;
    private final Core core;
    
    public PingProbeService(Core core, Probe probe)
    {
        this.probe = probe;
        this.core = core;
    }
    
    @Override
    public void packetParse(JPacket packet) {

    }

    @Override
    public void packetCompare(String ip, byte[] mac, Parser parser) {

    }

    @Override
    public void packetCompare(String ip, byte[] mac) {

    }

    @Override
    public void probeSend() {

    }
    
}

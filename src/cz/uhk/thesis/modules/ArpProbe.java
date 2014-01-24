/*
 * Modul pro sniffovani ARP packetu
 */

package cz.uhk.thesis.modules;

import cz.uhk.thesis.core.Core;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.protocol.JProtocol;

/**
 *
 * @author Filip Valenta
 */
public class ArpProbe implements Probe {
    
    ProbeService probeService;
    Core core;

    public ArpProbe(Core core) {
        this.core = core;
    }

    @Override
    public String GetModuleName() {
        return "ARP packets";
    }
    
    @Override
    public ProbeService GetProbeService() {
        return probeService;
    }
    
    @Override
    public boolean useThisModule(JPacket packet)
    {   
        return packet.hasHeader(JProtocol.ARP_ID);
    }

    @Override
    public void InitBefore() {
        probeService = new ArpProbeService(core, this);
    }
    
    @Override
    public void InitAfter() {

    }

    @Override
    public Core getCore() {
        return core;
    }
    
}

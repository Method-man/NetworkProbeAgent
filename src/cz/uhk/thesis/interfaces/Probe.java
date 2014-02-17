
package cz.uhk.thesis.interfaces;

import cz.uhk.thesis.core.Core;
import org.jnetpcap.packet.JPacket;

/**
 *
 * @author Filip Valenta
 */
public abstract class Probe {
    
    protected ProbeService probeService;
    protected Core core;
    
    public Probe(Core core) {
        this.core = core;
    }
    
    public ProbeService GetProbeService() {
        return probeService;
    }
    
    public void SetProbeService(ProbeService probeService) {
        this.probeService = probeService;
    }
    
    public abstract String GetModuleName();
    
    public abstract boolean useThisModule(JPacket packet);
    
    public abstract void InitBefore();
    
    public abstract void InitAfter();
    
}

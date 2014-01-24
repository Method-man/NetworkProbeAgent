
package cz.uhk.thesis.modules;

import cz.uhk.thesis.core.Core;
import org.jnetpcap.packet.JPacket;

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
        return false;
    }

    @Override
    public void InitBefore() {
        probeService = new TracerouteProbeService(getCore(), this);
    }

    @Override
    public void InitAfter() {
        
    }
    
    
    
}

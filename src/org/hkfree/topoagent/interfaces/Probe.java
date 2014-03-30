
package org.hkfree.topoagent.interfaces;

import org.hkfree.topoagent.core.Core;
import org.hkfree.topoagent.domain.ScheduleJobCrate;
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
    
    public String GetTcpdumpFilter() {
        return "ether proto ip";
    }
    
    public ScheduleJobCrate Schedule() {
        return null;
    }
    
}

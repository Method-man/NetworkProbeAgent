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

    public ProbeService getProbeService() {
        return probeService;
    }

    public void setProbeService(ProbeService probeService) {
        this.probeService = probeService;
    }

    public abstract String getModuleName();

    public abstract boolean useThisModule(JPacket packet);

    public abstract void initBefore();

    public abstract void initAfter();

    public String getTcpdumpFilter() {
        return "ether proto ip";
    }

    public ScheduleJobCrate schedule() {
        return null;
    }

}

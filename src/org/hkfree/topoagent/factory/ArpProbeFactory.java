package org.hkfree.topoagent.factory;

import org.hkfree.topoagent.core.Core;
import org.hkfree.topoagent.interfaces.Probe;
import org.hkfree.topoagent.interfaces.ProbeFactory;
import org.hkfree.topoagent.module.protocol.ArpProbe;
import org.hkfree.topoagent.module.protocol.ArpProbeService;

/**
 *
 * @author Filip Valenta
 */
public class ArpProbeFactory implements ProbeFactory {

    private final Core core;

    public ArpProbeFactory(Core core) {
        this.core = core;
    }

    @Override
    public Probe getProbe() {
        ArpProbe ap = new ArpProbe(core);
        ap.setProbeService(new ArpProbeService(core, ap));
        return ap;
    }

}

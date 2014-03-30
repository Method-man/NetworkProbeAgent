
package org.hkfree.topoagent.factories;

import org.hkfree.topoagent.core.Core;
import org.hkfree.topoagent.interfaces.Probe;
import org.hkfree.topoagent.interfaces.ProbeFactory;
import org.hkfree.topoagent.modules.ArpProbe;
import org.hkfree.topoagent.modules.ArpProbeService;

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
        ap.SetProbeService(new ArpProbeService(core, ap));
        return ap;
    }
    
}


package cz.uhk.thesis.factories;

import cz.uhk.thesis.core.Core;
import cz.uhk.thesis.interfaces.Probe;
import cz.uhk.thesis.interfaces.ProbeFactory;
import cz.uhk.thesis.modules.ArpProbe;
import cz.uhk.thesis.modules.ArpProbeService;

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

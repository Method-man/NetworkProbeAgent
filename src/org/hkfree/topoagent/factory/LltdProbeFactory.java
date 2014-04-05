/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.hkfree.topoagent.factory;

import org.hkfree.topoagent.core.Core;
import org.hkfree.topoagent.interfaces.Probe;
import org.hkfree.topoagent.interfaces.ProbeFactory;
import org.hkfree.topoagent.module.protocol.LltdProbe;
import org.hkfree.topoagent.module.protocol.LltdProbeService;

/**
 *
 * @author Filip Valenta
 */
public class LltdProbeFactory implements ProbeFactory {
    
    private final Core core;
    
    public LltdProbeFactory(Core core) {
        this.core = core;
    }
    
    @Override
    public Probe getProbe() {
        LltdProbe ap = new LltdProbe(core);
        ap.SetProbeService(new LltdProbeService(core, ap));
        return ap;
    }
    
}

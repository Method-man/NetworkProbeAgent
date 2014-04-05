/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.hkfree.topoagent.factory;

import org.hkfree.topoagent.core.Core;
import org.hkfree.topoagent.interfaces.Probe;
import org.hkfree.topoagent.interfaces.ProbeFactory;
import org.hkfree.topoagent.module.protocol.PingProbe;
import org.hkfree.topoagent.module.protocol.PingProbeService;

/**
 *
 * @author Filip Valenta
 */
public class PingProbeFactory implements ProbeFactory {
    
    private final Core core;
    
    public PingProbeFactory(Core core) {
        this.core = core;
    }
    
    @Override
    public Probe getProbe() {
        PingProbe ap = new PingProbe(core);
        ap.SetProbeService(new PingProbeService(core, ap));
        return ap;
    }
    
}

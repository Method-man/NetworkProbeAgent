/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hkfree.topoagent.factory;

import org.hkfree.topoagent.core.Core;
import org.hkfree.topoagent.interfaces.Probe;
import org.hkfree.topoagent.interfaces.ProbeFactory;
import org.hkfree.topoagent.module.protocol.TracerouteProbe;
import org.hkfree.topoagent.module.protocol.TracerouteProbeService;

/**
 *
 * @author Filip Valenta
 */
public class TracerouteProbeFactory implements ProbeFactory {

    private final Core core;

    public TracerouteProbeFactory(Core core) {
        this.core = core;
    }

    @Override
    public Probe getProbe() {
        TracerouteProbe ap = new TracerouteProbe(core);
        ap.setProbeService(new TracerouteProbeService(core, ap));
        return ap;
    }

}

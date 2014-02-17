/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.uhk.thesis.factories;

import cz.uhk.thesis.core.Core;
import cz.uhk.thesis.interfaces.Probe;
import cz.uhk.thesis.interfaces.ProbeFactory;
import cz.uhk.thesis.modules.TracerouteProbe;
import cz.uhk.thesis.modules.TracerouteProbeService;

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
        ap.SetProbeService(new TracerouteProbeService(core, ap));
        return ap;
    }
    
}

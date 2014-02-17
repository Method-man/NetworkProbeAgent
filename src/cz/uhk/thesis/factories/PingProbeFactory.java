/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.uhk.thesis.factories;

import cz.uhk.thesis.core.Core;
import cz.uhk.thesis.interfaces.Probe;
import cz.uhk.thesis.interfaces.ProbeFactory;
import cz.uhk.thesis.modules.PingProbe;
import cz.uhk.thesis.modules.PingProbeService;

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

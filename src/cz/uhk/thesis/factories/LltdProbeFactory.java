/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.uhk.thesis.factories;

import cz.uhk.thesis.core.Core;
import cz.uhk.thesis.interfaces.Probe;
import cz.uhk.thesis.interfaces.ProbeFactory;
import cz.uhk.thesis.modules.LltdProbe;
import cz.uhk.thesis.modules.LltdProbeService;

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

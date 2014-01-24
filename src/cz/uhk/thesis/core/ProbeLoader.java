/*
 * Zavadec modulu
 */

package cz.uhk.thesis.core;

import cz.uhk.thesis.modules.ArpProbe;
import cz.uhk.thesis.modules.LLTDProbe;
import cz.uhk.thesis.modules.Probe;
import cz.uhk.thesis.modules.TracerouteProbe;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Filip Valenta
 */
public class ProbeLoader {
    
    private final List<Probe> probes = new ArrayList<>();
    private final Core core;
    
    public ProbeLoader(Core core)
    {
        this.core = core;
        
        probes.add(new ArpProbe(this.core));
        probes.add(new LLTDProbe(this.core));
        probes.add(new TracerouteProbe(this.core));
    }
    
    public List<Probe> GetProbes()
    {
        return probes;
    }
    
    public void InitBeforeProbes()
    {
        for(Probe p: probes) {
            p.InitBefore();
            System.out.println("inicializuji modul: "+p.GetModuleName());
        }
    }
    
    public void InitAfterProbes()
    {
        for(Probe p: probes) {
            p.InitAfter();
            Logger.Log2Console(p.GetModuleName(), "spouštím init after");
            p.GetProbeService().probeSend();
        }
    }
    
}

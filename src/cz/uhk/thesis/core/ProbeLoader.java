/*
 * Zavadec modulu
 */

package cz.uhk.thesis.core;

import cz.uhk.thesis.factories.ArpProbeFactory;
import cz.uhk.thesis.factories.LltdProbeFactory;
import cz.uhk.thesis.factories.PingProbeFactory;
import cz.uhk.thesis.factories.TracerouteProbeFactory;
import cz.uhk.thesis.interfaces.DeviceObserver;
import cz.uhk.thesis.interfaces.Probe;
import cz.uhk.thesis.interfaces.ProbeFactory;
import cz.uhk.thesis.interfaces.Stateful;
import cz.uhk.thesis.modules.AdapterService;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Filip Valenta
 */
public class ProbeLoader extends Stateful {
    
    private final List<Probe> probes = new ArrayList<>();
    private final Core core;
    
    private static final int APP_STATE_IN_PROCESS = 1;
    private static final int APP_STATE_TRACEROUTE_MODULE_OK = 2;
    
    public ProbeLoader(Core core)
    {
        this.core = core;
        ProbeFactory pf = new ArpProbeFactory(core);
        probes.add(pf.getProbe());
        pf = new LltdProbeFactory(core);
        probes.add(pf.getProbe());
        pf = new TracerouteProbeFactory(core);
        probes.add(pf.getProbe());
        pf = new PingProbeFactory(core);
        probes.add(pf.getProbe());
    }
    
    public List<Probe> GetProbes()
    {
        return probes;
    }
    
    public void InitBeforeProbes()
    {
        for(Probe p: probes) {
            p.InitBefore();
            core.getNetworkManager().Add2Filter(p.GetTcpdumpFilter());
            LogService.Log2Console(this, "inicializuji modul: "+p.GetModuleName());
        }
    }
    
    public void InitAfterProbes()
    {
        for(Probe p: probes) {
            p.InitAfter();
            LogService.Log2Console(p.GetModuleName(), "spouštím init after");
            p.GetProbeService().probeSend();
        }
    }
    
    /**
     * Notify all modules and save info to log
     */
    public void NotifyAllModules()
    {
        core.GetDeviceManager().LogInfo();
        for(Probe p: probes) {
            if(p.GetProbeService() instanceof DeviceObserver) {
                ((DeviceObserver)p.GetProbeService()).Notify();
            }
        }
    }

    @Override
    public void SetState(int state) {
        // TODO: celkove stavy aplikace, nikoli modulu... 
        // napriklad has gateway se presune sem, traceroute se presune sem
        
        // TODO: if in state all done > AdapterService > send XML 2 server
        if(state == APP_STATE_TRACEROUTE_MODULE_OK) {
            
        }
    }
    
    public AdapterService GetAdapterService()
    {
        return new AdapterService();
    }
    
}

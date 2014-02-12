/*
 * Modul pro sniffovani LLTD packetu
 */

package cz.uhk.thesis.modules;

import cz.uhk.thesis.interfaces.Probe;
import cz.uhk.thesis.core.Core;
import cz.uhk.thesis.model.LltdHeader;
import cz.uhk.thesis.core.Logger;
import cz.uhk.thesis.modules.LLTDProbeService;
import cz.uhk.thesis.interfaces.ProbeService;
import cz.uhk.thesis.interfaces.Probe;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JRegistry;
import org.jnetpcap.packet.RegistryHeaderErrors;

/**
 *
 * @author Filip Valenta
 */
public class LLTDProbe implements Probe {
    
    LLTDProbeService probeService;
    Core core;

    @Override
    public Core getCore() {
        return core;
    }

    public LLTDProbe(Core core) {
        this.core = core;
        probeService = new LLTDProbeService(core, this);
    }

    @Override
    public String GetModuleName() {
        return "LLTD packets";
    }

    @Override
    public ProbeService GetProbeService() {
        return probeService;
    }
    
    @Override
    public boolean useThisModule(JPacket packet) {
        return packet.hasHeader(new LltdHeader());
    }
    
    @Override
    public void InitBefore() {
        try {
            LltdHeader.LLTD_ID = JRegistry.register(LltdHeader.class);
        } catch (RegistryHeaderErrors ex) {
            Logger.Log2ConsoleError(this, ex);
        }
    }
    
    @Override
    public void InitAfter() {
        
    }
    
    
}

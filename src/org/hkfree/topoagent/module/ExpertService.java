
package org.hkfree.topoagent.module;

import java.awt.TrayIcon;
import org.hkfree.topoagent.core.Core;

/**
 *
 * @author Filip Valenta
 */
public class ExpertService {
    
    private Core core;
    
    public ExpertService(Core core)
    {
        this.core = core;
    }
    
    /**
     * SET OF RULES
     */
    
    public void ShowNoNetworkConnection() {
        core.getTrayService().ShowMessage("Chyba 01!", "Žádné síťové připojení", TrayIcon.MessageType.ERROR);
    }
    
    public void ShowRemoteHostUnavailable() {
        core.getTrayService().ShowMessage("Chyba 02!", "Vzdálený server není dostupný", TrayIcon.MessageType.ERROR);
    }
    
    public void ShowTracerouteNoRoute() {
        core.getTrayService().ShowMessage("Chyba 03!", "Defaultní brána není dostupná", TrayIcon.MessageType.ERROR);
    }
    
}

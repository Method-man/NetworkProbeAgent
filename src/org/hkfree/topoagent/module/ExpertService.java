package org.hkfree.topoagent.module;

import java.awt.TrayIcon;
import java.util.ArrayList;
import java.util.List;
import org.hkfree.topoagent.core.Core;
import org.hkfree.topoagent.core.LogService;

/**
 *
 * @author Filip Valenta
 */
public class ExpertService {

    private Core core;

    private List<Integer> status = new ArrayList<>();

    public static final int STATUS_OK = 1;
    public static final int STATUS_PROBLEM = 0;

    public ExpertService(Core core) {
        this.core = core;
    }

    /**
     * SET OF NEGATIVE RULES
     */
    public void showNoNetworkConnection() {
        core.getTrayService().showMessage("Chyba 01!", "Žádné síťové připojení", TrayIcon.MessageType.ERROR);
        core.getTrayService().setImageError();
    }

    public void showRemoteHostUnavailable() {
        core.getTrayService().showMessage("Chyba 02!", "Vzdálený server není dostupný", TrayIcon.MessageType.ERROR);
        core.getTrayService().setImageError();
    }

    public void showTracerouteNoRoute() {
        core.getTrayService().showMessage("Chyba 03!", "Defaultní brána není dostupná", TrayIcon.MessageType.ERROR);
        core.getTrayService().setImageError();
    }

    /**
     * SET OF NEUTRAL RULES
     */
    public void showTraceIsBad() {
        if (setStatus(false)) {
            core.getTrayService().showMessage("Varování 01!", "Detekován možný problém s průchodností sítě", TrayIcon.MessageType.WARNING);
            core.getTrayService().setImageWarning();
        }
    }

    public void showNoPublicIpServiceAvailable() {
        if (setStatus(false)) {
            core.getTrayService().showMessage("Varování 02!", "Webová služba veřejné IP není dostupná", TrayIcon.MessageType.WARNING);
            core.getTrayService().setImageWarning();
        }
    }

    /**
     * SET OF POSITIVE RULES
     */
    public void showTraceIsFeasible() {
        if (setStatus(true)) {
            core.getTrayService().showMessage("Informace", "Sít je průchodná", TrayIcon.MessageType.INFO);
            core.getTrayService().setImageOK();
        }
    }

    /**
     * Set status and returns if show message
     *
     * @param allOk
     * @return
     */
    private boolean setStatus(boolean allOk) {
        for (int statusint : status) {
            LogService.Log2Console(this, "status - " + String.valueOf(statusint));
        }

        if (status.isEmpty()) {
            status.add(allOk ? STATUS_OK : STATUS_PROBLEM);
        }
        else {
            // prisel stejny stav jako mame ve fronte
            if (status.get(0) == (allOk ? STATUS_OK : STATUS_PROBLEM)) {
                // zapiseme stav pouze pokud neni vic jak 3 aby to neslo do nekonecna
                if (status.size() < 3) {
                    status.add(allOk ? STATUS_OK : STATUS_PROBLEM);
                }
                if (status.size() == 3) { // mame prave 3 stejne stavy
                    return true; // zobrazit hlasku
                }
            }
            // prisel jiny stav nez prave pocitame, mazeme a pridavame ho
            else {
                status.clear();
                status.add(allOk ? STATUS_OK : STATUS_PROBLEM);
            }
        }

        return false; // nezobrazovat

    }

}

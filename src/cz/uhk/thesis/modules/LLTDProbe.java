/*
 * Modul pro sniffovani LLTD packetu
 */

package cz.uhk.thesis.modules;

import cz.uhk.thesis.core.Core;
import cz.uhk.thesis.model.LltdHeader;
import cz.uhk.thesis.core.LogService;
import cz.uhk.thesis.interfaces.Probe;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JRegistry;
import org.jnetpcap.packet.RegistryHeaderErrors;

/**
 *
 * @author Filip Valenta
 */
public class LltdProbe extends Probe {
    
    public LltdProbe(Core core) {
        super(core);
    }

    @Override
    public String GetModuleName() {
        return "LLTD packets";
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
            LogService.Log2ConsoleError(this, ex);
        }
    }
    
    @Override
    public void InitAfter() {
        
    }

    @Override
    public String GetTcpdumpFilter() {
        return "ether proto 0x"+Integer.toHexString(LltdHeader.ETHERNET_HEADER_LLTD);
    }
    
}

/*
 * Modul pro sniffovani ARP packetu
 */

package cz.uhk.thesis.modules;

import cz.uhk.thesis.interfaces.Probe;
import cz.uhk.thesis.core.Core;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.protocol.JProtocol;

/**
 *
 * @author Filip Valenta
 */
public class ArpProbe extends Probe {

    public ArpProbe(Core core) {
        super(core);
    }

    @Override
    public String GetModuleName() {
        return "ARP packets";
    }
    
    @Override
    public boolean useThisModule(JPacket packet)
    {   
        return packet.hasHeader(JProtocol.ARP_ID);
    }

    @Override
    public void InitBefore() {
        
    }

    @Override
    public void InitAfter() {
        
    }
    
}

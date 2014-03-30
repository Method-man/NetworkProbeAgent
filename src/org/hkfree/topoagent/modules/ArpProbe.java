/*
 * Modul pro sniffovani ARP packetu
 */

package org.hkfree.topoagent.modules;

import org.hkfree.topoagent.interfaces.Probe;
import org.hkfree.topoagent.core.Core;
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

    @Override
    public String GetTcpdumpFilter() {
        return "arp";
    }

}

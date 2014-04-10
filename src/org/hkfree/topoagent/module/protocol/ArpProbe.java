/*
 * Modul pro sniffovani ARP packetu
 */
package org.hkfree.topoagent.module.protocol;

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
    public String getModuleName() {
        return "ARP packets";
    }

    @Override
    public boolean useThisModule(JPacket packet) {
        return packet.hasHeader(JProtocol.ARP_ID);
    }

    @Override
    public void initBefore() {

    }

    @Override
    public void initAfter() {

    }

    @Override
    public String getTcpdumpFilter() {
        return "arp";
    }

}

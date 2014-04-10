/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hkfree.topoagent.module.protocol;

import org.hkfree.topoagent.core.Core;
import org.hkfree.topoagent.interfaces.Probe;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.protocol.network.Icmp;

/**
 *
 * @author Filip Valenta
 */
public class PingProbe extends Probe {

    public PingProbe(Core core) {
        super(core);
    }

    @Override
    public String getModuleName() {
        return "Ping";
    }

    @Override
    public boolean useThisModule(JPacket packet) {
        return packet.hasHeader(new Icmp());
    }

    @Override
    public void initBefore() {

    }

    @Override
    public void initAfter() {

    }

    @Override
    public String getTcpdumpFilter() {
        return "icmp";
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hkfree.topoagent.module.protocol;

import java.net.UnknownHostException;
import jcifs.netbios.NbtAddress;
import org.hkfree.topoagent.core.Core;
import org.hkfree.topoagent.core.LogService;
import org.hkfree.topoagent.interfaces.Parser;
import org.hkfree.topoagent.interfaces.Probe;
import org.hkfree.topoagent.interfaces.ProbeService;
import org.jnetpcap.packet.JPacket;

/**
 *
 * @author Filip Valenta
 */
public class NetBIOSProbeService implements ProbeService {

    private final Probe probe;
    private final Core core;

    public NetBIOSProbeService(Core core, NetBIOSProbe probe) {
        this.probe = probe;
        this.core = core;
    }

    @Override
    public void packetParse(JPacket packet) {
    }

    @Override
    public void packetCompare(String ip, byte[] mac, Parser parser) {
    }

    @Override
    public void packetCompare(String ip, byte[] mac) {
    }

    @Override
    public void probeSend() {
    }

    /**
     * Get FIRST netbios name assignet to IP
     *
     * @param ip
     * @return
     */
    public String GetNetBIOSName(String ip) {
        String hostname = "";
        try {
            for (NbtAddress na : NbtAddress.getAllByAddress(ip)) {
                if (!na.firstCalledName().equals("")) {
                    hostname = na.firstCalledName();
                    break;
                }
            }
        } catch (UnknownHostException ex) {
            // LogService.Log2ConsoleError(this, ex);
        }
        return hostname;
    }

}

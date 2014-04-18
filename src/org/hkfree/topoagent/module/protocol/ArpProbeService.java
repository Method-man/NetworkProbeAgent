package org.hkfree.topoagent.module.protocol;

import org.hkfree.topoagent.interfaces.ProbeService;
import org.hkfree.topoagent.interfaces.Probe;
import org.hkfree.topoagent.core.Core;
import org.hkfree.topoagent.core.LogService;
import org.hkfree.topoagent.interfaces.Parser;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Arp;

/**
 *
 * @author Filip Valenta
 */
public class ArpProbeService implements ProbeService {

    private final Probe probe;
    private final Core core;

    public ArpProbeService(Core core, Probe probe) {
        this.probe = probe;
        this.core = core;
    }

    /**
     *
     * @param packet
     *
     */
    @Override
    public void packetParse(JPacket packet) {
        Arp arpHeader = new Arp();
        packet.getHeader(arpHeader);
        String type = "";

        Arp.OpCode opCode = arpHeader.operationEnum();
        if (opCode == Arp.OpCode.REQUEST) {
            type = "ARP REQUEST";
        }
        else if (opCode == Arp.OpCode.REPLY) {
            type = "ARP REPLY";
        }

        LogService.log2Console(this, probe.getModuleName() + " module: " + type + ": "
                + FormatUtils.ip(arpHeader.spa()) + " to "
                + FormatUtils.ip(arpHeader.tpa()));

        packetCompare(FormatUtils.ip(arpHeader.spa()), arpHeader.sha());
        packetCompare(FormatUtils.ip(arpHeader.tpa()), arpHeader.tha());
    }

    @Override
    public void packetCompare(String ip, byte[] mac) {
        if (core.getNetworkManager().isValidIp(ip) && core.getNetworkManager().isValidMac(mac)) {
            core.getDeviceManager().getDevice(mac).setIP(ip);
            core.getProbeLoader().notifyAllModules();
        }
    }

    @Override
    public void packetCompare(String ip, byte[] mac, Parser parser) {

    }

    @Override
    public void probeSend() {

    }

}

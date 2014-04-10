package org.hkfree.topoagent.module.protocol;

import org.hkfree.topoagent.core.Core;
import org.hkfree.topoagent.interfaces.Probe;
import org.hkfree.topoagent.interfaces.ProbeService;
import org.hkfree.topoagent.domain.Device;
import org.hkfree.topoagent.domain.LltdHeader;
import org.hkfree.topoagent.domain.LltdHelloSubHeaderParser;
import org.hkfree.topoagent.domain.Parser;
import org.jnetpcap.packet.JPacket;

/**
 *
 * @author Filip Valenta
 */
public class LltdProbeService implements ProbeService {

    private final Probe probe;
    private final Core core;

    public LltdProbeService(Core core, Probe probe) {
        this.probe = probe;
        this.core = core;
    }

    @Override
    public void packetParse(JPacket packet) {
        if (packet.hasHeader(new LltdHeader())) {
            packet.scan(LltdHeader.LLTD_ID);
            LltdHeader lltdHeader = packet.getHeader(new LltdHeader());

            // is LLTD hello response packet
            if (lltdHeader.function() == LltdHeader.HEADER_FUNCTION_HELLO) {
                byte[] subheader = lltdHeader.getByteArray(
                        LltdHelloSubHeaderParser.OFFSET_SUBHEADER_HELLO,
                        packet.size() - LltdHelloSubHeaderParser.OFFSET_SUBHEADER_HELLO
                );
                LltdHelloSubHeaderParser helloParser = new LltdHelloSubHeaderParser(subheader);
                packetCompare(helloParser.getIpv4(), lltdHeader.source(), helloParser);
            }
        }
    }

    @Override
    public void packetCompare(String ip, byte[] mac) {

    }

    // TODO: udelat privatni !!!
    @Override
    public void packetCompare(String ip, byte[] mac, Parser parser) {
        if (core.getNetworkManager().isValidIp(ip) && core.getNetworkManager().isValidMac(mac)) {
            /**
             * TODO: skutecne nastavit ? zkontrolovat jaky zaznam existuje a musi se shodovat
             */
            core.getDeviceManager().getDevice(mac).setIP(ip);

            LltdHelloSubHeaderParser p = (LltdHelloSubHeaderParser) parser;

            core.getDeviceManager().getDevice(mac).setInfo(Device.DEVICE_LINK_SPEED, p.getLinkSpeed());
            core.getDeviceManager().getDevice(mac).setInfo(Device.DEVICE_HOST_ID, p.getHostIdMacAddress());
            core.getDeviceManager().getDevice(mac).setInfo(Device.DEVICE_PHYSICAL_MEDIUM, p.getPhysicalMedium());
            core.getDeviceManager().getDevice(mac).setInfo(Device.DEVICE_WIRELESS_MODE, p.getWirelessMode());
            core.getDeviceManager().getDevice(mac).setInfo(Device.DEVICE_IPV6, p.getIpv6());
            core.getDeviceManager().getDevice(mac).setInfo(Device.DEVICE_MACHINE_NAME, p.getMachineName());
            core.getDeviceManager().getDevice(mac).setInfo(Device.DEVICE_BSSID, p.getBSSID());

            core.getProbeLoader().notifyAllModules();

            // TODO: prozatim odesilame vzdy, potom se to ale bude odesilat jen pri presne prilezitosti
            core.getAdapterService().serverXmlSend(core);
        }
    }

    @Override
    public void probeSend() {

    }

}

package org.hkfree.topoagent.domain;

import org.hkfree.topoagent.core.Core;
import java.net.UnknownHostException;
import org.jnetpcap.protocol.JProtocol;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Ip4;

/**
 * IcmpPacket
 *
 * @source: http://tools.ietf.org/html/rfc792
 * @source: https://github.com/mgodave/Jpcap/blob/master/sample/Traceroute.java
 * @source: http://cs.wikipedia.org/wiki/ICMP
 *
 * @author Filip Valenta
 */
public class IcmpPacket extends Packet {

    public static final int PACKET_ICMP_SIZE = 74;
    
    public static final int PACKET_HEADER_ICMP_IDENTIFIER = 5;
    public static final int PACKET_HEADER_ICMP_SEQUENCE_NUMBER = 7;

    private Ethernet ethernetHeader;
    private Ip4 ip4;
    private Icmp icmpHeader;

    public IcmpPacket(Core c) throws UnknownHostException {
        super(Ip4.ID, new byte[PACKET_ICMP_SIZE]);

        ethernetHeader = getEthernet();

        this.setUByte(14, 0x40 | 0x05); // length 20, version 5
        ethernetHeader.source(c.getNetworkManager().getActiveDeviceMACasByte());

        this.scan(JProtocol.ETHERNET_ID);
        ip4 = getHeader(new Ip4());
        ip4.ttl(1);
        ip4.type(Ip4.Ip4Type.ICMP);
        ip4.length(PACKET_ICMP_SIZE - ethernetHeader.size());

        this.scan(JProtocol.ETHERNET_ID);
        icmpHeader = getHeader(new Icmp());
        icmpHeader.type(Icmp.IcmpType.ECHO_REQUEST_ID);
        icmpHeader.code(0x00);

        this.recalculateAllChecksums();

    }

    /**
     * Set gateway MAC
     *
     * @param destination
     */
    public void ethernetDestination(byte[] destination) {
        ethernetHeader.destination(destination);
    }

    public void ipSource(byte[] source) {
        ip4.source(source);
    }

    public void ipDestination(byte[] destination) {
        ip4.destination(destination);
    }

    public void timeToLive(int time) {
        ip4.ttl(time);
    }

    public int timeToLive() {
        return ip4.ttl();
    }

    public void sequenceNumber(int sequence) {
        icmpHeader.setUByte(PACKET_HEADER_ICMP_SEQUENCE_NUMBER, sequence);
    }

    public void identifier(int id) {
        icmpHeader.setUByte(PACKET_HEADER_ICMP_IDENTIFIER, id);
    }

}

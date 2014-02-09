
package cz.uhk.thesis.model;

import cz.uhk.thesis.core.Core;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.jnetpcap.protocol.JProtocol;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Ip4;

/**
 * IcmpPacket
 * 
 * @source: http://tools.ietf.org/html/rfc792
 * 
 * @author Filip Valenta
 */
public class IcmpPacket extends Packet {

    public static final int PACKET_ICMP_SIZE = 100;
    
    public IcmpPacket(Core c) throws UnknownHostException {
        super(Ip4.ID, new byte[PACKET_ICMP_SIZE]);
        
        Ethernet ethernetHeader = GetEthernet();
        
        this.setUByte(14, 0x40 | 0x05); // length 20, version 5
        ethernetHeader.source(c.getNetworkManager().GetActiveDeviceMACasByte());
        
        // obtain MAC address of the default gateway
        InetAddress pingAddr = InetAddress.getByName("www.seznam.cz");
        
        this.scan(JProtocol.ETHERNET_ID);
        Ip4 ip4 = getHeader(new Ip4());
        ip4.ttl(1);
        ip4.type(Ip4.Ip4Type.ICMP);
        ip4.length(PACKET_ICMP_SIZE - ethernetHeader.size());
        
        this.scan(JProtocol.ETHERNET_ID);
        Icmp icmpHeader = getHeader(new Icmp());
        icmpHeader.type(Icmp.IcmpType.ECHO_REQUEST_ID);
        icmpHeader.code(0x00);
        
        this.recalculateAllChecksums();

        /**
         * TODO:
         * 
         * https://github.com/mgodave/Jpcap/blob/master/sample/Traceroute.java
         * 
         * http://jnetpcap.com/examples/subheaders
         * http://jnetpcap.com/docs/javadocs/jnetpcap-1.4/index.html
         * http://cs.wikipedia.org/wiki/ICMP
         * http://tools.ietf.org/html/rfc792
         * http://en.wikipedia.org/wiki/IPv4#Protocol
         */
    }
    
    
}

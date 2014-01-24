
package cz.uhk.thesis.model;

import org.jnetpcap.packet.JMemoryPacket;
import org.jnetpcap.protocol.JProtocol;
import org.jnetpcap.protocol.lan.Ethernet;

/**
 *
 * @author Filip Valenta
 */
public abstract class Packet extends JMemoryPacket {

    private final Ethernet headerEthernet;
    // private final Ip4 headerIp4;
    // private final Tcp headerTcp;
    
    public Packet(int protocolID, byte[] packetdata) {
    
        super(protocolID, packetdata);
        setUShort(12, 0x0800);
        scan(JProtocol.ETHERNET_ID);
        headerEthernet = getHeader(new Ethernet());
        
        recalculateAllChecksums();
        
    }
    
    public Packet(int protocolID, String hexdump) {
        
        super(protocolID, hexdump);
        
        setUShort(12, 0x0800);
        scan(JProtocol.ETHERNET_ID);
        headerEthernet = getHeader(new Ethernet());
        
        scan(JProtocol.ETHERNET_ID);
        
        recalculateAllChecksums();
        
    }
    
    public Ethernet GetEthernet()
    {
        return headerEthernet;
    }
    
}

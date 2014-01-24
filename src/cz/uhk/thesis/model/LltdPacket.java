/*
 * 
 */

package cz.uhk.thesis.model;

import cz.uhk.thesis.core.Core;
import cz.uhk.thesis.core.NetworkManager;

/**
 * Common LLTD packet
 * 
 * @author Filip Valenta
 */
public abstract class LltdPacket extends Packet {
    
    public static final int PACKET_SITE = 60;
    
    LltdHeader lltdHeader;
    
    public LltdPacket(Core c, byte version, byte service, byte reserved, byte function) {
        super(LltdHeader.LLTD_ID, new byte[PACKET_SITE]);
        scan(LltdHeader.LLTD_ID);
        
        lltdHeader = new LltdHeader();
        
        lltdHeader = getHeader(lltdHeader);
        
        lltdHeader.type((short)LltdHeader.ETHERNET_HEADER_LLTD);
        
        lltdHeader.source(c.getNetworkManager().GetActiveDeviceMACasByte());
        lltdHeader.destination(NetworkManager.BROADCAST_MAC_ADDRESS);
        
        /**
         * demultiplex header
         */
        lltdHeader.version(version);
        lltdHeader.service(service);
        lltdHeader.reserved(reserved);
        lltdHeader.function(function);

        /**
         * base header
         */
        lltdHeader.sourceReal(c.getNetworkManager().GetActiveDeviceMACasByte());
        lltdHeader.destinationReal(NetworkManager.BROADCAST_MAC_ADDRESS);
        
        lltdHeader.xid((short)0x0000);
        
    }  
    
    public void setXid(short xid)
    {
        lltdHeader.xid(xid);
    }
    
}

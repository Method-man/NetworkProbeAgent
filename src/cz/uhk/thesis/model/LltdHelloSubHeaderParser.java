
package cz.uhk.thesis.model;

import java.nio.ByteBuffer;
import java.util.Arrays;
import org.jnetpcap.packet.format.FormatUtils;

/**
 *
 * @author Filip Valenta
 */
public class LltdHelloSubHeaderParser implements Parser {
    
    public static final int OFFSET_SUBHEADER_HELLO = 46;
    
    public static final byte TYPE_HOST_ID = 0x01;
    public static final byte TYPE_PHYSICAL_MEDIUM = 0x03;
    public static final byte TYPE_WIRELESS_MODE = 0x04;
    public static final byte TYPE_IPV4 = 0x07;
    public static final byte TYPE_IPV6 = 0x08;
    public static final byte TYPE_LINK_SPEED = 0x0c;
    
    public static final byte LEN_HOST_ID = 0x06;
    public static final byte LEN_PHYSICAL_MEDIUM = 0x04;
    public static final byte LEN_WIRELESS_MODE = 0x01;
    public static final byte LEN_IPV4 = 0x04;
    public static final byte LEN_IPV6 = 0x10;
    public static final byte LEN_LINK_SPEED = 0x04;
    
    byte[] headerData;
    
    private byte[] bMac             = new byte[LEN_HOST_ID];
    private byte[] bIpv4            = new byte[LEN_IPV4];
    private byte[] bIpv6            = new byte[LEN_IPV6];
    private byte[] bLinkSpeed       = new byte[LEN_LINK_SPEED];
    
    private byte bPhysicalMedium    = 0x00;
    private byte bWirelessMode      = 0x00;
    
    public LltdHelloSubHeaderParser(byte[] data)
    {
        this.headerData = data;
        parse();
    }
    
    /**
     * Returns 1. element HOST ID MAC
     * 
     * @return 
     */
    public String getHostIdMacAddress()
    {
        return FormatUtils.mac(bMac);
    }
    
    /**
     * Returns 3. element HOST ID MAC
     * 
     * @return 
     */
    public String getPhysicalMedium()
    {
        String sPhysicalMedium;
        switch (bPhysicalMedium) {
            case 0x47:
                sPhysicalMedium = "IEEE 802.11";
                break;
            case 0x06:
                sPhysicalMedium = "Ethernet";
                break;
            default:
                sPhysicalMedium = String.valueOf(bPhysicalMedium);
                break;
        }
        return sPhysicalMedium;
    }
    
    /**
     * Returns 4. element WIRELESS MODE
     * 
     * @return 
     */
    public String getWirelessMode()
    {
        String sWirelessMode;
        switch (bWirelessMode) {
            case 0x00:
                sWirelessMode = "802.11 IBBS or ad-hoc režim";
                break;
            case 0x01:
                sWirelessMode = "802.11 infrastructure režim";
                break;
            default:
                sWirelessMode = String.valueOf(bWirelessMode);
                break;
        }
        return sWirelessMode;
    }
    
    /**
     * Returns 7. element IPv4
     * 
     * @return 
     */
    public String getIpv4()
    {
        return FormatUtils.ip(bIpv4);
    }
    
    /**
     * Returns 8. element IPv6
     * 
     * @return 
     */
    public String getIpv6()
    {
        return FormatUtils.asStringIp6(bIpv6, true);
    }
    
    /**
     * Returns 1. element LINK SPEED
     * 
     * @return 
     */
    public String getLinkSpeed()
    {
        return String.valueOf(ByteBuffer.wrap(bLinkSpeed).getInt()/1000000) + " Mbit";
    }
    
    private void parse()
    {
        // Logger.Log2Hex(headerData);
        
        byte bActualBlock = 0x00; // safe data reading
        
        for (int i = 0; i < headerData.length; i++) {
            
            if(headerData[i] == TYPE_HOST_ID && headerData[i+1] == LEN_HOST_ID && bActualBlock < TYPE_HOST_ID) {
                bMac = Arrays.copyOfRange(headerData, 2+i, 2+i+LEN_HOST_ID);
                bActualBlock = TYPE_HOST_ID;
            }
            
            if(headerData[i] == TYPE_PHYSICAL_MEDIUM && headerData[i+1] == LEN_PHYSICAL_MEDIUM && bActualBlock < TYPE_PHYSICAL_MEDIUM) {
                bPhysicalMedium = headerData[1+i+LEN_PHYSICAL_MEDIUM];
                bActualBlock = TYPE_PHYSICAL_MEDIUM;
            }
            
            if(headerData[i] == TYPE_WIRELESS_MODE && headerData[i+1] == LEN_WIRELESS_MODE && bActualBlock < TYPE_WIRELESS_MODE) {
                bWirelessMode = headerData[1+i+LEN_WIRELESS_MODE];
                bActualBlock = TYPE_WIRELESS_MODE;
            }
            
            if(headerData[i] == TYPE_IPV4 && headerData[i+1] == LEN_IPV4 && bActualBlock < TYPE_IPV4) {
                bIpv4 = Arrays.copyOfRange(headerData, 2+i, 2+i+LEN_IPV4);
                bActualBlock = TYPE_IPV4;
            }
            
            if(headerData[i] == TYPE_IPV6 && headerData[i+1] == LEN_IPV6 && bActualBlock < TYPE_IPV6) {
                bIpv6 = Arrays.copyOfRange(headerData, 2+i, 2+i+LEN_IPV6);
                bActualBlock = TYPE_IPV6;
            }
            
            if(headerData[i] == TYPE_LINK_SPEED && headerData[i+1] == LEN_LINK_SPEED && bActualBlock < TYPE_LINK_SPEED) {
                bLinkSpeed = Arrays.copyOfRange(headerData, 2+i, 2+i+LEN_LINK_SPEED);
                bActualBlock = TYPE_LINK_SPEED;
            }
            
        }
    }
    
}

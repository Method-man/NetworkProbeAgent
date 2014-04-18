package org.hkfree.topoagent.domain;

import org.hkfree.topoagent.interfaces.Parser;
import org.hkfree.topoagent.core.LogService;
import java.io.UnsupportedEncodingException;
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
    public static final byte TYPE_CHARACTERISTICS = 0x02;
    public static final byte TYPE_PHYSICAL_MEDIUM = 0x03;
    public static final byte TYPE_WIRELESS_MODE = 0x04;
    public static final byte TYPE_BSSID = 0x05;
    public static final byte TYPE_SSID = 0x06;
    public static final byte TYPE_IPV4 = 0x07;
    public static final byte TYPE_IPV6 = 0x08;
    public static final byte TYPE_MAXIMUM_OPERATIONAL_RATE = 0x09;
    public static final byte TYPE_PERFOMANCE_COUNTER_FREQUENCY = 0x0A;
    public static final byte TYPE_LINK_SPEED = 0x0c;
    public static final byte TYPE_MACHINE_NAME = 0x0f;

    public static final byte LEN_HOST_ID = 0x06;
    public static final byte LEN_CHARACTERISTICS = 0x02;
    public static final byte LEN_PHYSICAL_MEDIUM = 0x04;
    public static final byte LEN_WIRELESS_MODE = 0x01;
    public static final byte LEN_BSSID = 0x06;
//    public static final byte LEN_SSID = 0x00; VARIABLE LENGTH
    public static final byte LEN_IPV4 = 0x04;
    public static final byte LEN_IPV6 = 0x10;
    public static final byte LEN_MAXIMUM_OPERATIONAL_RATE = 0x02;
    public static final byte LEN_PERFOMANCE_COUNTER_FREQUENCY = 0x08;
    public static final byte LEN_LINK_SPEED = 0x04;

    byte[] headerData;

    private byte[] bMac = new byte[LEN_HOST_ID];
    private byte[] bBSSID = new byte[LEN_BSSID];
    private byte[] bIpv4 = new byte[LEN_IPV4];
    private byte[] bIpv6 = new byte[LEN_IPV6];
    private byte[] bLinkSpeed = new byte[LEN_LINK_SPEED];

    private String sMachineName = "";

    private byte bPhysicalMedium = 0x00;
    private byte bWirelessMode = 0x00;

    public LltdHelloSubHeaderParser(byte[] data) {
        this.headerData = data;
        parse();
    }

    /**
     * Returns 1. element HOST ID MAC
     *
     * @return
     */
    public String getHostIdMacAddress() {
        return FormatUtils.mac(bMac);
    }

    /**
     * Returns x. element BSSID - router/AP id
     *
     * @return
     */
    public String getBSSID() {
        return FormatUtils.mac(bBSSID);
    }

    /**
     * Returns 3. element HOST ID MAC
     *
     * @return
     */
    public String getPhysicalMedium() {
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
    public String getWirelessMode() {
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
    public String getIpv4() {
        return FormatUtils.ip(bIpv4);
    }

    /**
     * Returns 8. element IPv6
     *
     * @return
     */
    public String getIpv6() {
        return FormatUtils.asStringIp6(bIpv6, true);
    }

    /**
     * Returns 1. element LINK SPEED
     *
     * @return
     */
    public String getLinkSpeed() {
        return String.valueOf(ByteBuffer.wrap(bLinkSpeed).getInt() / 1_00_00) + " Mbit";
    }

    public String getMachineName() {
        return sMachineName;
    }

    private void parseType(byte type, int pointer2data, byte length) {
        switch (type) {
            case TYPE_HOST_ID: {
                bMac = Arrays.copyOfRange(headerData, pointer2data, pointer2data + length);
            }
            break;
            case TYPE_WIRELESS_MODE: {
                bWirelessMode = headerData[pointer2data];
            }
            break;
            case TYPE_PHYSICAL_MEDIUM: {
                bPhysicalMedium = headerData[pointer2data];
            }
            break;
            case TYPE_IPV4: {
                bIpv4 = Arrays.copyOfRange(headerData, pointer2data, pointer2data + length);
            }
            break;
            case TYPE_IPV6: {
                bIpv6 = Arrays.copyOfRange(headerData, pointer2data, pointer2data + length);
            }
            break;
            case TYPE_LINK_SPEED: {
                bLinkSpeed = Arrays.copyOfRange(headerData, pointer2data, pointer2data + length);
            }
            break;
            case TYPE_MACHINE_NAME: {
                byte[] bMachineName = Arrays.copyOfRange(headerData, pointer2data, pointer2data + length);
                try {
                    sMachineName = new String(bMachineName, "UTF-16LE");
                } catch (UnsupportedEncodingException ex) {
                    LogService.log2ConsoleError(this, ex);
                }
            }
            break;
            case TYPE_CHARACTERISTICS: {
                // TODO:
            }
            break;
            case TYPE_BSSID: {
                bBSSID = Arrays.copyOfRange(headerData, pointer2data, pointer2data + length);
            }
            break;
            case TYPE_SSID: {
                // TODO:
            }
            break;
            case TYPE_MAXIMUM_OPERATIONAL_RATE: {
                // TODO:
            }
            break;
            case TYPE_PERFOMANCE_COUNTER_FREQUENCY: {
                // TODO:
            }
            break;
            default: {
                // LogService.log2Console(this, "neznamy typ");
            }
        }
    }

    private void parse() {
        if (headerData.length == 0) {
            return;
        }

        for (int i = 0; i <= headerData.length; i++) {
            // posledni byte je pouze ukoncovaci
            if (i + 1 >= headerData.length) {
                break;
            }

            byte type = headerData[i];
            byte length = headerData[i + 1];
            parseType(type, i + 2, length);
            i += (length + 1);

        }

    }

}

package org.hkfree.topoagent.domain;

import org.hkfree.topoagent.interfaces.Parser;
import org.hkfree.topoagent.core.LogService;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public static final byte TYPE_802_11_PHYSICAL_MEDIUM = 0x15;

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
    public static final byte LEN_802_11_PHYSICAL_MEDIUM = 0x01;

    byte[] headerData;

    private byte[] bMac = new byte[LEN_HOST_ID];
    private byte[] bBSSID = new byte[LEN_BSSID];
    private byte[] bIpv4 = new byte[LEN_IPV4];
    private byte[] bIpv6 = new byte[LEN_IPV6];
    private byte[] bLinkSpeed = new byte[LEN_LINK_SPEED];

    private String sMachineName = "";
    private String sSSID = "";

    private byte bCharacter = 0x00;
    private byte bPhysicalMedium = 0x00;
    private byte b80211physicalMedium = 0x00;
    private byte bWirelessMode = -0x01;

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
     * Returns x. element SSID - wifi name
     *
     * @return
     */
    public String getSSID() {
        return sSSID.trim();
    }

    /**
     * Get Characteristics of responder
     *
     * @return String description
     */
    public String GetCharacteristics() {
        String sCharacter = "";
        if ((bCharacter & 0xff) > 1) {
            /*
             * P X F M L - - -
             * P - public of NAT
             * X - private of NAT
             * F - full duplex
             * M - has management web page
             * L - loopback
             */

            if (((bCharacter >> 7) & 1) == 1) {
                sCharacter += "public side of NAT, ";
            }
            if (((bCharacter >> 6) & 1) == 1) {
                sCharacter += "private side of NAT, ";
            }
            if (((bCharacter >> 5) & 1) == 1) {
                sCharacter += "is full duplex, ";
            }
            if (((bCharacter >> 4) & 1) == 1) {
                sCharacter += "has web management, ";
            }
            if (((bCharacter >> 3) & 1) == 1) {
                sCharacter += "is loopback, ";
            }
        }

        return sCharacter;
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
                sPhysicalMedium = Device.CONNECTION_WIFI;
                break;
            case 0x06:
                sPhysicalMedium = "Ethernet";
                break;
            default:
                sPhysicalMedium = "Unknown";
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
            case 0x02:
                sWirelessMode = "Automatic mode";
                break;
            case 0x08:
                sWirelessMode = " -- ";
                break;
            default:
                sWirelessMode = String.valueOf(bWirelessMode);
                break;
        }
        return sWirelessMode;
    }

    /**
     * Returns x. element WIRELESS PHYSICAL MEDIUM
     *
     * @return
     */
    public String get80211PhysicalMedium() {
        String s80211physicalMedium;
        switch (b80211physicalMedium) {
            case 0x00:
                s80211physicalMedium = "unknown";
                break;
            case 0x01:
                s80211physicalMedium = "FHSS 2.4 gigahertz (GHz)";
                break;
            case 0x02:
                s80211physicalMedium = "DSSS 2.4 GHz";
                break;
            case 0x03:
                s80211physicalMedium = "IR Baseband";
                break;
            case 0x04:
                s80211physicalMedium = "OFDM 5 GHz";
                break;
            case 0x05:
                s80211physicalMedium = "HRDSSS";
                break;
            case 0x06:
                s80211physicalMedium = "ERP";
                break;
            case 0x07:
                s80211physicalMedium = "Reserved for future use";
                break;
            default:
                s80211physicalMedium = String.valueOf(b80211physicalMedium);
                break;
        }
        return s80211physicalMedium;
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
                /**
                 * The Host ID attribute uniquely identifies the host on which the responder is running. All responders
                 * MUST include this attribute in all Hello frames.
                 */
                bMac = Arrays.copyOfRange(headerData, pointer2data, pointer2data + length);
            }
            break;
            case TYPE_WIRELESS_MODE: {
                bWirelessMode = headerData[pointer2data];
            }
            break;
            case TYPE_PHYSICAL_MEDIUM: {
                // cteme jen posledni dulezity byte
                bPhysicalMedium = headerData[pointer2data + length - 1];
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
                    LogService.Log2ConsoleError(this, ex);
                }
            }
            break;
            case TYPE_CHARACTERISTICS: {
                /**
                 * The Characteristics attribute identifies various characteristics of the responder host and network
                 * interface. This attribute is mandatory. All responders MUST include this attribute in all Hello
                 * frames.
                 */

                bCharacter = headerData[pointer2data];

            }
            break;
            case TYPE_BSSID: {
                /**
                 * This field specifies the MAC address of the AP with which a wireless responder's wireless network
                 * interface is associated.
                 */
                bBSSID = Arrays.copyOfRange(headerData, pointer2data, pointer2data + length);
            }
            break;
            case TYPE_SSID:  {
                byte[] bSSID = Arrays.copyOfRange(headerData, pointer2data, pointer2data + length);
                sSSID = new String(bSSID);
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
            case TYPE_802_11_PHYSICAL_MEDIUM: {
                b80211physicalMedium = headerData[pointer2data];
            }
            break;
            default: {
                // LogService.Log2Console(this, "neznamy typ");
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

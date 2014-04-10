/*
 *
 */
package org.hkfree.topoagent.domain;

import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.packet.JHeader;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.annotate.Bind;
import org.jnetpcap.packet.annotate.Dynamic;
import org.jnetpcap.packet.annotate.Field;
import org.jnetpcap.packet.annotate.FieldSetter;
import org.jnetpcap.packet.annotate.Header;
import org.jnetpcap.packet.annotate.HeaderLength;
import org.jnetpcap.protocol.lan.Ethernet;

/**
 * Lltd all sub headers
 *
 * @author Filip Valenta
 */
@Header(name = "LLTD") // , length = 18 * BYTE
public class LltdHeader extends JHeader {

    public static final byte HEADER_VERSION_1 = 0x01;

    public static final byte HEADER_SERVICE_TOPOLOGY_DISCOVERY = 0x00;
    public static final byte HEADER_SERVICE_QUICK_DISCOVERY = 0x01;

    public static final byte HEADER_RESERVED = 0x00;

    public static final byte HEADER_FUNCTION_DISCOVER = 0x00;
    public static final byte HEADER_FUNCTION_HELLO = 0x01;
    public static final byte HEADER_FUNCTION_RESET = 0x08;

    public static final int ETHERNET_HEADER_LLTD = 0x88d9;

    public static int LLTD_ID = 64;

    @Bind(to = Ethernet.class)
    public static boolean bindToEthernet(JPacket packet, Ethernet eth) {
        return eth.type() == ETHERNET_HEADER_LLTD;
    }

    @HeaderLength
    public static int headerLength(JBuffer buffer, int offset) {
        return buffer.size() - offset;
    }

    @FieldSetter
    public void destination(byte[] value) {
        setByteArray(0, value);
    }

    @Field(offset = 0, length = 6, display = "destination", format = "#mac#")
    public byte[] destination() {
        return getByteArray(0, 6);
    }

    @Dynamic(Field.Property.DESCRIPTION)
    public String serviceDescription() {
        String descr = null;
        switch (service()) {
            case HEADER_SERVICE_TOPOLOGY_DISCOVERY:
                descr = "topology discovery";
                break;
            case HEADER_SERVICE_QUICK_DISCOVERY:
                descr = "quick discovery";
                break;
        }
        return descr;
    }

    @Dynamic(Field.Property.DESCRIPTION)
    public String functionDescription() {
        String descr = null;
        switch (function()) {
            case HEADER_FUNCTION_DISCOVER:
                descr = "discover";
                break;
            case HEADER_FUNCTION_HELLO:
                descr = "hello";
                break;
            case HEADER_FUNCTION_RESET:
                descr = "reset";
                break;
        }
        return descr;
    }

    @FieldSetter
    public void source(byte[] value) {
        setByteArray(6, value);
    }

    @Field(offset = 6, length = 6, display = "source", format = "#mac#")
    public byte[] source() {
        return getByteArray(6, 6);
    }

    @FieldSetter
    public void type(short value) {
        setShort(12, value);
    }

    @Field(offset = 96, length = 16, format = "%x")
    public int type() {
        return super.getUShort(12); // Offset 12, length 2 bytes  
    }

    @Dynamic(Field.Property.DESCRIPTION)
    public String typeDescription() {
        // TODO: konst.
        return (type() == ETHERNET_HEADER_LLTD) ? "lltd" : null;
    }

    @FieldSetter
    public void version(byte value) {
        setByte(14, value);
    }

    @Field(offset = 14, length = 1, display = "version", format = "%x")
    public byte version() {
        return getByte(14);
    }

    @FieldSetter
    public void service(byte value) {
        setByte(15, value);
    }

    @Field(offset = 15, length = 1, display = "service", format = "%x")
    public byte service() {
        return getByte(15);
    }

    @FieldSetter
    public void reserved(byte value) {
        setByte(16, value);
    }

    @Field(offset = 16, length = 1, display = "reserved", format = "%x")
    public byte reserved() {
        return getByte(16);
    }

    @FieldSetter
    public void function(byte value) {
        setByte(17, value);
    }

    @Field(offset = 17, length = 1, display = "function", format = "%x")
    public byte function() {
        return getByte(17);
    }

    @FieldSetter
    public void destinationReal(byte[] value) {
        setByteArray(18, value);
    }

    @Field(offset = 18, length = 6, display = "real destination", format = "#mac#")
    public byte[] destinationReal() {
        return getByteArray(18, 6);
    }

    @FieldSetter
    public void sourceReal(byte[] value) {
        setByteArray(24, value);
    }

    @Field(offset = 24, length = 6, display = "real source", format = "#mac#")
    public byte[] sourceReal() {
        return getByteArray(24, 6);
    }

    @FieldSetter
    public void xid(short value) {
        setShort(30, value);
    }

    @Field(offset = 96, length = 16, format = "%x")
    public int xid() {
        return super.getUShort(30); // Offset 12, length 2 bytes  
    }

}

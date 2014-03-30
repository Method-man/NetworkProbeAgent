
package org.hkfree.topoagent.core;

import org.hkfree.topoagent.domain.Device;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Static access to logable outputs
 * 
 * @author Filip Valenta
 */
public class LogService {
    
    /**
     * Log all objects into the TXT file
     * 
     * @param devices List<Device>
     */
    public static void LogDeviceList(List<Device> devices)
    {
        Writer writer;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("device-list.txt"), "utf-8"));
            for(Device d: devices) {
                writer.write(d.toString()+"\r\n");
            }
            writer.close();
        } catch (IOException ex) {
            Log2ConsoleError(LogService.class, ex);
        }
    }
    
    public static void Log2xmlFile(String xmlData, String filename)
    {
        Writer writer;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "utf-8"));
            writer.write(xmlData);
            writer.close();
        } catch (IOException ex) { 
            Log2ConsoleError(LogService.class, ex);
        }
    }
    
    /**
     * Print to console
     * 
     * @param object Object class who call this method
     * @param output String
     */
    public static void Log2Console(Object object, String output)
    {
        Logger log = LogManager.getLogger(caller(object));
        log.info(output);
    }
    
    /**
     * Log exceptions
     * 
     * @param object
     * @param exceptionMessage 
     */
    public static void Log2ConsoleError(Object object, Exception exceptionMessage)
    {
        Logger log = LogManager.getLogger(caller(object));
        log.error(exceptionMessage.getMessage());
    }
    
    /**
     * Debug 4 packets
     * 
     * @param packet 
     */
    public static void LogPacket2Console(JPacket packet)
    {
        Log2Console(LogService.class, packet.getState().toDebugString());
        Log2Console(LogService.class, packet.toString());
    }
    
    /**
     * Print to console in HEX output
     * 
     * @param o 
     */
    public static void Log2Hex(Object o)
    {
        if(o instanceof Integer) {
            System.out.println(Integer.toHexString((int) o));
        } else if(o instanceof byte[]) {
            StringBuilder sb = new StringBuilder();
            for(byte b: (byte[])o)
            sb.append(" "+String.format("%02x", b&0xff));
            System.out.println(sb);
        } else if(o instanceof Byte) {
            System.out.println(FormatUtils.toHexString((byte) o));
        } else {
            System.out.println("unsuported hex input");
        }
    }
    
    /**
     * Parse caller
     * 
     * @param object
     * @return 
     */
    private static String caller(Object object)
    {
        String caller = "";
        if(object instanceof String) {
            caller = (String) object;
        } else {
            caller = object.getClass().getSimpleName().toString();
        }
        return caller;
    }
    
}

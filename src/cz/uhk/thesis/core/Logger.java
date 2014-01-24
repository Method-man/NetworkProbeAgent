
package cz.uhk.thesis.core;

import cz.uhk.thesis.model.Device;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import org.jnetpcap.packet.JPacket;

/**
 * Static access to logable outputs
 * 
 * @author Filip Valenta
 */
public class Logger {
    
    /**
     * Log all objects into the TXT file
     * 
     * @param devices List<Device>
     */
    public static void Log2File(List<Device> devices)
    {
        Writer writer;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("device-list.txt"), "utf-8"));
            for(Device d: devices) {
                writer.write(d.toString()+"\r\n");
            }
            writer.write("---------------------------------\r\n");
            writer.close();
        } catch (IOException ex) { }
    }
    
    /**
     * Print to console
     * 
     * @param object Object class who call this method
     * @param output String
     */
    public static void Log2Console(Object object, String output)
    {
        String whoCalls;
        if(object instanceof String) {
            whoCalls = (String) object;
        } else {
            whoCalls = object.getClass().getSimpleName().toString();
        }
        System.out.println(whoCalls+": "+output);
    }
    
    /**
     * Log exceptions
     * 
     * @param object
     * @param exceptionMessage 
     */
    public static void Log2ConsoleError(Object object, Exception exceptionMessage)
    {
        System.out.println("EXCEPTION: " + exceptionMessage.getMessage());
    }
    
    /**
     * Debug 4 packets
     * 
     * @param packet 
     */
    public static void Log2ConsolePacket(JPacket packet)
    {
        System.out.println(packet.getState().toDebugString());
        System.out.println(packet);
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
        } else {
            System.out.println("unsuported hex input");
        }
    }
    
}

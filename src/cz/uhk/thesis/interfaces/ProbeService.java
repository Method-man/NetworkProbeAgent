
package cz.uhk.thesis.interfaces;

import cz.uhk.thesis.model.Parser;
import org.jnetpcap.packet.JPacket;

/**
 *
 * @author Filip Valenta
 */
public interface ProbeService {
    
    public void packetParse(JPacket packet);
    
    public void packetCompare(String ip, byte[] mac, Parser parser);
    
    public void packetCompare(String ip, byte[] mac);
    
    public void probeSend();
    
}

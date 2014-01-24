
package cz.uhk.thesis.modules;

import cz.uhk.thesis.model.Parser;
import org.jnetpcap.packet.JPacket;

/**
 *
 * @author Filip Valenta
 */
public interface ProbeService {
    
    public void packetParse(JPacket packet);
    
    public void packetCompare(String ip, String mac, Parser parser);
    
    public void packetCompare(String ip, String mac);
    
    public void probeSend();
    
}

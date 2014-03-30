
package org.hkfree.topoagent.interfaces;

import org.hkfree.topoagent.domain.Parser;
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

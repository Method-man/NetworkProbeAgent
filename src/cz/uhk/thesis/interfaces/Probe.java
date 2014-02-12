/*
 * Modul pro sniffovani
 */

package cz.uhk.thesis.interfaces;

import cz.uhk.thesis.core.Core;
import org.jnetpcap.packet.JPacket;

/**
 *
 * @author Filip Valenta
 */
public interface Probe {
    
    public Core getCore();
    
    public String GetModuleName();
    
    public ProbeService GetProbeService();
            
    public boolean useThisModule(JPacket packet);
    
    public void InitBefore();
    
    public void InitAfter();
    
}

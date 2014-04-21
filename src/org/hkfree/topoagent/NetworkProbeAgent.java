package org.hkfree.topoagent;

import java.net.UnknownHostException;
import java.util.ConcurrentModificationException;
import jcifs.netbios.NbtAddress;
import org.hkfree.topoagent.core.Core;
import org.hkfree.topoagent.core.LogService;

/**
 *
 * @author Filip Valenta
 */
public class NetworkProbeAgent {
    
    public NetworkProbeAgent() {
        System.setProperty("log4j.configurationFile", "log4j2.xml");
        
        Core core = new Core();
        core.init();
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new NetworkProbeAgent();
        } catch (ConcurrentModificationException ex) {
            LogService.Log2ConsoleError("NetworkProbeAgent", ex);
        } catch(Exception ex) {
            LogService.Log2ConsoleError("NetworkProbeAgent", ex);
        }
    }
    
}

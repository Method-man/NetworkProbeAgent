
package org.hkfree.topoagent;

import org.hkfree.topoagent.core.Core;

/**
 *
 * @author Filip Valenta
 */
public class NetworkProbeAgent {
    
    private final Core core;

    public NetworkProbeAgent() 
    {
        System.setProperty("log4j.configurationFile", "log4j2.xml");
        
        core = new Core();
        core.Init();
        
    }
    

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new NetworkProbeAgent();
        
    }

}


package org.hkfree.topoagent.module;

import java.net.UnknownHostException;
import jcifs.netbios.NbtAddress;
import org.hkfree.topoagent.core.LogService;

/**
 *
 * @author Filip Valenta
 */
public class NetBIOSService {
    
    /**
     * Get FIRST netbios name assignet to IP
     * 
     * @param ip
     * @return 
     */
    public String GetNetBIOSName(String ip)
    {
        String hostname = "";
        try {
            for (NbtAddress na : NbtAddress.getAllByAddress(ip)) {
                if(!na.firstCalledName().equals("")) {
                    hostname = na.firstCalledName();
                    break;
                }
            }
        } catch (UnknownHostException ex) {
            LogService.Log2ConsoleError(this, ex);
        }
        return hostname;
    }
    
}

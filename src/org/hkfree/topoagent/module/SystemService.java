package org.hkfree.topoagent.module;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.hkfree.topoagent.core.LogService;

/**
 * Comunication with OS
 *
 * @author Filip Valenta
 */
public class SystemService {

    /**
     * Get wifi bssid of this computer from console output
     *
     * @return
     */
    public String getWlanPossibleBSSID() {
        String line;
        String output = "";
        try {
            Process p = Runtime.getRuntime().exec("netsh wlan show interfaces");
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                if (line.trim().equals("")) {
                    continue;
                }
                String[] splits = line.split(" : ");
                if (splits.length > 1) {
                    String key = splits[0].trim();
                    String value = splits[1].trim();
                    if (key.equals("BSSID")) {
                        output = value;
                    }
                }
            }
            input.close();
        } catch (Exception ex) {
            LogService.Log2ConsoleError(this, ex);
        }
        return output;
    }

    /**
     * Try to erase arp cache
     */
    public void arpCacheDelete() {
        execCommand("arp -d", "ARP cache deleted");
    }

    /**
     * Try to erase dns cache
     */
    public void dnsCacheDelete() {
        execCommand("ipconfig /flushdns", "dns cache deleted");
    }

    private void execCommand(String command, String notice) {
        try {
            Process p = Runtime.getRuntime().exec(command);
            LogService.Log2Console(this, notice);
            String line;

            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                if(line.trim().equals("")) continue;
                LogService.Log2Console(this, line.trim());
            }
        } catch (IOException ex) {
            LogService.Log2ConsoleError(this, ex);
        }
    }

}

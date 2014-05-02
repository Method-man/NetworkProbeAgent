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

    public static final String WIFI_DATA_BSSID = "BSSID";
    public static final String WIFI_DATA_SSID = "SSID";
    public static final String WIFI_DATA_NETWORK_TYPE = "Network type";
    public static final String WIFI_DATA_TRANSMIT_RAT = "Transmit rate (Mbps)";

    /**
     * Get wifi data of this computer from console output
     *
     * @return
     */
    public String getWlanData(String datakey) {
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
                    if (key.equals(datakey)) {
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

    public String getActiveDeviceIP() {
        String line;
        String ip = "";
        int actualMetrics = 500; // very high number
        try {
            Process p = Runtime.getRuntime().exec("route print");
            try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                while ((line = input.readLine()) != null) {
                    if (line.trim().equals("")) {
                        continue;
                    }
                    // too much spaces
                    String[] splits = line.trim().replaceAll("[ ]+", " ").split(" ");
                    if (splits.length > 4) {
                        // this is gateway, lets choose the lowest metrics
                        if (splits[0].equals("0.0.0.0") && splits[1].equals("0.0.0.0")) {
                            // String gateway = splits[2];
                            String interfaceIp = splits[3];
                            String metrics = splits[4];
                            // this interface has lower metrics, use them
                            if (actualMetrics > Integer.parseInt(metrics)) {
                                ip = interfaceIp;
                                actualMetrics = Integer.parseInt(metrics);
                            }
                        }
                    }
                }
            }
        } catch (IOException | NumberFormatException ex) {
            LogService.Log2ConsoleError(this, ex);
        }
        return ip;
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
                if (line.trim().equals("")) {
                    continue;
                }
                LogService.Log2Console(this, line.trim());
            }
        } catch (IOException ex) {
            LogService.Log2ConsoleError(this, ex);
        }
    }

}

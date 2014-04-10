/**
 * This class works with found devices from all modules
 */
package org.hkfree.topoagent.core;

import org.hkfree.topoagent.domain.Device;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jnetpcap.packet.format.FormatUtils;

/**
 *
 * @author Filip Valenta
 */
public class DeviceManager {

    // TODO: rozpoznat, ze 2 zarizeni se stejnou IP maji rozdilny MAC
    // TODO: rozpoznat 2 cesty do stejneho bodu
    HashMap<String, Device> devices = new HashMap<>();

    public Device getDevice(byte[] bMac) {
        String sMac = FormatUtils.mac(bMac);
        Device d = devices.get(sMac);
        if (d == null) {
            d = new Device(sMac, bMac);
            devices.put(sMac, d);
            logInfo();
        }

        return d;

    }

    /**
     * Returns number of found devices
     *
     * @return int
     */
    public int devicesCount() {
        return devices.size();
    }

    /**
     * Get gateway device
     *
     * @return
     */
    public Device getGateway() {
        for (Map.Entry<String, Device> d : devices.entrySet()) {
            if (d.getValue().isGateway()) {
                return d.getValue();
            }
        }
        return null;
    }

    /**
     * Trigger the LogService of devices if device list is not empty
     */
    public void logInfo() {

        // TODO: presunout do Loggeru !
        LogService.log2Console(this, "ukladam nove info");
        if (devicesCount() > 0) {
            List<Device> d = new ArrayList<>(devices.values());
            LogService.logDeviceList(d);
        }
    }

    public HashMap<String, Device> getAllDevices() {
        return devices;
    }

}

package org.hkfree.topoagent.module;

import java.util.Map;
import org.hkfree.topoagent.core.Core;
import org.hkfree.topoagent.core.DeviceManager;
import org.hkfree.topoagent.core.NetworkManager;
import org.hkfree.topoagent.domain.Device;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class to create topology of network
 *
 * @author Filip Valenta
 */
public class TopologyCreatorService {

    Core core;
    DeviceManager deviceManager;
    NetworkManager networkManager;

    public TopologyCreatorService(Core core) {
        this.core = core;
        this.deviceManager = core.getDeviceManager();
        this.networkManager = core.getNetworkManager();
    }

    /**
     * Make relations beetween devices for xml representation
     *
     * Example relation:
     * <relation from="20:2b:c1:95:1a:10" to="14:49:e0:55:5b:7c">
     * <medium>02</medium>
     * </relation>
     *
     * @param doc
     * @param rootElement
     */
    public void relationMapper(Document doc, Element rootElement) {
        Device gateway = deviceManager.getGateway();
        for (Map.Entry<String, Device> deviceEntry : deviceManager.getAllDevices().entrySet()) {
            Device device = deviceEntry.getValue();

            if (!device.isGateway()) {

                String sMacFrom = getMacFrom(device, gateway);
                String sMacTo = device.getMacLowest(false);

                Element relation = doc.createElement("relation");
                relation.setAttribute("from", sMacFrom);
                relation.setAttribute("to", sMacTo);

                Element medium = doc.createElement("medium");
                /**
                 * melo by tu byl 80211 physical medium, ale to nic neobsahuje String sMedium =
                 * device.getInfo(Device.DEVICE_802_11_PHYSICAL_MEDIUM);
                 */
                String sMedium = device.getInfo(Device.DEVICE_PHYSICAL_MEDIUM);
                if (sMedium == null || sMedium.equals("")) {
                    sMedium = "unknown"; // 0 is unknown by specification
                }
                medium.appendChild(doc.createTextNode(sMedium));

                relation.appendChild(medium);
                rootElement.appendChild(relation);
            }

        }
    }

    private boolean matchHardwareAddress(String mac1, String mac2) {
        boolean matches = false;
        if (mac1 != null && mac2 != null && mac1.substring(0, 14).equals(mac2.substring(0, 14))) {
            return true;
        }
        return matches;
    }

    /**
     * Find the right connection point
     *
     * @param device this device
     * @param gateway gateway of subnetwork
     * @return
     */
    private String getMacFrom(Device device, Device gateway) {

        // standard connection from
        String sMacFrom = device.getInfo(Device.DEVICE_BSSID);

        boolean isAccessPoint = matchHardwareAddress(
                device.getInfo(Device.DEVICE_BSSID),
                device.getInfo(Device.DEVICE_HOST_ID));

        if (isAccessPoint) { // connect to DEFAULT GATEWAY instead itself
            sMacFrom = gateway.getMacLowest(false);
        }
        
        boolean getFromGateway = false;

        // is LAN or undetected WLAN LLTD or invalid MAC
        if (!networkManager.isValidMac(sMacFrom)) {
            
            // is possible to load it from system command and only for local PC !
            if (device.getIp().equals(networkManager.getActiveDeviceIPasString())) {

                String backupBSSID = core.getSystemService().getWlanPossibleBSSID();
                if (!backupBSSID.equals("")) {
                    Device d = deviceManager.GetByFirst5BytesOfMAC(backupBSSID);
                    sMacFrom = d.getMacLowest(false).toUpperCase();
                } else {
                    getFromGateway = true;
                }

            }
            else {
                getFromGateway = true;
            }

        }
        else { // MAC is OK, check if there is any possible lower
            Device deviceFrom = deviceManager.GetByFirst5BytesOfMAC(sMacFrom);
            if (deviceFrom == null) { // A HAH ! that device does not exist ! invalid BSSID
                sMacFrom = gateway.getMacLowest(false);
            }
            else {
                sMacFrom = deviceFrom.getMacLowest(false);
            }
        }
        
        // connect to DEFAULT GATEWAY
        if(getFromGateway) {
            if(gateway != null) {
                sMacFrom = gateway.getMacLowest(false);
            }
        }
        
        return sMacFrom;
    }

}

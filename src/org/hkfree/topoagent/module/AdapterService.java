package org.hkfree.topoagent.module;

import org.hkfree.topoagent.core.Core;
import org.hkfree.topoagent.core.DeviceManager;
import org.hkfree.topoagent.core.LogService;
import org.hkfree.topoagent.core.NetworkManager;
import org.hkfree.topoagent.domain.Device;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jnetpcap.packet.format.FormatUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

/**
 * Service for messaging between ISP XML server and application
 *
 * @author Filip Valenta
 */
public class AdapterService {

    private String tracerouteHostname = "";
    private List<String> tracerouteDefault = new ArrayList<>();

    public AdapterService() {
        serverXmlGetSettings();
    }

    public String getTracerouteHostname() {
        return tracerouteHostname;
    }

    public List<String> getTracerouteDefault() {
        return tracerouteDefault;
    }

    /**
     * Send XML containing info about found out devices and relations
     *
     * @param c
     */
    public void serverXmlSend(Core c) {
        String xml;
        try {
            xml = prepareXML(c);
            LogService.log2Console(this, xml);
            LogService.log2xmlFile(xml, "export.xml");
        } catch (ParserConfigurationException ex) {
            LogService.log2ConsoleError(this, ex);
        }
    }

    /**
     * Get settings XML from server
     *
     * TODO: from server
     */
    private void serverXmlGetSettings() {
        try {
            File fXmlFile = new File("settings.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            tracerouteHostname = doc.getElementsByTagName("Hostname").item(0).getTextContent();
            NodeList nl = ((Element) doc.getElementsByTagName("Route").item(0)).getElementsByTagName("Ip");
            for (int i = 0; i < nl.getLength(); i++) {
                tracerouteDefault.add(nl.item(i).getTextContent());
                LogService.log2Console(this, "defaultni route ip " + i + " " + nl.item(i).getTextContent());
            }

        } catch (ParserConfigurationException | IOException | SAXException ex) {
            LogService.log2ConsoleError(this, ex);
        }
    }

    private String prepareXML(Core c) throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();
        Element lltd = doc.createElement("lltd");
        String publicIP = "";
        try {
            publicIP = getPublicIP();
        } catch (IOException ex) {
            LogService.log2ConsoleError(this, ex);
        }
        lltd.setAttribute("publicIP", publicIP);
        doc.appendChild(lltd);

        // traceroute
        Element traceroute = doc.createElement("traceroute");
        lltd.appendChild(traceroute);
        for (Entry<byte[], Integer> r : c.getDeviceManager().getGateway().getRoute2Internet().entrySet()) {
            Element ip = doc.createElement("ip");
            ip.appendChild(doc.createTextNode(FormatUtils.ip(r.getKey())));
            traceroute.appendChild(ip);
        }

        // devices
        for (Entry<String, Device> d : c.getDeviceManager().getAllDevices().entrySet()) {
            Element device = doc.createElement("device");
            device.setAttribute("mask", d.getKey());

            Element machineName = doc.createElement("machineName");
            machineName.appendChild(doc.createTextNode(d.getValue().getInfo(Device.DEVICE_MACHINE_NAME)));
            device.appendChild(machineName);

            Element ipv4 = doc.createElement("ipv4");
            ipv4.appendChild(doc.createTextNode(d.getValue().getIp()));
            device.appendChild(ipv4);

            Element ipv6 = doc.createElement("ipv6");
            ipv6.appendChild(doc.createTextNode(d.getValue().getInfo(Device.DEVICE_IPV6)));
            device.appendChild(ipv6);

            lltd.appendChild(device);
        }

        relationMapper(doc, lltd, c.getDeviceManager(), c.getNetworkManager());

        DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
        LSSerializer lsSerializer = domImplementation.createLSSerializer();
        return lsSerializer.writeToString(doc);
    }

    /**
     * Make relations beetween devices for xml representation
     *
     * Example relation:
     * <relation from="20:2b:c1:95:1a:10" to="14:49:e0:55:5b:7c">
     * <medium>02</medium>
     * </relation>
     *
     * @param rootElement
     * @param devices
     */
    private void relationMapper(Document doc, Element rootElement, DeviceManager deviceManager, NetworkManager networkManager) {
        Device gateway = deviceManager.getGateway();
        for (Entry<String, Device> deviceEntry : deviceManager.getAllDevices().entrySet()) {
            Device device = deviceEntry.getValue();
            if (!device.isGateway()) { // TODO: zjistit zda se tam ma pridavat i gateway
                String sMacFrom = device.getInfo(Device.DEVICE_BSSID);
                String sMacTo = device.getMac();
                // connect to default gateway instead of null or invalid mac
                if (!networkManager.isValidMac(sMacFrom)) {
                    sMacFrom = gateway.getMac();
                }
                Element relation = doc.createElement("relation");
                relation.setAttribute("from", sMacFrom);
                relation.setAttribute("to", sMacTo);

                // TODO: proverit medium zda delame stejne jako u kolegy
                Element medium = doc.createElement("medium");
                medium.appendChild(doc.createTextNode(device.getInfo(Device.DEVICE_PHYSICAL_MEDIUM)));
                relation.appendChild(medium);
                rootElement.appendChild(relation);
            }
        }

    }

    private String getPublicIP() throws IOException {
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
        String publicIP = in.readLine(); // vraci IP jako string
        in.close();

        return publicIP;
    }

}

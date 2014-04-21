package org.hkfree.topoagent.module;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.hkfree.topoagent.core.Core;
import org.hkfree.topoagent.core.LogService;
import org.hkfree.topoagent.domain.Device;
import org.hkfree.topoagent.domain.ScheduleJobCrate;
import org.hkfree.topoagent.interfaces.Probe;
import org.hkfree.topoagent.module.protocol.LltdProbeSchedule;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.format.FormatUtils;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import org.quartz.SchedulerException;
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

    private Core core;

    private String tracerouteHostname = "";
    private List<String> tracerouteDefault = new ArrayList<>();
    private TopologyCreatorService topologyCreator;

    private boolean debug = true;
    private String serverXMLvisualisation = "";
    private String cronLltd = "";
    private String cronPing = "";
    private String cronSend2Server = "";

    public AdapterService(Core core) {
        this.core = core;
        topologyCreator = new TopologyCreatorService(this.core);
        serverXmlGetSettings();
    }

    public String getCronLltd() {
        return cronLltd;
    }

    public String getCronPing() {
        return cronPing;
    }

    public String getCronSend2Server() {
        return cronSend2Server;
    }

    public String getTracerouteHostname() {
        return tracerouteHostname;
    }

    public List<String> getTracerouteDefault() {
        return tracerouteDefault;
    }

    public boolean isDebug() {
        return debug;
    }

    public String getServerXMLvisualisation() {
        return serverXMLvisualisation;
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
            LogService.Log2xmlFile(xml, "export.xml");
            
            String param = URLEncoder.encode(xml.replaceAll("\n", ""), "UTF-8");
            new URL(core.getAdapterService().getServerXMLvisualisation()+"?data="+param).openStream().close();
            
            LogService.Log2Console(this, "data odeslány na server");

        } catch (ParserConfigurationException | MalformedURLException | UnsupportedEncodingException ex) {
            LogService.Log2ConsoleError(this, ex);
        } catch (IOException ex) {
            LogService.Log2ConsoleError(this, ex);
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

            // traceroute
            tracerouteHostname = doc.getElementsByTagName("Hostname").item(0).getTextContent();
            NodeList nl = ((Element) doc.getElementsByTagName("Route").item(0)).getElementsByTagName("Ip");
            for (int i = 0; i < nl.getLength(); i++) {
                tracerouteDefault.add(nl.item(i).getTextContent());
                LogService.Log2Console(this, "defaultni route ip " + i + " " + nl.item(i).getTextContent());
            }

            // debug
            debug = doc.getElementsByTagName("Showlogs").item(0).getTextContent().equals("1");

            // XML server visualisation
            serverXMLvisualisation = doc.getElementsByTagName("Serverurl").item(0).getTextContent();

            // cron LLTD
            cronLltd = ((Element) doc.getElementsByTagName("Triggers").item(0)).getElementsByTagName("Lltd").item(0).getTextContent();

            // cron PING
            cronPing = ((Element) doc.getElementsByTagName("Triggers").item(0)).getElementsByTagName("Ping").item(0).getTextContent();

            // cron send 2 server
            cronSend2Server = ((Element) doc.getElementsByTagName("Triggers").item(0)).getElementsByTagName("Send2server").item(0).getTextContent();

        } catch (ParserConfigurationException | IOException | SAXException ex) {
            LogService.Log2ConsoleError(this, ex);
        }
    }

    private String prepareXML(Core c) throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();
        Element lltd = doc.createElement("lltd");
        String publicIP = getPublicIP();
        lltd.setAttribute("publicip", publicIP);
        lltd.setAttribute("millis", String.valueOf(new Date().getTime()));
        doc.appendChild(lltd);

        // traceroute
        Element traceroute = doc.createElement("traceroute");
        lltd.appendChild(traceroute);
        if (c.getDeviceManager().getGateway() != null) {
            for (Entry<byte[], Integer> r : c.getDeviceManager().getGateway().getRoute2Internet().entrySet()) {
                Element ip = doc.createElement("ip");
                ip.appendChild(doc.createTextNode(FormatUtils.ip(r.getKey())));
                traceroute.appendChild(ip);
            }
        }

        // devices
        for (Entry<String, Device> d : c.getDeviceManager().getAllDevices().entrySet()) {
            Element device = doc.createElement("device");
            device.setAttribute("mask", d.getValue().getMacLowest(false));

            Element machineName = doc.createElement("machineName");
            String deviceHostname = d.getValue().getInfo(Device.DEVICE_MACHINE_NAME);
            if(deviceHostname == null || deviceHostname.equals("")) {
                deviceHostname = core.getNetBIOSService().GetNetBIOSName(d.getValue().getIp());
            }
            machineName.appendChild(doc.createTextNode(deviceHostname));
            device.appendChild(machineName);

            Element ipv4 = doc.createElement("ipv4");
            ipv4.appendChild(doc.createTextNode(d.getValue().getIp()));
            device.appendChild(ipv4);

            Element ipv6 = doc.createElement("ipv6");
            ipv6.appendChild(doc.createTextNode(d.getValue().getInfo(Device.DEVICE_IPV6)));
            device.appendChild(ipv6);

            lltd.appendChild(device);
        }

        topologyCreator.relationMapper(doc, lltd);

        DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
        LSSerializer lsSerializer = domImplementation.createLSSerializer();
        return lsSerializer.writeToString(doc);
    }

    private String getPublicIP() {
        String publicIP = "";
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            publicIP = in.readLine(); // vraci IP jako string
            in.close();
        } catch (NullPointerException | IOException ex) {
            core.getExpertService().showNoPublicIpServiceAvailable();
            LogService.Log2ConsoleError(this, ex);
        }

        return publicIP;
    }

}

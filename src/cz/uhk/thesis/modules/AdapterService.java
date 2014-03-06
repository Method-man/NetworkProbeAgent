
package cz.uhk.thesis.modules;

import cz.uhk.thesis.core.Core;
import cz.uhk.thesis.core.LogService;
import cz.uhk.thesis.model.Device;
import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jnetpcap.packet.format.FormatUtils;
 
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

/**
 * Service for messaging between ISP XML server and application
 * 
 * @author Filip Valenta
 */
public class AdapterService {
    
    /**
     * Send XML containing info about found out devices and relations
     * @param c
     */
    public void ServerSendXML(Core c)
    {
        String xml;
        try {
            xml = prepareXML(c);
            LogService.Log2Console(this, xml);
            LogService.Log2FileXML(xml, "export.xml");
        } catch (ParserConfigurationException ex) {
            LogService.Log2ConsoleError(this, ex);
        }
    }
    
    /**
     * Get settings XML from server
     */
    public void ServerGetXMLSettings()
    {
        // TODO:
    }
    
    private String prepareXML(Core c) throws ParserConfigurationException
    {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        
        // root elements
		Document doc = docBuilder.newDocument();
		Element lltd = doc.createElement("lltd");
        lltd.setAttribute("publicIP", "xxx.xxx.xxx.xxx"); // TODO: ip + attribute milis ?
		doc.appendChild(lltd);
        
        // traceroute
        Element traceroute = doc.createElement("traceroute");
        lltd.appendChild(traceroute);
        for(Entry<byte[],Integer> r: c.GetDeviceManager().GetGateway().GetRoute2Internet().entrySet()) {
            Element ip = doc.createElement("ip");
            ip.appendChild(doc.createTextNode(FormatUtils.ip(r.getKey())));
            traceroute.appendChild(ip);
        }
        
        // devices
        for(Entry<String, Device> d: c.GetDeviceManager().GetAllDevices().entrySet()) {
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
        
        /**
         * TODO:
         * 
         * <relation from="20:2b:c1:95:1a:10" to="14:49:e0:55:5b:7c">
         * <medium>02</medium>
         * </relation>
         */
        
        DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
        LSSerializer lsSerializer = domImplementation.createLSSerializer();
        return lsSerializer.writeToString(doc); 
    }
    
}

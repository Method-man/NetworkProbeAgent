
package cz.uhk.thesis.modules;

import cz.uhk.thesis.interfaces.ProbeService;
import cz.uhk.thesis.interfaces.Probe;
import cz.uhk.thesis.core.Core;
import cz.uhk.thesis.core.Logger;
import cz.uhk.thesis.model.Parser;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Arp;

/**
 *
 * @author Filip Valenta
 */
public class ArpProbeService implements ProbeService {
    
    private final Probe probe;
    private final Core core;
    
    public ArpProbeService(Core core, Probe probe)
    {
        this.probe = probe;
        this.core = core;
    }
    
    /**
    *
    * @param packet
    * 
    */
    @Override
    public void packetParse(JPacket packet)
    {
        Arp arpHeader = new Arp();
        packet.getHeader(arpHeader);
        String type = "";

        Arp.OpCode opCode = arpHeader.operationEnum();
        if(opCode == Arp.OpCode.REQUEST) {
            type = "ARP REQUEST";
        } else if(opCode == Arp.OpCode.REPLY) {
            type = "ARP REPLY";
        }
        
        Logger.Log2Console(this, probe.GetModuleName()+" module: "+type+": "
            +FormatUtils.ip(arpHeader.spa())+" to "
            +FormatUtils.ip(arpHeader.tpa()));
        
        packetCompare(FormatUtils.ip(arpHeader.spa()), arpHeader.sha());
        packetCompare(FormatUtils.ip(arpHeader.tpa()), arpHeader.tha());
    }
    
    

    @Override
    public void packetCompare(String ip, byte[] mac) {
        if(core.getNetworkManager().isValidIp(ip) && core.getNetworkManager().isValidMac(mac)) {
            core.GetDeviceManager().getDevice(mac).SetIP(ip);
            core.getProbeLoader().NotifyAllModules();
        }
    }
    
    @Override
    public void packetCompare(String ip, byte[] mac, Parser parser) {
        
    }

    @Override
    public void probeSend() {
        
    }

    

}

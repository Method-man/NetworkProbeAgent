
package cz.uhk.thesis.modules;

import cz.uhk.thesis.interfaces.ProbeService;
import cz.uhk.thesis.interfaces.Probe;
import cz.uhk.thesis.core.Core;
import cz.uhk.thesis.model.LltdHeader;
import cz.uhk.thesis.model.LltdQdDiscoveryPacket;
import cz.uhk.thesis.model.LltdQdResetPacket;
import cz.uhk.thesis.core.Logger;
import cz.uhk.thesis.model.Device;
import cz.uhk.thesis.model.LltdHelloSubHeaderParser;
import cz.uhk.thesis.model.Parser;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.JPacket;

/**
 *
 * @author Filip Valenta
 */
public class LltdProbeService implements ProbeService {
    
    private final Probe probe;
    private final Core core;
    
    public LltdProbeService(Core core, Probe probe)
    {
        this.probe = probe;
        this.core = core;
    }
    
    @Override
    public void packetParse(JPacket packet)
    {
        if(packet.hasHeader(new LltdHeader())) {
            packet.scan(LltdHeader.LLTD_ID);
            LltdHeader lltdHeader = packet.getHeader(new LltdHeader());
            
            // is LLTD hello response packet
            if(lltdHeader.function() == LltdHeader.HEADER_FUNCTION_HELLO) {
                byte[] subheader = lltdHeader.getByteArray(
                        LltdHelloSubHeaderParser.OFFSET_SUBHEADER_HELLO, 
                        packet.size() - LltdHelloSubHeaderParser.OFFSET_SUBHEADER_HELLO
                );
                LltdHelloSubHeaderParser helloParser = new LltdHelloSubHeaderParser(subheader);
                packetCompare(helloParser.getIpv4(), lltdHeader.source(), helloParser);
            }
        }
    }
    
    @Override
    public void packetCompare(String ip, byte[] mac) {
        
    }

    // TODO: udelat privatni !!!
    
    @Override
    public void packetCompare(String ip, byte[] mac, Parser parser) {
        if(core.getNetworkManager().isValidIp(ip) && core.getNetworkManager().isValidMac(mac)) {
            /**
             * TODO: skutecne nastavit ? zkontrolovat jaky zaznam existuje a musi se shodovat
             */
            core.GetDeviceManager().getDevice(mac).SetIP(ip);
            
            LltdHelloSubHeaderParser p = (LltdHelloSubHeaderParser) parser;
            
            core.GetDeviceManager().getDevice(mac).setInfo(Device.DEVICE_LINK_SPEED, p.getLinkSpeed());
            core.GetDeviceManager().getDevice(mac).setInfo(Device.DEVICE_INFO_HOST_ID, p.getHostIdMacAddress());
            core.GetDeviceManager().getDevice(mac).setInfo(Device.DEVICE_PHYSICAL_MEDIUM, p.getPhysicalMedium());
            core.GetDeviceManager().getDevice(mac).setInfo(Device.DEVICE_WIRELESS_MODE, p.getWirelessMode());
            core.GetDeviceManager().getDevice(mac).setInfo(Device.DEVICE_IPV6, p.getIpv6());
            core.GetDeviceManager().getDevice(mac).setInfo(Device.DEVICE_MACHINE_NAME, p.getMachineName());
            
            core.getProbeLoader().NotifyAllModules();
        }
    }

    @Override
    public void probeSend() 
    {
    ScheduledExecutorService mainLoopExecutor = Executors.newScheduledThreadPool(1);
    mainLoopExecutor.schedule(new Runnable() {
        @Override
        public void run() {
            
            PcapIf activeDevice = core.getNetworkManager().getActiveDevice();
            
            LltdQdResetPacket packet_r = new LltdQdResetPacket(core);
            int state_r = Pcap.OK;
            state_r += core.getNetworkManager().SendPacket(activeDevice, packet_r);
            state_r += core.getNetworkManager().SendPacket(activeDevice, packet_r);
            state_r += core.getNetworkManager().SendPacket(activeDevice, packet_r);
            if(state_r == Pcap.OK) {
                Logger.Log2Console(probe.GetModuleName(), "LLTD Reset QD Packet (3) odeslán");
            }


            LltdQdDiscoveryPacket packet_qd = new LltdQdDiscoveryPacket(core);
            int state_qd = Pcap.OK;
            state_qd += core.getNetworkManager().SendPacket(activeDevice, packet_qd);
            if(state_qd == Pcap.OK) {
                Logger.Log2Console(probe.GetModuleName(), "LLTD Discovery Packet (1) odeslán");
            }
            
        }

    }, 5, TimeUnit.SECONDS);
    }

}

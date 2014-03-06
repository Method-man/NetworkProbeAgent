
package cz.uhk.thesis.core;

import cz.uhk.thesis.model.Packet;
import cz.uhk.thesis.interfaces.Probe;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapExtensionNotAvailableException;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.packet.format.FormatUtils;

/**
 *
 * @author Filip Valenta
 */
public class NetworkManager {
    
    public static final int ACTIVE_DEVICE_DEBUG = 2;
    
    //TODO: vyladit pocet
    public static final int ACTIVE_DEVICE_MIN_PACKET_COUNT = 10;
    public static final int ACTIVE_DEVICE_WAIT_FOR_S = 10;
    
    public static final int MODE_CATCH_PACKETS = 0;
    public static final int MODE_INTERFACE_TEST = 1;
    
    public static final byte[] BROADCAST_MAC_ADDRESS = {(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,};
    
    private Set<String> filterExpressions = new HashSet<String>();
    
    public enum ExcludeMasks {
        NETWORK_IP_DEFAULT_GATEWAY("^0\\.0\\.0\\.0$"),
        NETWORK_IP_LOCALHOST("^127\\.0\\.0\\.1$"),
        NETWORK_IP_LINK_LOCAL("^169\\.254"),
        NETWORK_MAC_NULL("^(00:)+00$"),
        NETWORK_INTERFACE_DESCRIBE_VIRTUAL_VMWARE("virtual"),
        NETWORK_INTERFACE_DESCRIBE_VIRTUAL_SUN("sun");
        
        private ExcludeMasks(String mask) {
            this.mask = mask;
        }
        
        private final String mask;
        
        public String getMask() { return mask; }
    }
    
    private PcapIf activeDevice;
    
    private final Core core;

    StringBuilder errbuf = new StringBuilder();
    private final HashMap<Integer, PcapIf> interfaces = new HashMap<>();
    
    // TODO: co s tema promenyma
    private int packetCounterAll = 0;
    private int packetCounter = 0;
    
    public NetworkManager(Core core)
    {
        this.core = core;
    }
    
    public int GetLocalNetworkDevicesCount()
    {
        return interfaces.size();
    }
    
    public void InreasePacketCounterAll()
    {
        packetCounterAll++;
    }
    
    public void setActiveDevice(PcapIf activeDevice) {
        this.activeDevice = activeDevice;
    }
    
    public PcapIf getActiveDevice() {
        return activeDevice;
    }
    
    public boolean isValidIp(String ip)
    {
        boolean isValid = true;
        
        if(ip.matches(ExcludeMasks.NETWORK_IP_DEFAULT_GATEWAY.getMask())) isValid = false;
        if(ip.matches(ExcludeMasks.NETWORK_IP_LOCALHOST.getMask())) isValid = false;
        if(ip.matches(ExcludeMasks.NETWORK_IP_LINK_LOCAL.getMask())) isValid = false;
        
        return isValid;
    }
    
    public boolean isValidMac(byte[] mac)
    {
        return isValidMac(FormatUtils.mac(mac));
    }
    
    public boolean isValidMac(String mac)
    {
        boolean isValid = true;
        
        if(mac.matches(NetworkManager.ExcludeMasks.NETWORK_MAC_NULL.getMask())) isValid = false;
        
        return isValid;
    }
    
    /**
     * Load all network interfaces in computer
     */
    public void LoadNetworkInterfaces()
    {
        List<PcapIf> alldevs = new ArrayList<>();

        int result = Pcap.findAllDevs(alldevs, errbuf);
        if (result != Pcap.OK || alldevs.isEmpty()) {
            LogService.Log2Console(this, "Chyba, nemohu načíst síťová zařízení: " + errbuf.toString());
        } else {
            int i = 0;
            for(PcapIf interfc : alldevs) {
                this.interfaces.put(i, interfc);
                i++;
            }
        }
    }
    
    public void IncrearePacketCounter()
    {
        packetCounter++;
    }
    
    public int SendPacket(PcapIf device, Packet packet)
    {
        int status = -1;
        Pcap pcap;
        if(Pcap.isPcap100Loaded()) {
            pcap = Pcap.create(device.getName(), errbuf);
            preparePcapDevice(pcap);
            status = pcap.sendPacket( ByteBuffer.wrap( packet.getByteArray(0, packet.size())) );
            pcap.close();
        } else {
            throw new PcapExtensionNotAvailableException();
        }
        return status;
    }
    
    private Integer catchPackets(PcapIf device, final Core core, int mode)
    {
        packetCounter = 0;
        
        try {
            Pcap pcap = new Pcap();
            ProbeLoader probeLoader = core.getProbeLoader();
            if(Pcap.isPcap100Loaded()) {
                pcap = Pcap.create(device.getName(), errbuf);
                preparePcapDevice(pcap);
                preparePcapFilter(pcap);
            } else {
                throw new PcapExtensionNotAvailableException();
            }

            /**
             * CLASSIC
             */
            PcapPacketHandler<ProbeLoader> handler = new PcapPacketHandler<ProbeLoader>() 
            {
                @Override
                public void nextPacket(PcapPacket packet, ProbeLoader probeLoader) { 
                    for(Probe probe: probeLoader.GetProbes()) {
                        if(probe.useThisModule(packet)) {
                            probe.GetProbeService().packetParse(packet);
                            core.getNetworkManager().InreasePacketCounterAll();
                        }
                    }
                }  
            };
            
            /**
             * 4 FIND ACTIVE DEVICE, TEST DEVICE
             */
            PcapPacketHandler<String> handlerTestInterface = new PcapPacketHandler<String>() 
            {
                @Override
                public void nextPacket(PcapPacket packet, String nothing) { 
                    System.out.println("packet");
                    IncrearePacketCounter();
                }  
            };
            
            switch(mode) {
                case MODE_CATCH_PACKETS: {
                    pcap.loop(Pcap.LOOP_INFINITE, handler, probeLoader);
                } break;
                case MODE_INTERFACE_TEST: {
                    pcap.loop(ACTIVE_DEVICE_MIN_PACKET_COUNT, handlerTestInterface, "");
                } break;
                default: {
                    throw new Exception("unknown packet catch type");
                }
            }
            pcap.close();

        } catch(PcapExtensionNotAvailableException ex) {
            System.out.println(ex.toString());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        
        return packetCounter;
    }
    
    /**
    * FIND PROBABLY ACTIVE DEVICE
    * 
    * http://docs.oracle.com/javase/6/docs/api/java/net/InetAddress.html
    *
    * TODO: cache result
    * TODO: when app start up, check if interface is still activated, if not run this func again
    */
    public void FindActiveDevice()
    {
        try {
            for(final Map.Entry<Integer, PcapIf> interfc: GetNetworkInterfaces().entrySet()) {
                InetAddress localIp = InetAddress.getByAddress(interfc.getValue().getAddresses().get(0).getAddr().getData());
                
                if(IsRealDeviceFilter(interfc.getValue()) && localIp.isReachable(2000) && localIp instanceof Inet4Address) {
                    setActiveDevice(interfc.getValue());
                    LogService.Log2Console(this, "nalezeno aktivní rozhraní: "+GetDeviceIP(interfc.getValue()));
                    break;
                }
                
            }
        } catch (UnknownHostException ex) {
            LogService.Log2ConsoleError(this, ex);
        } catch (IOException ex) {
            LogService.Log2ConsoleError(this, ex);
        }
    }
    
    /**
     * Returns if this device is not virtual
     * 
     * @param device
     * @return 
     */
    private boolean IsRealDeviceFilter(PcapIf device)
    {
        boolean realDevice = true;
        
        realDevice &= !matchCaseInsensitive(NetworkManager.ExcludeMasks.NETWORK_INTERFACE_DESCRIBE_VIRTUAL_VMWARE.getMask(), device.getDescription());
        realDevice &= !matchCaseInsensitive(NetworkManager.ExcludeMasks.NETWORK_INTERFACE_DESCRIBE_VIRTUAL_SUN.getMask(), device.getDescription());
        
        return realDevice;
    }
    
    private boolean matchCaseInsensitive(String pattern, String str)
    {
        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(str);
        return m.matches();
    }
    
    public void CatchPacketsTrigger()
    {
        ExecutorService mainLoopExecutor = Executors.newSingleThreadExecutor();
        mainLoopExecutor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                LogService.Log2Console(this, "Core: spouštím smyčku odchytávání packetů");
                return catchPackets(
                    getActiveDevice(), 
                    core, 
                    NetworkManager.MODE_CATCH_PACKETS
                );
            }
        });
    }
    
    /**
     * Get HashMap of network interfaces
     *
     * @return HashMap<Integer, PcapIf>
     */
    public HashMap<Integer, PcapIf> GetNetworkInterfaces() {
        return interfaces;
    }
    
    public String GetActiveDeviceIPasString()
    {
        return FormatUtils.ip(GetActiveDeviceIPasByte());
    }
    
    public byte[] GetActiveDeviceIPasByte()
    {
        return activeDevice.getAddresses().get(0).getAddr().getData();
    }
    
    public String GetActiveDeviceMACasString()
    {
        return FormatUtils.mac(GetActiveDeviceMACasByte());
    }
    
    public byte[] GetActiveDeviceMACasByte()
    {
        try {
            return activeDevice.getHardwareAddress();
        } catch (IOException ex) {
            LogService.Log2Console(this, "chyba zjisteni MAC adresy");
        }
        return null;
    }
    
    public String GetDeviceIP(PcapIf pcapif)
    {
        return FormatUtils.ip(pcapif.getAddresses().get(0).getAddr().getData());
    }
    
    /**
     * Add expression filter for future usage
     * 
     * @param expression 
     */
    public void Add2Filter(String expression)
    {
        filterExpressions.add(expression);
    }
    
    private void preparePcapDevice(Pcap pcap)
    {
        pcap.setSnaplen(64 * 1024);
        pcap.setPromisc(Pcap.MODE_PROMISCUOUS);
        pcap.setTimeout(10 * 1000);
        pcap.setDirection(Pcap.Direction.INOUT);
        pcap.setBufferSize(128 * 1024 * 1024); // Set ring-buffer to 128Mb
        pcap.activate();
    }
    
    private void preparePcapFilter(Pcap pcap)
    {
        PcapBpfProgram program = new PcapBpfProgram();
		int optimize = 1;         // 0 = false
		int netmask = 0xFFFFFF00; // TODO: aktualni masku, k detekci broadcast adres
        
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for(String expression: filterExpressions) {
            if(expression.equals("")) continue;
            if(!first) sb.append(" or ");
            sb.append(expression);
            first = false;
        }
        LogService.Log2Console(this, sb.toString());
        
		if (pcap.compile(program, sb.toString(), optimize, netmask) != Pcap.OK) {
			LogService.Log2Console(this, pcap.getErr());
			return;
		}
		
		if (pcap.setFilter(program) != Pcap.OK) {
			LogService.Log2Console(this, pcap.getErr());
			return;		
		}
    }
    
}

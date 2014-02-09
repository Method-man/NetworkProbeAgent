
package cz.uhk.thesis.modules;

import cz.uhk.thesis.core.Core;
import cz.uhk.thesis.core.Logger;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.logging.Level;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Ip4;

/**
 *
 * @author Filip Valenta
 */
public class TracerouteProbe implements Probe {
    
    ProbeService probeService;
    Core core;
    
    public TracerouteProbe(Core core)
    {
        this.core = core;
    }
    
    @Override
    public Core getCore() {
        return core;
    }

    @Override
    public String GetModuleName() {
        return "Traceroute";
    }

    @Override
    public ProbeService GetProbeService() {
        return probeService;
    }

    @Override
    public boolean useThisModule(JPacket packet) {
        boolean use = false;
        use |= useThisModuleObtainGatewayMac(packet);
        return use;
    }
    
    /**
     * Is this packet form gateway (in obtain test)
     * 
     * @param packet
     * @return 
     */
    public boolean useThisModuleObtainGatewayMac(JPacket packet)
    {
        boolean use = false;
        if(packet.hasHeader(new Ip4())) { // 4 obtain gateway mac
            use |= (FormatUtils.ip(((Ip4)packet.getHeader(new Ip4())).source()).equals(GetTracerouteHostTestIp()));
            use &= !Arrays.equals(((Ethernet)packet.getHeader(new Ethernet())).source(), core.getNetworkManager().GetActiveDeviceMACasByte());
        }
        return use;
    }

    @Override
    public void InitBefore() {
        probeService = new TracerouteProbeService(getCore(), this);
    }

    @Override
    public void InitAfter() {
        try {
            new URL("http://"+GetTracerouteHostTestHostname()).openStream().close();
        } catch (IOException ex) {
            Logger.Log2ConsoleError(this, ex);
        }
    }
    
    /***************************************************************************
     * PRIVATE METHODS
     *
     */
    
    /**
     * Obtain traceroute primary destination IP
     * 
     * @return 
     */
    private String GetTracerouteHostTestIp()
    {
        String ip = "";
        try {
            ip = InetAddress.getByName(GetTracerouteHostTestHostname()).getHostAddress();
            Logger.Log2Console(this, ip);
        } catch (UnknownHostException ex) {
            Logger.Log2ConsoleError(this, ex);
        }
        return ip;
    }
    
    private String GetTracerouteHostTestHostname()
    {
        // TODO: move to settings
        return "www.seznam.cz";
    }
    
}

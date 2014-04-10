package org.hkfree.topoagent.module.protocol;

import org.hkfree.topoagent.interfaces.ProbeService;
import org.hkfree.topoagent.interfaces.Probe;
import org.hkfree.topoagent.core.Core;
import org.hkfree.topoagent.core.LogService;
import org.hkfree.topoagent.interfaces.DeviceObserver;
import org.hkfree.topoagent.domain.Device;
import org.hkfree.topoagent.domain.Parser;
import org.hkfree.topoagent.interfaces.Stateful;
import static org.hkfree.topoagent.interfaces.Stateful.STATE_INITIAL;
import org.hkfree.topoagent.domain.IcmpPacket;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Icmp.IcmpType;
import org.jnetpcap.protocol.network.Ip4;

/**
 *
 * @author Filip Valenta
 */
public class TracerouteProbeService extends Stateful implements ProbeService, DeviceObserver {

    public static final int STATE_HAS_GATEWAY = 1;
    public static final int STATE_PROCESS_TRACEROUTE = 2;
    public static final int STATE_TRACEROUTE_DONE = 3;

    private final Probe probe;
    private final Core core;

    private int tracerouteTtl = 1;
    private int tracerouteTimeoutSeconds = 15; // default traceroute timeout 4 s
    private byte[] ip;

    public TracerouteProbeService(Core core, Probe probe) {
        this.probe = probe;
        this.core = core;
    }

    @Override
    public void packetParse(JPacket packet) {
        // function A - Obtain gateway MAC
        TracerouteProbe probeTraceroute = ((TracerouteProbe) probe);
        if (probeTraceroute.useThisModuleObtainGatewayMac(packet)) {

            // TODO: presunout cely obsah do metody
            Ethernet e = packet.getHeader(new Ethernet());
            Device d = core.getDeviceManager().getDevice(e.source());
            d.setIsGateway(true);
            if (isInState(STATE_INITIAL)) {
                setState(STATE_HAS_GATEWAY);
            }

        }
        // function B - Traceroute
        if (probeTraceroute.useThisModuleTraceroute(packet)) {
            byte[] destination = ((Ip4) packet.getHeader(new Ip4())).source();
            boolean requiredDestination = destination == getTracerouteHostTestIp();
            int type = ((Icmp) packet.getHeader(new Icmp())).type();
            if (type == IcmpType.TIME_EXCEEDED_ID && !requiredDestination) { // vyprselo TTL a neni to cilova destinace
                tracerouteTtl++;
                tracerouteSend();
                addIp2RouteFromPacket(packet, destination);
            }
            else if (requiredDestination || type == IcmpType.ECHO_REPLY_ID) { // je to cilova destinace nebo odpovedel nalezeno
                setState(STATE_TRACEROUTE_DONE);
                addIp2RouteFromPacket(packet, destination);
            }
        }
    }

    private void addIp2RouteFromPacket(JPacket packet, byte[] destination) {
        Ethernet e = packet.getHeader(new Ethernet());
        Device d = core.getDeviceManager().getDevice(e.source());
        d.addRoute2internet(destination);
    }

    private void addIp2Route(byte[] destination) {
        Device d = core.getDeviceManager().getGateway();
        if (d != null) {
            d.addRoute2internet(destination);
        }
        else {
            LogService.log2Console(this, "pokus o pridani traceroute ip do null device");
        }
    }

    @Override
    public void packetCompare(String ip, byte[] mac, Parser parser) {

    }

    @Override
    public void packetCompare(String ip, byte[] mac) {

    }

    @Override
    public void probeSend() {
        try {
            new URL("http://" + getTracerouteHostTestHostname()).openStream().close();
        } catch (IOException ex) {
            core.getExpertService().showRemoteHostUnavailable();
            LogService.log2ConsoleError(this, ex);
        }
    }

    private void tracerouteSend() {
        if (isInState(STATE_TRACEROUTE_DONE)) {
            return;
        }

        if ((!isInState(STATE_PROCESS_TRACEROUTE) || tracerouteTtl > 1)) {
            setState(STATE_PROCESS_TRACEROUTE);
            LogService.log2Console(this, "odesilam traceroute hop " + tracerouteTtl);
            try {
                Device gateway = core.getDeviceManager().getGateway();
                if (gateway != null) {
                    PcapIf activeDevice = core.getNetworkManager().getActiveDevice();

                    IcmpPacket icmp = new IcmpPacket(core);
                    icmp.ethernetDestination(gateway.getMacAsByte());
                    icmp.ipSource(core.getNetworkManager().getActiveDeviceIPasByte());
                    icmp.ipDestination(getTracerouteHostTestIp());
                    icmp.timeToLive(tracerouteTtl);
                    icmp.recalculateAllChecksums();

                    int state_icmp = Pcap.OK;
                    state_icmp += core.getNetworkManager().sendPacket(activeDevice, icmp);
                    if (state_icmp == Pcap.OK) {
                        LogService.log2Console(probe.getModuleName(), "Traceroute Packet (hop " + icmp.timeToLive() + ") odeslán");
                    }
                }
            } catch (UnknownHostException ex) {
                LogService.log2ConsoleError(this, ex);
            }
        }
    }

    @Override
    public void notifyChange() {
        if (isInState(STATE_HAS_GATEWAY)) {
            tracerouteTtl = 1;
            tracerouteSend();
            checkTraceroute();
        }
    }

    /**
     * Check and set allowed state
     *
     * @param state
     */
    @Override
    public void setState(int state) {
        switch (state) {
            case STATE_INITIAL:
            case STATE_HAS_GATEWAY:
            case STATE_PROCESS_TRACEROUTE:
            case STATE_TRACEROUTE_DONE: {
                this.state = state;
            }
            break;
            default:
                this.state = STATE_INITIAL;
                break;
        }
        LogService.log2Console(this, "nastavuji stav " + this.state);
        core.getProbeLoader().notifyAllModules();

        // TODO: prozatim odesilam vzdy, potom se to bude odesialt pri jine prilezitosti
        core.getAdapterService().serverXmlSend(core);
    }

    @Override
    public boolean isInState(int state) {
        return super.isInState(state);
    }

    /**
     * Obtain traceroute primary destination IP
     *
     * @return
     */
    public byte[] getTracerouteHostTestIp() {
        if (ip == null) {
            ip = null;
            try {
                ip = new byte[4];
                ip = InetAddress.getByName(getTracerouteHostTestHostname()).getAddress();
            } catch (UnknownHostException ex) {
                LogService.log2ConsoleError(this, ex);
            }
        }
        return ip;
    }

    public String getTracerouteHostTestHostname() {
        return core.getAdapterService().getTracerouteHostname();
    }

    private void checkTraceroute() {
        ScheduledExecutorService mainLoopExecutor = Executors.newScheduledThreadPool(1);
        mainLoopExecutor.schedule(new Runnable() {
            @Override
            public void run() {

                if (tracerouteTtl > 1) {
                    return; // traceroute je OK
                }
                LogService.log2Console(this, "traceroute bez odpovedi, nahrazuji defaultni cestou");
                core.getExpertService().showTracerouteNoRoute();
                try {
                    for (String sIp : core.getAdapterService().getTracerouteDefault()) {
                        InetAddress ip = InetAddress.getByName(sIp);
                        addIp2Route(ip.getAddress());
                    }
                    setState(STATE_TRACEROUTE_DONE);
                } catch (UnknownHostException ex) {
                    LogService.log2ConsoleError(this, ex);
                }
            }
        }, tracerouteTimeoutSeconds, TimeUnit.SECONDS);
    }

}

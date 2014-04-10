package org.hkfree.topoagent.module.protocol;

import org.hkfree.topoagent.core.Core;
import org.hkfree.topoagent.core.LogService;
import org.hkfree.topoagent.interfaces.Probe;
import org.hkfree.topoagent.domain.IcmpPacket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.format.FormatUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author Filip Valenta
 */
public class PingProbeSchedule implements Job {

    @Override
    public void execute(final JobExecutionContext jec) throws JobExecutionException {
        LogService.log2Console(this, "spoustim naplanovany ping");
        Core core = (Core) jec.getJobDetail().getJobDataMap().get("core");
        Probe probe = (Probe) jec.getJobDetail().getJobDataMap().get("probe");

        HashMap<byte[], Integer> route2internet = core.getDeviceManager().getGateway().getRoute2Internet();
        for (Map.Entry<byte[], Integer> ip : route2internet.entrySet()) {
            ip.setValue(0); // reset counter
            try {
                LogService.log2Console(this, "test adresy " + FormatUtils.ip(ip.getKey()));
                for (int i = 1; i <= PingProbeService.PING_HOP_TEST_COUNT; i++) {
                    sendIcmpRequest(core, probe, ip.getKey(), core.getDeviceManager().getGateway().getMacAsByte());
                    Thread.sleep(1000);
                }
            } catch (InterruptedException ex) {
                LogService.log2ConsoleError(this, ex);
            }
        }

    }

    private void sendIcmpRequest(Core core, Probe probe, byte[] ipdestination, byte[] gatewaymac) {
        try {
            PcapIf activeDevice = core.getNetworkManager().getActiveDevice();

            IcmpPacket icmp = new IcmpPacket(core);
            icmp.ethernetDestination(gatewaymac);
            icmp.ipSource(core.getNetworkManager().getActiveDeviceIPasByte());
            icmp.ipDestination(ipdestination);
            icmp.timeToLive(15);
            icmp.recalculateAllChecksums();

            int state_icmp = Pcap.OK;
            state_icmp += core.getNetworkManager().sendPacket(activeDevice, icmp);
            if (state_icmp == Pcap.OK) {
                LogService.log2Console(probe.getModuleName(), "Ping Packet (hop " + icmp.timeToLive() + ") odeslán");
            }
        } catch (UnknownHostException ex) {
            LogService.log2ConsoleError(this, ex);
        }
    }

}

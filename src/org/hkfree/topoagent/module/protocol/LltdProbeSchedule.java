/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hkfree.topoagent.module.protocol;

import java.util.ConcurrentModificationException;
import org.hkfree.topoagent.core.Core;
import org.hkfree.topoagent.core.LogService;
import org.hkfree.topoagent.domain.LltdQdDiscoveryPacket;
import org.hkfree.topoagent.domain.LltdQdResetPacket;
import org.hkfree.topoagent.interfaces.Probe;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author Filip Valenta
 */
@DisallowConcurrentExecution
public class LltdProbeSchedule implements Job {

    @Override
    public void execute(final JobExecutionContext jec) throws JobExecutionException, ConcurrentModificationException  {
        Core core = (Core) jec.getJobDetail().getJobDataMap().get("core");
        Probe probe = (Probe) jec.getJobDetail().getJobDataMap().get("probe");

        PcapIf activeDevice = core.getNetworkManager().getActiveDevice();

        if (activeDevice != null) {

            LltdQdResetPacket packet_r = new LltdQdResetPacket(core);
            int state_r = Pcap.OK;
            state_r += core.getNetworkManager().sendPacket(activeDevice, packet_r);
            state_r += core.getNetworkManager().sendPacket(activeDevice, packet_r);
            state_r += core.getNetworkManager().sendPacket(activeDevice, packet_r);
            if (state_r == Pcap.OK) {
                LogService.Log2Console(probe.getModuleName(), "LLTD Reset QD Packet (3) odeslán");
            }

            LltdQdDiscoveryPacket packet_qd = new LltdQdDiscoveryPacket(core);
            // LltdTopologyDiscovery packet_qd = new LltdTopologyDiscovery(core);
            int state_qd = Pcap.OK;
            state_qd += core.getNetworkManager().sendPacket(activeDevice, packet_qd);
            if (state_qd == Pcap.OK) {
                LogService.Log2Console(probe.getModuleName(), "LLTD Quick Discovery Packet (1) odeslán");
            }

        }
    }

}

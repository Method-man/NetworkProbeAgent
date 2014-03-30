/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.hkfree.topoagent.modules;

import org.hkfree.topoagent.core.Core;
import org.hkfree.topoagent.core.LogService;
import org.hkfree.topoagent.interfaces.Probe;
import org.hkfree.topoagent.domain.LltdQdDiscoveryPacket;
import org.hkfree.topoagent.domain.LltdQdResetPacket;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author Filip Valenta
 */
public class LltdProbeSchedule implements Job {

    @Override
    public void execute(final JobExecutionContext jec) throws JobExecutionException
    {
        Core core = (Core)jec.getJobDetail().getJobDataMap().get("core");
        Probe probe = (Probe)jec.getJobDetail().getJobDataMap().get("probe");

        PcapIf activeDevice = core.getNetworkManager().getActiveDevice();

        LltdQdResetPacket packet_r = new LltdQdResetPacket(core);
        int state_r = Pcap.OK;
        state_r += core.getNetworkManager().SendPacket(activeDevice, packet_r);
        state_r += core.getNetworkManager().SendPacket(activeDevice, packet_r);
        state_r += core.getNetworkManager().SendPacket(activeDevice, packet_r);
        if(state_r == Pcap.OK) {
            LogService.Log2Console(probe.GetModuleName(), "LLTD Reset QD Packet (3) odeslán");
        }

        LltdQdDiscoveryPacket packet_qd = new LltdQdDiscoveryPacket(core);
        int state_qd = Pcap.OK;
        state_qd += core.getNetworkManager().SendPacket(activeDevice, packet_qd);
        if(state_qd == Pcap.OK) {
            LogService.Log2Console(probe.GetModuleName(), "LLTD Discovery Packet (1) odeslán");
        }
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hkfree.topoagent.module.protocol;

import java.net.UnknownHostException;
import java.util.ConcurrentModificationException;
import jcifs.netbios.NbtAddress;
import org.apache.commons.net.util.SubnetUtils;
import org.apache.commons.net.util.SubnetUtils.SubnetInfo;
import org.hkfree.topoagent.core.Core;
import org.hkfree.topoagent.core.LogService;
import org.hkfree.topoagent.interfaces.Probe;
import org.jnetpcap.packet.format.FormatUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author Filip Valenta
 */
public class NetBIOSScheduleCrate implements Job {

    @Override
    public void execute(final JobExecutionContext jec) throws JobExecutionException, ConcurrentModificationException {
        Core core = (Core) jec.getJobDetail().getJobDataMap().get("core");
        Probe probe = (Probe) jec.getJobDetail().getJobDataMap().get("probe");
        
        byte[] mask = core.getNetworkManager().getActiveDeviceNetmaskAsByte();
        
        SubnetUtils su = new SubnetUtils(core.getNetworkManager().getActiveDeviceIPasString(), FormatUtils.ip(mask));
        SubnetInfo info = su.getInfo();
        
        for(String ip: info.getAllAddresses()) {
            ((NetBIOSProbeService)probe.getProbeService()).GetNetBIOSName(ip);
        }

    }

}

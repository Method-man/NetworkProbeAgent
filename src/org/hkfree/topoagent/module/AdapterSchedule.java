/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hkfree.topoagent.module;

import java.util.ConcurrentModificationException;
import org.hkfree.topoagent.core.Core;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author Filip Valenta
 */
public class AdapterSchedule implements Job {

    @Override
    public void execute(final JobExecutionContext jec) throws JobExecutionException, ConcurrentModificationException {
        Core core = (Core) jec.getJobDetail().getJobDataMap().get("core");
        
        core.getAdapterService().serverXmlSend(core);

    }

}

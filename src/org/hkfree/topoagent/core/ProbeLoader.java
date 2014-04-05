/*
 * Zavadec modulu
 */

package org.hkfree.topoagent.core;

import org.hkfree.topoagent.factory.ArpProbeFactory;
import org.hkfree.topoagent.factory.LltdProbeFactory;
import org.hkfree.topoagent.factory.PingProbeFactory;
import org.hkfree.topoagent.factory.TracerouteProbeFactory;
import org.hkfree.topoagent.interfaces.DeviceObserver;
import org.hkfree.topoagent.interfaces.Probe;
import org.hkfree.topoagent.interfaces.ProbeFactory;
import org.hkfree.topoagent.domain.ScheduleJobCrate;
import org.hkfree.topoagent.module.AdapterService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.quartz.CronTrigger;
import static org.quartz.JobBuilder.newJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import static org.quartz.TriggerBuilder.newTrigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author Filip Valenta
 */
public class ProbeLoader {
    
    private final List<Probe> probes = new ArrayList<>();
    private final Core core;
    
    Scheduler scheduler = null;
    
    public ProbeLoader(Core core)
    {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
        } catch (JobExecutionException ex) {
            LogService.Log2ConsoleError(this, ex);
        } catch(SchedulerException ex) {
            LogService.Log2ConsoleError(this, ex);
        }
        
        this.core = core;
        ProbeFactory pf = new ArpProbeFactory(core);
        probes.add(pf.getProbe());
        pf = new LltdProbeFactory(core);
        probes.add(pf.getProbe());
        pf = new TracerouteProbeFactory(core);
        probes.add(pf.getProbe());
        pf = new PingProbeFactory(core);
        probes.add(pf.getProbe());
    }
    
    public List<Probe> GetProbes()
    {
        return probes;
    }
    
    public void InitBeforeProbes()
    {
        try {
            for(Probe p: probes) {
                p.InitBefore();
                LogService.Log2Console(this, "inicializuji modul: "+p.GetModuleName());
                core.getNetworkManager().Add2Filter(p.GetTcpdumpFilter());
                LogService.Log2Console(p, "pridavam filtr");
                ScheduleJobCrate sjc = p.Schedule();
                if(sjc != null) {
                    SchedulePrepare(sjc, p);
                    LogService.Log2Console(p, "uloha naplanovana");
                }
            }
            
            // scheduler.shutdown();
        } catch (JobExecutionException ex) {
            LogService.Log2ConsoleError(this, ex);
        } catch(SchedulerException ex) {
            LogService.Log2ConsoleError(this, ex);
        }
    }
    
    public void InitAfterProbes()
    {
        for(Probe p: probes) {
            p.InitAfter();
            LogService.Log2Console(p.GetModuleName(), "spouštím init after");
            p.GetProbeService().probeSend();
        }
    }
    
    /**
     * Notify all modules and save info to log
     */
    public void NotifyAllModules()
    {
        core.GetDeviceManager().LogInfo();
        for(Probe p: probes) {
            if(p.GetProbeService() instanceof DeviceObserver) {
                ((DeviceObserver)p.GetProbeService()).Notify();
            }
        }
    }
    
    /**
     * Add scheduled event to scheduler
     * 
     * @param sjc 
     * @param probe 
     * @throws org.quartz.SchedulerException 
     */
    public void SchedulePrepare(ScheduleJobCrate sjc, Probe probe) throws SchedulerException
    {
        Map data = new HashMap();
        data.put("core",core);
        data.put("probe",probe);
        
        JobDetail job = newJob(sjc.getJobClass())
            .usingJobData(new JobDataMap(data))
            .withIdentity(sjc.getJobIdentity(), sjc.getJobGroup())
            .build();

        CronTrigger trigger = newTrigger()
            .withIdentity(sjc.getTriggerIndentity(), sjc.getTriggerGroup())
            .withSchedule(sjc.getCronScheduleBuilder())
            .build();

        scheduler.scheduleJob(job, trigger);
    }
    
}

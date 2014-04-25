/*
 * Zavadec modulu
 */
package org.hkfree.topoagent.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hkfree.topoagent.domain.ScheduleJobCrate;
import org.hkfree.topoagent.factory.ArpProbeFactory;
import org.hkfree.topoagent.factory.LltdProbeFactory;
import org.hkfree.topoagent.factory.NetBIOSFactory;
import org.hkfree.topoagent.factory.PingProbeFactory;
import org.hkfree.topoagent.factory.TracerouteProbeFactory;
import org.hkfree.topoagent.interfaces.DeviceObserver;
import org.hkfree.topoagent.interfaces.Probe;
import org.hkfree.topoagent.interfaces.ProbeFactory;
import org.hkfree.topoagent.module.protocol.NetBIOSProbe;
import org.quartz.CronTrigger;
import static org.quartz.JobBuilder.newJob;
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

    NetBIOSProbe NetBIOS;

    public ProbeLoader(Core core) {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
        } catch (JobExecutionException ex) {
            LogService.Log2ConsoleError(this, ex);
        } catch (SchedulerException ex) {
            LogService.Log2ConsoleError(this, ex);
        }

        this.core = core;
        ProbeFactory pf = new ArpProbeFactory(core);
        probes.add(pf.getProbe());
        pf = new TracerouteProbeFactory(core);
        probes.add(pf.getProbe());
        pf = new PingProbeFactory(core);
        probes.add(pf.getProbe());
        pf = new LltdProbeFactory(core);
        probes.add(pf.getProbe());
        pf = new NetBIOSFactory(core);
        NetBIOS = (NetBIOSProbe) pf.getProbe();
        probes.add(NetBIOS);
    }

    public NetBIOSProbe getNetBIOSProbe() {
        return NetBIOS;
    }

    public List<Probe> getProbes() {
        return probes;
    }

    public void initBeforeProbes() {
        try {
            for (Probe p : probes) {
                p.initBefore();
                LogService.Log2Console(this, "inicializuji modul: " + p.getModuleName());
                core.getNetworkManager().add2Filter(p.getTcpdumpFilter());
                LogService.Log2Console(p, "pridavam filtr");
                ScheduleJobCrate sjc = p.schedule();
                if (sjc != null) {
                    schedulePrepare(sjc, p);
                    LogService.Log2Console(p, "uloha naplanovana");
                }
            }

            // scheduler.shutdown();
        } catch (JobExecutionException ex) {
            LogService.Log2ConsoleError(this, ex);
        } catch (SchedulerException ex) {
            LogService.Log2ConsoleError(this, ex);
        }
    }

    public void initAfterProbes() {
        for (Probe p : probes) {
            p.initAfter();
            LogService.Log2Console(p.getModuleName(), "spouštím init after");
            p.getProbeService().probeSend();
        }
    }

    /**
     * notifyChange all modules and save info to log
     */
    public void notifyAllModules() {
        LogService.logInfo(this, core.getDeviceManager().getAllDevices());
        for (Probe p : probes) {
            if (p.getProbeService() instanceof DeviceObserver) {
                ((DeviceObserver) p.getProbeService()).notifyChange();
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
    public void schedulePrepare(ScheduleJobCrate sjc, Probe probe) throws SchedulerException {
        Map data = new HashMap();
        data.put("core", core);
        data.put("probe", probe);
        schedule(sjc, data);
    }

    /**
     * Add scheduled event to scheduler
     *
     * @param sjc
     * @throws org.quartz.SchedulerException
     */
    public void schedulePrepare(ScheduleJobCrate sjc) throws SchedulerException {
        Map data = new HashMap();
        data.put("core", core);
        schedule(sjc, data);
    }

    private void schedule(ScheduleJobCrate sjc, Map data) throws SchedulerException {
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

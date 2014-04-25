/*
 * Hlavni trida
 */
package org.hkfree.topoagent.core;

import org.hkfree.topoagent.domain.ScheduleJobCrate;
import org.hkfree.topoagent.module.AdapterSchedule;
import org.hkfree.topoagent.module.AdapterService;
import org.hkfree.topoagent.module.ExpertService;
import org.hkfree.topoagent.module.SystemService;
import org.hkfree.topoagent.module.TrayService;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import org.quartz.SchedulerException;

/**
 *
 * @author Filip Valenta
 */
public class Core {

    private NetworkManager network;
    private ProbeLoader probeLoader;
    private DeviceManager deviceManager;

    private AdapterService adapterService;
    private ExpertService expertService;
    private TrayService trayService;
    private SystemService systemService;

    public void init() {
        deviceManager = new DeviceManager();
        expertService = new ExpertService(this);
        systemService = new SystemService();

        network = new NetworkManager(this);
        network.loadNetworkInterfaces();
        
        // adapter service needs instance of network manager
        adapterService = new AdapterService(this);
        LogService.allowdebug = adapterService.isDebug();

        probeLoader = new ProbeLoader(this);
        probeLoader.initBeforeProbes();

        // must be right here due modules dependencies !
        trayService = new TrayService(this);

        network.findActiveDevice();
        network.catchPacketsTrigger();

        probeLoader.initAfterProbes();
        
        // sending data to server
        try {
            getProbeLoader().schedulePrepare(
                    new ScheduleJobCrate(
                            AdapterSchedule.class,
                            "job-adapter-export",
                            "group-adapter",
                            "trigger-adapter",
                            "group-adapter",
                            cronSchedule(getAdapterService().getCronSend2Server())));
        } catch (SchedulerException ex) {
            LogService.Log2ConsoleError(this, ex);
        }

    }

    /*
     * GETTERS, SETTERS
     */
    public DeviceManager getDeviceManager() {
        return deviceManager;
    }

    public NetworkManager getNetworkManager() {
        return network;
    }

    public ProbeLoader getProbeLoader() {
        return probeLoader;
    }

    /**
     * Getters for special modules services
     */
    /**
     * Get module - Adapter
     *
     * @return Adapter
     */
    public AdapterService getAdapterService() {
        return adapterService;
    }

    /**
     * Get module - Expert
     *
     * @return Expert
     */
    public ExpertService getExpertService() {
        return expertService;
    }

    /**
     * Get module - TrayIcon
     *
     * @return TrayIcon
     */
    public TrayService getTrayService() {
        return trayService;
    }
    
    /**
     * Get module - SystemService
     *
     * @return SystemService
     */
    public SystemService getSystemService() {
        return systemService;
    }

}

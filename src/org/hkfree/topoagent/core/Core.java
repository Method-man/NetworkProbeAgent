/*
 * Hlavni trida
 */

package org.hkfree.topoagent.core;

import java.awt.TrayIcon;
import org.hkfree.topoagent.module.AdapterService;
import org.hkfree.topoagent.module.ExpertService;
import org.hkfree.topoagent.module.TrayService;

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
    
    public void Init()
    {
        deviceManager = new DeviceManager();
        adapterService = new AdapterService();
        expertService = new ExpertService(this);
        
        network = new NetworkManager(this);
        network.LoadNetworkInterfaces();
        
        probeLoader = new ProbeLoader(this);
        probeLoader.InitBeforeProbes();
            
        // must be right here due modules dependencies !
        trayService = new TrayService(this);
        
        network.FindActiveDevice();
        network.CatchPacketsTrigger();
        
        probeLoader.InitAfterProbes();
        
    }
    
    /*
     * GETTERS, SETTERS
     */
    
    public DeviceManager GetDeviceManager()
    {
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
    
}

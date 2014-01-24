/*
 * Hlavni trida
 */

package cz.uhk.thesis.core;

import cz.uhk.thesis.model.IcmpPacket;

/**
 *
 * @author Filip Valenta
 */
public class Core {
    
    private NetworkManager network;
    private ProbeLoader probeLoader;
    private DeviceManager deviceManager;
    
    public void Init()
    {
        deviceManager = new DeviceManager();
        
        network = new NetworkManager(this);
        network.LoadNetworkInterfaces();
        
        probeLoader = new ProbeLoader(this);
        probeLoader.InitBeforeProbes();
            
        network.FindActiveDevice();
        network.CatchPacketsTrigger();
        
        getProbeLoader().InitAfterProbes();
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
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.uhk.thesis.modules;

import cz.uhk.thesis.core.Core;
import cz.uhk.thesis.interfaces.Probe;
import org.jnetpcap.packet.JPacket;

/**
 *
 * @author Filip Valenta
 */
public class PingProbe extends Probe {

    public PingProbe(Core core) {
        super(core);
    }

    @Override
    public String GetModuleName() {
        return "Ping";
    }

    @Override
    public boolean useThisModule(JPacket packet) {
        return false;
    }

    @Override
    public void InitBefore() {
        
    }

    @Override
    public void InitAfter() {
        
    }

    @Override
    public String GetTcpdumpFilter() {
        return "icmp";
    }
    
}

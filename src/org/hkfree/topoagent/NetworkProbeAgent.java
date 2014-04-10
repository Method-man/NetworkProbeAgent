package org.hkfree.topoagent;

import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcifs.netbios.NbtAddress;
import org.hkfree.topoagent.core.Core;

/**
 *
 * @author Filip Valenta
 */
public class NetworkProbeAgent {

    private final Core core;

    public NetworkProbeAgent() {
        System.setProperty("log4j.configurationFile", "log4j2.xml");

        // TODO: udelat na to modul !
        try {
            for (NbtAddress na : NbtAddress.getAllByAddress("10.0.0.140")) {
                System.out.println(na.firstCalledName());
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(NetworkProbeAgent.class.getName()).log(Level.SEVERE, null, ex);
        }

        core = new Core();
        core.init();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new NetworkProbeAgent();

    }

}

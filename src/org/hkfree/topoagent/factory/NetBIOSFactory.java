
package org.hkfree.topoagent.factory;

import org.hkfree.topoagent.core.Core;
import org.hkfree.topoagent.interfaces.Probe;
import org.hkfree.topoagent.interfaces.ProbeFactory;
import org.hkfree.topoagent.module.protocol.NetBIOSProbe;
import org.hkfree.topoagent.module.protocol.NetBIOSProbeService;

/**
 *
 * @author Filip Valenta
 */
public class NetBIOSFactory implements ProbeFactory {

    private final Core core;

    public NetBIOSFactory(Core core) {
        this.core = core;
    }

    @Override
    public Probe getProbe() {
        NetBIOSProbe ap = new NetBIOSProbe(core);
        ap.setProbeService(new NetBIOSProbeService(core, ap));
        return ap;
    }

}

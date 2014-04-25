package org.hkfree.topoagent.module.protocol;

import org.hkfree.topoagent.core.Core;
import org.hkfree.topoagent.domain.ScheduleJobCrate;
import org.hkfree.topoagent.interfaces.Probe;
import org.jnetpcap.packet.JPacket;
import static org.quartz.CronScheduleBuilder.cronSchedule;

/**
 *
 * @author Filip Valenta
 */
public class NetBIOSProbe extends Probe {

    public NetBIOSProbe(Core core) {
        super(core);
    }

    @Override
    public String getModuleName() {
        return "NetBIOS";
    }

    @Override
    public boolean useThisModule(JPacket packet) {
        return false;
    }

    @Override
    public void initBefore() {
        
    }

    @Override
    public void initAfter() {
        
    }
    
    @Override
    public ScheduleJobCrate schedule() {
        return new ScheduleJobCrate(NetBIOSScheduleCrate.class, "job-netbios", "group-netbios", "trigger-netbios", "group-netbios",
                cronSchedule(core.getAdapterService().getCronNetBIOS())
        );
    }


}

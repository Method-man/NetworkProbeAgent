
package cz.uhk.thesis.modules;

import cz.uhk.thesis.core.Core;
import cz.uhk.thesis.core.LogService;
import cz.uhk.thesis.interfaces.DeviceObserver;
import cz.uhk.thesis.interfaces.Probe;
import cz.uhk.thesis.interfaces.ProbeService;
import cz.uhk.thesis.interfaces.Stateful;
import cz.uhk.thesis.model.Device;
import cz.uhk.thesis.model.IcmpPacket;
import cz.uhk.thesis.model.Parser;
import cz.uhk.thesis.model.ScheduleJobCrate;
import static cz.uhk.thesis.modules.TracerouteProbeService.STATE_TRACEROUTE_DONE;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Ip4;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import org.quartz.SchedulerException;

/**
 *
 * @author Filip Valenta
 */
public class PingProbeService extends Stateful implements ProbeService, DeviceObserver {

    public static final int STATE_SCHEDULE_STARTED = 1;
    
    public static final int PING_HOP_TEST_COUNT = 3;
    public static final int PING_HOP_TEST_TIMEOUT_SECONDS = 5000;
    
    private int actualTestCounter = 0;
    
    private final Probe probe;
    private final Core core;
    
    public PingProbeService(Core core, Probe probe)
    {
        this.probe = probe;
        this.core = core;
    }

    /**
     * Ping packet from scheduled plan received
     * 
     * @param packet 
     */
    @Override
    public void packetParse(JPacket packet) {
        byte[] destination = ((Ip4)packet.getHeader(new Ip4())).source();
        int type = ((Icmp)packet.getHeader(new Icmp())).type();
        if(type == Icmp.IcmpType.ECHO_REPLY_ID) {
            for(Map.Entry<byte[], Integer> entries: core.GetDeviceManager().GetGateway().GetRoute2Internet().entrySet()) {
                if(Arrays.equals(entries.getKey(), destination)) {
                    entries.setValue(entries.getValue()+1);
                    LogService.Log2Console(this, " dorazila icmp response od "+FormatUtils.ip(destination));
                }
            }
        }
        
    }

    @Override
    public void packetCompare(String ip, byte[] mac, Parser parser) {

    }

    @Override
    public void packetCompare(String ip, byte[] mac) {

    }

    @Override
    public void probeSend() {

    }

    @Override
    public void Notify() {
        Device d = core.GetDeviceManager().GetGateway();
        if(d != null && d.GetRoute2Internet().size() > 0) {
            try {
                if(!IsInState(STATE_SCHEDULE_STARTED)) {
                    core.getProbeLoader().SchedulePrepare(
                        new ScheduleJobCrate(
                            PingProbeSchedule.class, 
                            "job-ping", "group-ping", "trigger-ping", "group-ping", 
                            cronSchedule("0 0/5 * * * ?")
                        ),
                        probe
                    );
                    SetState(STATE_SCHEDULE_STARTED);
                }
            } catch (SchedulerException ex) {
                LogService.Log2ConsoleError(this, ex);
            }
        }
    }

    @Override
    public void SetState(int state) {
        switch(state) {
            case STATE_SCHEDULE_STARTED:
            {
                this.state = state;
            } break;
            default: break;
        }
    }
    
}

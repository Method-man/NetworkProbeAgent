package org.hkfree.topoagent.module.protocol;

import java.util.Arrays;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.hkfree.topoagent.core.Core;
import org.hkfree.topoagent.core.LogService;
import org.hkfree.topoagent.domain.Device;
import org.hkfree.topoagent.domain.ScheduleJobCrate;
import org.hkfree.topoagent.interfaces.DeviceObserver;
import org.hkfree.topoagent.interfaces.Parser;
import org.hkfree.topoagent.interfaces.Probe;
import org.hkfree.topoagent.interfaces.ProbeService;
import org.hkfree.topoagent.interfaces.Stateful;
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
    public static final int STATE_LAST_SEND = 2;

    public static final int PING_HOP_TEST_COUNT = 3;
    public static final int PING_HOP_TEST_TIMEOUT_SECONDS = 8000;

    private int actualTestCounter = 0;

    private final Probe probe;
    private final Core core;

    public PingProbeService(Core core, Probe probe) {
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

        byte[] destination = ((Ip4) packet.getHeader(new Ip4())).source();
        int type = ((Icmp) packet.getHeader(new Icmp())).type();
        if (type == Icmp.IcmpType.ECHO_REPLY_ID) {
            for (Map.Entry<byte[], Integer> entries : core.getDeviceManager().getGateway().getRoute2Internet().entrySet()) {
                if (Arrays.equals(entries.getKey(), destination)) {
                    entries.setValue(entries.getValue() + 1);
                    LogService.Log2Console(this, " dorazila icmp response od " + FormatUtils.ip(destination));
                }
            }
            if (isInState(STATE_LAST_SEND)) {
                triggerTraceCheck();
                setState(STATE_SCHEDULE_STARTED);
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
    public void notifyChange() {
        Device d = core.getDeviceManager().getGateway();
        if (d != null && d.getRoute2Internet().size() > 0) {
            try {
                if (isInState(STATE_INITIAL)) {
                    core.getProbeLoader().schedulePrepare(
                            new ScheduleJobCrate(
                                    PingProbeSchedule.class,
                                    "job-ping", "group-ping", "trigger-ping", "group-ping",
                                    cronSchedule(core.getAdapterService().getCronPing())
                            ),
                            probe
                    );
                    setState(STATE_SCHEDULE_STARTED);
                }
            } catch (SchedulerException ex) {
                LogService.Log2ConsoleError(this, ex);
            }
        }
    }

    @Override
    public void setState(int state) {
        switch (state) {
            case STATE_SCHEDULE_STARTED:
            case STATE_LAST_SEND: {
                this.state = state;
            }
            break;
            default:
                break;
        }
    }
    
    /**
     * Trigger trace hops check
     */
    private void triggerTraceCheck() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                if (((PingProbeService) probe.getProbeService()).areAllHopsOk()) {
                    core.getExpertService().showTraceIsFeasible();
                }
                else {
                    core.getExpertService().showTraceIsBad();
                }
            }
        }, PING_HOP_TEST_TIMEOUT_SECONDS);
    }

    /**
     * Iterate through all hops and check packet loss
     *
     * @return
     */
    public boolean areAllHopsOk() {
        boolean allOk = true;
        for (Map.Entry<byte[], Integer> ip : core.getDeviceManager().getGateway().getRoute2Internet().entrySet()) {
            if (ip.getValue() == 0) {
                allOk = false;
            }
        }
        return allOk;
    }

}

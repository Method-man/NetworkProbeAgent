
package cz.uhk.thesis.model;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;

/**
 * Crate for store module schedule event settings
 * source: http://www.quartz-scheduler.org/
 * 
 * @author Filip Valenta
 */
public class ScheduleJobCrate {
 
    private Class<? extends Job> jobClass = null;
    private String jobIdentity = null;
    private String jobGroup = null;
    
    private String triggerIndentity = null;
    private String triggerGroup = null;
    private CronScheduleBuilder cronScheduleBuilder = null;
    
    public ScheduleJobCrate(
            Class<? extends Job> jobClass, 
            String jobIdentity, 
            String jobGroup,
            String triggerIdentity,
            String triggerGroup,
            CronScheduleBuilder cronScheduleBuilder)
    {
        // job info
        this.jobClass = jobClass;
        this.jobIdentity = jobIdentity;
        this.jobGroup = jobGroup;
        
        // trigger info
        this.triggerIndentity = triggerIdentity;
        this.triggerGroup = triggerGroup;
        this.cronScheduleBuilder = cronScheduleBuilder;
    }

    public Class<? extends Job> getJobClass() {
        return jobClass;
    }

    public String getJobIdentity() {
        return jobIdentity;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public String getTriggerIndentity() {
        return triggerIndentity;
    }

    public String getTriggerGroup() {
        return triggerGroup;
    }

    public CronScheduleBuilder getCronScheduleBuilder() {
        return cronScheduleBuilder;
    }
    
}

package io.mateu.erp.model.workflow;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

/**
 * Created by miguel on 28/4/17.
 */
public class Scheduler {

    static org.quartz.Scheduler scheduler = null;

    public Scheduler() {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

    }


    public void start() throws SchedulerException {
            // and start it off
            scheduler.start();

    }

    public static void shutdown() throws SchedulerException {
        scheduler.shutdown();
    }


    public static void main(String... args) throws SchedulerException {
//        // define the job and tie it to our HelloJob class
//        JobDetail job = new Job(HelloJob.class)
//                .withIdentity("job1", "group1")
//                .build();
//
//        // Trigger the job to run now, and then repeat every 40 seconds
//        Trigger trigger = new Trigger()
//                .withIdentity("trigger1", "group1")
//                .startNow()
//                .withSchedule(simpleSchedule()
//                        .withIntervalInSeconds(40)
//                        .repeatForever())
//                .build();
//
//        // Tell quartz to schedule the job using our trigger
//        scheduler.scheduleJob(job, trigger);
    }

}

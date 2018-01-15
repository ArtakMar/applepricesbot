package ru.proshik.applepriceparcer.service.scheduler;

import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import ru.proshik.applepriceparcer.service.OperationService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.quartz.SimpleScheduleBuilder.repeatSecondlyForever;

public class QuartzDefaultScheduler {

    private static final Logger LOG = Logger.getLogger(QuartzDefaultScheduler.class);

    private static final int MINUTE_INTERVAL = 515;

    private OperationService operationService;

    public QuartzDefaultScheduler(OperationService operationService) {
        this.operationService = operationService;
    }

    public void init() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        JobDetail job = JobBuilder.newJob(ShopJob.class)
                .withIdentity("ShopJob", "default5")
                .build();

        LocalDateTime now = LocalDateTime.now().plusSeconds(5);

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("ShopTrigger", "default")
                .startAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .withSchedule(repeatSecondlyForever(MINUTE_INTERVAL)
                        .repeatForever())
                .build();

        // Tell quartz to schedule the job using our trigger
        scheduler.getContext().put(ShopJob.OPERATION_SERVICE_LABEL, operationService);

        scheduler.scheduleJob(job, trigger);
        scheduler.start();

        LOG.info("Scheduler with repeat interval=" + MINUTE_INTERVAL + "was started!");
    }

}

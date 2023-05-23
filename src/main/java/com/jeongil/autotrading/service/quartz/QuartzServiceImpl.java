package com.jeongil.autotrading.service.quartz;

import com.jeongil.autotrading.common.exception.JobException;
import com.jeongil.autotrading.common.properties.JobProperties;
import com.jeongil.autotrading.scheduler.QuartzJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class QuartzServiceImpl implements QuartzService{

   private static final Logger logger = LoggerFactory.getLogger(QuartzServiceImpl.class);
   
   @Autowired
   private SchedulerFactoryBean schedulerFactoryBean;

   @Autowired
   private JobLauncher jobLauncher;

   @Autowired
   private JobLocator jobLocator;

   @Autowired
   private JobProperties jobProperties;
   
   private Scheduler scheduler = null;
   
   @PostConstruct
   public void init(){
      scheduler = schedulerFactoryBean.getScheduler();
   }
   
   /**
    * 스케쥴러에 스케쥴 등록
    * @throws Exception
    */
   @Override
   public void register() throws Exception {
      addJob(jobProperties.getName(), jobProperties.getCron());
   }

   /**
    * add job
    * @param jobName
    * @param cron
    * @throws Exception
    */
   private void addJob(String jobName, String cron) {
      try {
         JobDetail jobDetail = createJobDetail(jobName);
         CronTrigger cronTrigger = createCronTrigger(jobName, cron);
         scheduler.scheduleJob(jobDetail, cronTrigger);
      }
      catch (SchedulerException e) {
         logger.error("addJob error!!", e);
         throw JobException.ofError("quartz.job.add.Exception");
      }
   }

   /**
    * pause job
    */
   @Override
   public ResponseEntity<?> pauseJob(String jobName, String adminId) {
      JobKey jobKey = JobKey.jobKey(jobName);
      try {
         if (!scheduler.checkExists(jobKey)) {
            throw JobException.ofError("quartz.job.NotFoundException");
         }
         /*
          * pause job
          */
         scheduler.pauseJob(jobKey);
      }
      catch (SchedulerException e) {
         logger.error("addJob error!!", e);
         throw JobException.ofError("quartz.job.SchedulerException");
      }

      return ResponseEntity.ok().build();
   }

   /**
    * resume job
    * @param jobName
    * @return
    */
   @Override
   public ResponseEntity<?> resumeJob(String jobName, String adminId){
      JobKey jobKey = JobKey.jobKey(jobName);
      try {
         if (!scheduler.checkExists(jobKey)) {
            throw JobException.ofError("quartz.job.NotFoundException");
         }
         scheduler.resumeJob(jobKey);
      }
      catch (SchedulerException e) {
         logger.error("resumeJob error!!", e);
         throw JobException.ofError("quartz.job.SchedulerException");
      }

      return ResponseEntity.ok().build();
   }

   /**
    * JobDetail 생성
    * @param jobName
    * @return
    */
   private JobDetail createJobDetail(String jobName) {
      JobDataMap jobDataMap = new JobDataMap();
      jobDataMap.put("jobName", jobName);
      jobDataMap.put("jobLauncher", jobLauncher);
      jobDataMap.put("jobLocator", jobLocator);
      
      JobDetail jobDetail = JobBuilder.newJob(QuartzJob.class)
            .withIdentity(jobName)
            .setJobData(jobDataMap)
            .storeDurably()
            .build();

      return jobDetail;
   }

   /**
    * CronTrigger 생성
    * @param jobNm
    * @param cron
    * @return
    */
   private CronTrigger createCronTrigger(String jobNm, String cron) {
      return TriggerBuilder.newTrigger()
            .withIdentity(new JobKey(jobNm).getName())
            .withSchedule(CronScheduleBuilder.cronSchedule(cron))
            .build();
   }

   /**
    * 스케쥴러 시작
    * @throws SchedulerException
    */
   @Override
   public void start() throws SchedulerException {
      if(scheduler != null && !scheduler.isStarted()) {
         scheduler.start();
      }
   }
   
   /**
    * 스케쥴러 종료
    * @throws SchedulerException
    * @throws InterruptedException
    */
   @Override
   public void shutdown() throws SchedulerException, InterruptedException {
      if(scheduler != null && !scheduler.isShutdown()) {
         scheduler.shutdown();
      }
   }
   
   /**
    * 스케쥴러 클리어
    * @throws SchedulerException
    */
   @Override
   public void clear() throws SchedulerException {
      scheduler.clear();
   }
   
   /**
    * 스케쥴러 리스너 등록
    * @param jobListener
    * @throws SchedulerException
    */
   @Override
   public void addListener(JobListener jobListener) throws SchedulerException {
      scheduler.getListenerManager().addJobListener(jobListener);
   }
}
package com.jeongil.autotrading.scheduler;

import com.jeongil.autotrading.service.quartz.QuartzService;
import org.springframework.beans.factory.annotation.Autowired;

public class QuartzStarter {
   
   @Autowired
   private QuartzService quartzService;
   
   /**
    * init Quartz
    * @throws Exception
    */
   public void init() throws Exception {
      quartzService.clear();
      quartzService.addListener(new QuartzListener());
      quartzService.register();
      quartzService.start();
   }

   /**
    * destroy Quartz
    * @throws Exception
    */
   public void destroy() throws Exception {
      quartzService.shutdown();
   }
   
}

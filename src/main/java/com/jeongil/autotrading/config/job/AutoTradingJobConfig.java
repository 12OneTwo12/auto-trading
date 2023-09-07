package com.jeongil.autotrading.config.job;

import com.jeongil.autotrading.service.trading.AutoTradingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Duration;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AutoTradingJobConfig {
    private final static String PROCESS_METHOD = "process";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    private final AutoTradingService autoTradingService;

    @Autowired
    private final PlatformTransactionManager masterTransactionManager;

    /*
     * Loaner Sms Job
     */
    @Bean
    public Job autoTradingJob() {
        LocalDateTime from = LocalDateTime.now();

        Job job = jobBuilderFactory.get("autoTradingJob")
                .start(autoTradingJobStep())
                .build();

        LocalDateTime to = LocalDateTime.now();
        log.info("[ autoTradingJob run time : " +  Duration.between(from.toLocalTime(), to.toLocalTime()).getSeconds() + "s ]");

        return job;
    }

    @Bean
    public Step autoTradingJobStep() {
        return stepBuilderFactory.get("autoTradingJobStep")
                .transactionManager(masterTransactionManager)
                .tasklet(autoTradingTasklet()).build();
    }

    @Bean
    public MethodInvokingTaskletAdapter autoTradingTasklet() {
        MethodInvokingTaskletAdapter adapter = new MethodInvokingTaskletAdapter();

        adapter.setTargetObject(autoTradingService);
        adapter.setTargetMethod(PROCESS_METHOD);

        return adapter;
    }
}

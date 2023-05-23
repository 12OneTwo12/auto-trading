package com.jeongil.autotrading.scheduler;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class QuartzJob extends QuartzJobBean {
    private String jobName;
    private JobLauncher jobLauncher;
    private JobLocator jobLocator;

    /**
     * get jobName
     * @return
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * set jobName
     * @param jobName
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * get jobLauncher
     * @return
     */
    public JobLauncher getJobLauncher() {
        return jobLauncher;
    }

    /**
     * set jobLauncher
     * @param jobLauncher
     */
    public void setJobLauncher(JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }

    /**
     * get jobLocator
     * @return
     */
    public JobLocator getJobLocator() {
        return jobLocator;
    }

    /**
     * set jobLocator
     * @param jobLocator
     */
    public void setJobLocator(JobLocator jobLocator) {
        this.jobLocator = jobLocator;
    }

    /**
     * set execute Internal
     * @param context
     * @throws JobExecutionException
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            Job job = jobLocator.getJob(jobName);
            JobParameters params = new JobParametersBuilder()
                    .addString("JobID", String.valueOf(System.currentTimeMillis()))
                    .toJobParameters();

            jobLauncher.run(job, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

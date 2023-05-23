package com.jeongil.autotrading.service.quartz;

import org.quartz.JobListener;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;

public interface QuartzService {
    /**
     * 스케쥴러에 스케쥴 등록
     * @throws Exception
     */
    void register() throws Exception;

    /**
     * 스케쥴러 시작
     * @throws SchedulerException
     */
    void start() throws SchedulerException;

    /**
     * 스케쥴러 종료
     * @throws SchedulerException
     * @throws InterruptedException
     */
    void shutdown() throws SchedulerException, InterruptedException;

    /**
     * 스케쥴러 클리어
     * @throws SchedulerException
     */
    void clear() throws SchedulerException;

    /**
     * 스케쥴러 리스너 등록
     * @param jobListener
     * @throws SchedulerException
     */
    void addListener(JobListener jobListener) throws SchedulerException;

    /**
     * pause job
     */
    ResponseEntity<?> pauseJob(String jobName, String adminId) throws SchedulerException;

    /**
     * resume job
     */
    ResponseEntity<?> resumeJob(String jobName, String adminId);
}

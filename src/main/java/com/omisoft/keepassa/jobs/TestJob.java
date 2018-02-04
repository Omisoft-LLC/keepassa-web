package com.omisoft.keepassa.jobs;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Example job Created by dido on 10/21/16.
 */
@Slf4j
public class TestJob implements Job {

  public void execute(JobExecutionContext context) throws JobExecutionException {
    log.info("Test JOB!");
  }
}

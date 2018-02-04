package com.omisoft.keepassa.jobs;

import com.google.inject.Inject;
import com.omisoft.keepassa.dao.GroupDAO;
import com.omisoft.server.common.exceptions.DataBaseException;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by nslavov on 1/10/17.
 */
@Slf4j
public class CleanupJob implements Job {

  @Inject
  private GroupDAO groupDao;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    log.info("CleanupJob");
    try {
      groupDao.deleteEmptyGroup();
    } catch (DataBaseException e) {
      log.error("ERROR EXECUTING JOB:", e);
      throw new JobExecutionException(e);
    }
  }
}

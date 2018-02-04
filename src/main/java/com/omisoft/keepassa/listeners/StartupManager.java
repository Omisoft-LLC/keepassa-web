package com.omisoft.keepassa.listeners;

import static com.omisoft.keepassa.constants.Constants.LDAP;
import static com.omisoft.keepassa.constants.Constants.LDAP_DOMAIN_NAME;
import static com.omisoft.keepassa.constants.Constants.LDAP_GROUP;
import static com.omisoft.keepassa.constants.Constants.LDAP_PASSWORD;
import static com.omisoft.keepassa.constants.Constants.LDAP_SERVER;
import static com.omisoft.keepassa.constants.Constants.LDAP_USER;
import static com.omisoft.keepassa.constants.Constants.MUTUAL_SSL;
import static com.omisoft.keepassa.constants.Constants.SMTP_PASSWORD;
import static com.omisoft.keepassa.constants.Constants.SMTP_PORT;
import static com.omisoft.keepassa.constants.Constants.SMTP_SERVER;
import static com.omisoft.keepassa.constants.Constants.SMTP_TLS;
import static com.omisoft.keepassa.constants.Constants.SMTP_USER;
import static com.omisoft.keepassa.constants.Constants.SYSTEM_INIT;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.omisoft.keepassa.configuration.Configuration;
import com.omisoft.keepassa.configuration.FileConfigService;
import com.omisoft.keepassa.constants.Constants;
import com.omisoft.keepassa.dao.SettingsDAO;
import com.omisoft.keepassa.dao.UserDAO;
import com.omisoft.keepassa.di.GuiceJobFactory;
import com.omisoft.keepassa.entities.settings.Settings;
import com.omisoft.keepassa.entities.settings.SettingsTypeEnum;
import com.omisoft.keepassa.jobs.TestJob;
import com.omisoft.server.common.exceptions.DataBaseException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.persistence.EntityManager;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Startup singleton managed bean Created by dido on 10/11/16.
 */

@Slf4j
@Data
public class StartupManager {


  private final UserDAO userDAO;
  private final SettingsDAO settingsDAO;
  private final EntityManager sessionFactory;
  private final Injector injector;
  private String buildVersion;
  private String buildTimestamp;
  private Date startupDate;

  @Inject
  public StartupManager(UserDAO userDAO, SettingsDAO settingsDAO, Injector injector,
      EntityManager sessionFactory) throws DataBaseException {
    this.injector = injector;
    this.userDAO = userDAO;
    this.settingsDAO = settingsDAO;
    this.sessionFactory = sessionFactory;
    Configuration configuration = FileConfigService.getInstance().getConfig();
    if (configuration.getMode().equals(Constants.DEV_MODE) || configuration.getMode()
        .equals(Constants.HOSTED_MODE)) {
//      addTestUser();
    }
    addDefaultSettings();
    addBuildInfo();

    setupJobs();
  }


  private void setupJobs() {

    try {
      Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
      scheduler.setJobFactory(injector.getInstance(GuiceJobFactory.class));
      JobKey jobKey = JobKey.jobKey("testJob", "myJobGroup");

      JobDetail job = JobBuilder.newJob(TestJob.class).withIdentity(jobKey).storeDurably().build();
      scheduler.addJob(job, true);
      scheduler.triggerJob(jobKey);

//TODO JOB CLEAN GROUPS - SET START TIMER
//      JobKey jobKeyClean = JobKey.jobKey("CleanupJob", "JobGroup");
//      JobDetail jobClean = JobBuilder.newJob(CleanupJob.class).withIdentity(jobKeyClean).storeDurably().build();
//      scheduler.addJob(jobClean, true);
//      scheduler.triggerJob(jobKeyClean);

      // Trigger trigger = TriggerBuilder
      // .newTrigger()
      // .withIdentity("test1", "group1")
      // .withSchedule(
      // SimpleScheduleBuilder.simpleSchedule()
      // .withRepeatCount(1).w)
      // .build();

      // scheduler.scheduleJob(job, trigger);
    } catch (SchedulerException e) {
      log.error("SCHEDULE EXCEPTION", e);
    }


  }


  private void addBuildInfo() {
    startupDate = new Date();
    InputStream in =
        getClass().getClassLoader().getResourceAsStream("buildinfo/buildInfo.properties");
    if (in == null) {
      return;
    }

    Properties props = new Properties();
    try {
      props.load(in);
    } catch (IOException e) {
      log.error("IO EXCEPTION", e);

    }

    buildVersion = props.getProperty("build.version");
    buildTimestamp = props.getProperty("build.timestamp");

    // etc.
  }


  /**
   * Create test user
   */
//  private void addTestUser() {
//    log.info("ADD TEST USER");
//    if (userDao.count() == 0) {
//      User user = new User();
//      user.setEmail("code@omisoft.eu");
//      user.setFirstName("Tester");
//      user.setLastName("TESTOV");
//      user.setAbout("About user");
//      user.setContactInfo(new ContactInfo());
//      try {
//        String testPassword = DigestUtils.sha512Hex("test");
//        log.info("TEST PASSWORD:"+testPassword);
//        SecureString password = new SecureString(testPassword);
//        user.setSecureKeystore(SecureKeystore.createKeyStore(user.getId(), user.getEmail(),
//            Constants.MASTER_PASSWORD, password, Constants.CONSTANT_PASSWORD, true));;
//      } catch (SecurityException e) {
//        log.error("ERROR IN KEYSTORE CREATION", e);
//      }
//      userDao.saveOrUpdate(user);
//    }
//
//  }
  private void addDefaultSettings() throws DataBaseException {
    EntityManager session = sessionFactory;
    session.getTransaction().begin();
    if (settingsDAO.count() == 0) {
      // Add default settings to DB
      List<Settings> settingsList = new ArrayList<>();

      settingsList.add(new Settings(SYSTEM_INIT, "true", "true",
          "Is system inited for first time", SettingsTypeEnum.BOOLEAN));
      settingsList.add(new Settings(SMTP_SERVER, "smtp.googlemail.com", "", "SMTP SERVER",
          SettingsTypeEnum.STRING));
      settingsList.add(new Settings(SMTP_PORT, "465", "", "SMTP PORT", SettingsTypeEnum.INTEGER));
      settingsList.add(new Settings(SMTP_USER, "support@omisoft.eu", "", "SMTP USER",
          SettingsTypeEnum.STRING));
      settingsList.add(new Settings(SMTP_PASSWORD, "support123@1", "", "SMTP Password",
          SettingsTypeEnum.STRING));
      settingsList
          .add(new Settings(SMTP_TLS, "true", "true", "Use TLS", SettingsTypeEnum.BOOLEAN));
      settingsList
          .add(new Settings(MUTUAL_SSL, "false", "false", "Mutual SSL setting",
              SettingsTypeEnum.BOOLEAN));

      //LDAP Settings
      settingsList
          .add(new Settings(LDAP, "false", "false", "LDAP settings", SettingsTypeEnum.BOOLEAN));
      settingsList.add(new Settings(LDAP_SERVER, "192.168.4.36", "192.168.4.36", "LDAP SERVER",
          SettingsTypeEnum.STRING));
      settingsList
          .add(new Settings(LDAP_USER, "dido", "dido", "LDAP USER", SettingsTypeEnum.STRING));
      settingsList.add(new Settings(LDAP_PASSWORD, "asdqwe123!@#", "", "LDAP USER PASSWORD",
          SettingsTypeEnum.STRING));
      settingsList
          .add(new Settings(LDAP_GROUP, "", "", "LDAP GROUP (OPTIONAL)", SettingsTypeEnum.STRING));
      settingsList.add(new Settings(LDAP_DOMAIN_NAME, "OMISOFT", "", "LDAP DOMAIN NAME ",
          SettingsTypeEnum.STRING));

      settingsDAO.persistAll(settingsList);
    }
    session.getTransaction().commit();

  }
}

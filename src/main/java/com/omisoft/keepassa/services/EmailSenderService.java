package com.omisoft.keepassa.services;

import static com.omisoft.keepassa.constants.Constants.SMTP_PASSWORD;
import static com.omisoft.keepassa.constants.Constants.SMTP_PORT;
import static com.omisoft.keepassa.constants.Constants.SMTP_SERVER;
import static com.omisoft.keepassa.constants.Constants.SMTP_USER;

import com.omisoft.keepassa.constants.EmailConstants;
import com.omisoft.keepassa.constants.RestUrl;
import com.omisoft.keepassa.dao.SettingsDAO;
import com.omisoft.keepassa.entities.settings.Settings;
import com.omisoft.keepassa.exceptions.NotFoundException;
import com.omisoft.keepassa.templates.AddUserTMP;
import com.omisoft.keepassa.templates.BaseTMP;
import com.omisoft.keepassa.templates.InvitedTMP;
import com.omisoft.keepassa.templates.RegisterTMP;
import com.omisoft.keepassa.templates.SendGroupMessageTMP;
import com.omisoft.keepassa.templates.SendNormalEmailTMP;
import com.omisoft.keepassa.templates.SendRegInvitationEmail;
import com.omisoft.keepassa.templates.SharePasswordTMP;
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.mail.internet.InternetAddress;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;

/**
 * Creates emails Created by dido on 17.05.16.
 */
@Singleton
@Slf4j
public class EmailSenderService {


  public static final String STYLE_TAG_END = "</style>";
  private final Configuration conf;
  private final SettingsDAO settingsDAO;

  @Inject
  public EmailSenderService(Configuration conf, SettingsDAO settingsDAO) {
    this.conf = conf;
    this.settingsDAO = settingsDAO;
  }

  /**
   * Getting the email instance.
   */
  protected HtmlEmail getEmail() {
    HtmlEmail email = new HtmlEmail();
    try {
      email.setHostName(getSMTPServer());
      email.setSmtpPort(Integer.parseInt(getSMTPPort()));
      email.setAuthenticator(
          new DefaultAuthenticator(EmailConstants.EMAIL_TO, getSMTPPassword()));
      email.setSSLOnConnect(true);
      email.setFrom(getSMTPUser());

      email.setReplyTo(
          Arrays.asList(new InternetAddress(EmailConstants.NO_REPLY)));

    } catch (Exception e) {
      log.error("GENERIC EXCEPTION", e);
    }
    return email;
  }

  /**
   * Setting the logo.
   *
   * @param email current email instance.
   */
  protected String setLogo(HtmlEmail email) {
    String result = null;
    try {
      // TODO change with domain name
      result = email
          .embed(RestUrl.SERVER_NAME + "/images/KP_LOGO_600x150_for_email.png", "Keepassa Logo");
    } catch (Exception e) {
//      log.error("GENERIC EXCEPTION", e);
      log.error("EMAIL SENDER SERVICE - INVALID EMAIL");
    }
    return result;
  }

  /**
   * Method to send a registration confirmation email.
   *
   * @param to email recipient
   */
  public HtmlEmail sendRegistrationVerificationEmail(String to) {
    HtmlEmail email = null;
    try {
      email = getEmail();
      send(email, to, EmailConstants.SBJ_REGISTRY, new RegisterTMP("Registration", setLogo(email)),
          EmailConstants.TMP_REGISTER);
    } catch (Exception e) {
//      log.error("GENERIC EXCEPTION", e);
      log.error("EMAIL SENDER SERVICE - INVALID EMAIL");
    }

    return email;
  }

  /**
   * Method to send the invitation to a specified group.
   *
   * @param to recipient
   * @param adminEmail adminEmail email
   */
  public HtmlEmail sendGroupNotifyEmail(String to, String adminEmail, String groupName) {
    HtmlEmail email = null;
    try {
      email = getEmail();
      send(email, to, EmailConstants.SBJ_INVITED,
          new InvitedTMP(adminEmail, setLogo(email), groupName), EmailConstants.TMP_INVITED);
    } catch (Exception e) {
      log.error("GENERIC EXCEPTION", e);
    }

    return email;
  }

  public HtmlEmail sendAddCompanyUsersEmail(String to, String adminEmail, String subject,
      String messsage, String inviteCode) {
    HtmlEmail email = null;
    try {
      email = getEmail();
      send(email, to, subject,
          new AddUserTMP(adminEmail, setLogo(email), messsage, inviteCode),
          EmailConstants.TMP_ADD_USERS);
    } catch (Exception e) {
      log.error("GENERIC EXCEPTION", e);
    }

    return email;
  }

  public void sendShareNotifyEmail(String toEmail, String senderEmail, String passwordSafeName) {
    HtmlEmail email;
    try {
      email = getEmail();
      send(email, toEmail, EmailConstants.SBJ_SHARE_PASSWORD,
          new SharePasswordTMP(senderEmail, setLogo(email), passwordSafeName),
          EmailConstants.TMP_SHARE_NOTIFY);
    } catch (Exception e) {
      log.error("GENERIC EXCEPTION", e);
    }

  }


  public void sendMessage(String emailFrom, String emailTo, String subject, String message) {
    HtmlEmail email;
    try {
      email = getEmail();
      email.addReplyTo(emailFrom);
      email.getReplyToAddresses().remove(0);
      send(email, emailTo, subject, new SendNormalEmailTMP(emailFrom, message, setLogo(email)),
          EmailConstants.TMP_NORMAL_MESSAGE);
    } catch (Exception e) {
      log.error("GENERIC EXCEPTION", e);
    }
  }

  public void sendGroupMessage(String emailFrom, String groupEmail, String subject,
      String message) {
    HtmlEmail email;
    try {
      email = getEmail();
      email.addReplyTo(emailFrom);
      email.getReplyToAddresses().remove(0);
      send(email, groupEmail, subject, new SendGroupMessageTMP(emailFrom, message, setLogo(email)),
          EmailConstants.TMP_GROUP_MESSAGE);
      MultiPartEmail multipartEmail = new MultiPartEmail();
    } catch (Exception e) {
      log.error("GENERIC EXCEPTION", e);
    }
  }

  public void sendKeepassaInvitationEmail(String from, String to) {
    HtmlEmail email;
    try {
      email = getEmail();
      send(email, to, EmailConstants.SBJ_INVITED,
          new SendRegInvitationEmail(from, setLogo(email)),
          EmailConstants.TMP_KEEPASSA_INVITE);
    } catch (Exception e) {
      log.error("GENERIC EXCEPTION", e);
    }
  }

  private void send(HtmlEmail email, String to, String subject, BaseTMP tmp, String tmpFile) {

    StringWriter out = new StringWriter();
    try {
      email.setSubject(subject);
      email.addTo(to);
      Map<String, Object> map = new HashMap<>();
      map.put(EmailConstants.TMP_BIND_NAME, tmp);
      Template template = conf.getTemplate(tmpFile, "UTF-8");
      template.process(map, out);
      String transformedTmp = out.toString();
      email.setHtmlMsg(transformedTmp);
      // Strip CSS
      String plainText = transformedTmp
          .substring(transformedTmp.indexOf(STYLE_TAG_END) + STYLE_TAG_END.length());
      // Strip html
      email.setTextMsg(plainText.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " "));
      email.setCharset("UTF-8");
      email.send();
    } catch (Exception e) {
      log.error("GENERIC EXCEPTION", e);
    }
  }

  private String getSMTPServer() throws NotFoundException {
    Settings SMTPServer = settingsDAO.findSettingByName(SMTP_SERVER);
    return SMTPServer.getValue();
  }

  private String getSMTPPort() throws NotFoundException {
    Settings SMTPPort = settingsDAO.findSettingByName(SMTP_PORT);
    return SMTPPort.getValue();
  }

  private String getSMTPUser() throws NotFoundException {
    Settings SMTPUser = settingsDAO.findSettingByName(SMTP_USER);
    return SMTPUser.getValue();
  }

  private String getSMTPPassword() throws NotFoundException {
    Settings SMTPPassword = settingsDAO.findSettingByName(SMTP_PASSWORD);
    return SMTPPassword.getValue();
  }
}


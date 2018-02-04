package com.omisoft.keepassa.constants;

/**
 * Created by nslavov on 6/13/16.
 */
public class EmailConstants {

  // config
  public static final String EMAIL_HOST = "smtp.googlemail.com";
  public static final int EMAIL_PORT = 465;
  public static final String EMAIL_FROM = "support@omisoft.eu";
  public static final String EMAIL_TO = "support@omisoft.eu";
  public static final String PASSWORD = "support123@1";

  // binding
  public static final String TMP_BIND_NAME = "info";

  // templates
  public static final String TMP_REGISTER = "email_templates/register.ftl";
  public static final String TMP_INVITED = "email_templates/invited.ftl";
  public static final String TMP_SHARE_NOTIFY = "email_templates/share_notify.ftl";
  public static final String TMP_NORMAL_MESSAGE = "email_templates/normal_message.ftl";
  public static final String TMP_GROUP_MESSAGE = "email_templates/group_message.ftl";
  public static final String TMP_ADD_USERS = "email_templates/add_user.ftl";
  public static final String TMP_KEEPASSA_INVITE =
      "email_templates/unregistered_invite.ftl";


  // subjects
  public static final String NO_REPLY = "no-reply@omisoft.eu";
  public static final String SBJ_REGISTRY = "Keepassa registration";
  public static final String SBJ_INVITED = "You have been invited!";
  public static final String SBJ_SHARE_PASSWORD = "You have a new password";
}

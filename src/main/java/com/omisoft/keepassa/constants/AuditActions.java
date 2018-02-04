package com.omisoft.keepassa.constants;

/**
 * Holds audit actions
 * Created by dido on 27.01.17.
 */
public interface AuditActions {

  String REGISTER_ACTION = "New User registered";
  String RESET_PASSWORD_ACTION = "Password reset by user";
  String CREATE_NEW_DEPARTMENT = "New Department created";
  String SET_DEPARTMENT_HEAD = "Became Department head";
  String UPDATE_USER_INFO = "Updated user profile info";
  String UPDATE_USER_SECURITY = "Update user security settings";
  String REMOVE_USER = "Remove user";
  String SUSPEND_USER = "Suspend user";
  String UPDATE_DEPARTMENT = "Update department info";
  String REMOVE_DEPARTMENT = "Remove department";
  String REMOVE_USER_FROM_DEPARTMENT = "Remove user from department";
  String ADD_USER_TO_DEPARTMENT = "Add user to department";
  String UPDATE_SETTINGS = "Update settings";
  String UPDATE_TLS_SETTINGS = "Update TLS settings";
  String CREATE_NEW_PASSWORD = "Create new password";
  String ADD_ATTACHMENT_TO_PASSWORD_SAFE = "Add attachment to password safe";
  String REMOVE_PASSWORD_SAFE = "Remove password safe";
  String UPDATE_PASSWORD_SAFE = "Update password safe";
  String SHARE_PASSWORD_SAFE = "Share password safe";
  String REMOVE_SHARE = "Stopped sharing password";
  String UPDATE_USER_AVATAR = "Update user avatar";
  String ENABLE_2F = "Enable two-factor authentication";
  String DISABLE_2F = "Disable two-factor authentication";
  String IMPORT_PASSWORDS = "Import passwords";
  String REMOVE_USER_FROM_GROUP = "Remove user from password group";
  String ADD_USER_FROM_GROUP = "Add user to group";
  String JOIN_GROUP = "Join group";
  String CREATE_GROUP_SAFE = "Create group password safe";
  String REMOVE_GROUP_SAFE = "Remove group password safe";
}

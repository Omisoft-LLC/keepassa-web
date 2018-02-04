package com.omisoft.keepassa.constants;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Don't use slf4j here
 * Created by nslavov on 3/25/16.
 */
public class RestUrl {

  public static final String API_VERSION_1 = "/api/v1";
  public static final String SECURE = "/secure";
  public static final String REST = "/rest";
  // Endpoints
  public static final String USER = "/user";
  public static final String TEST = "/test";
  public static final String INIT = "/init";
  public static final String GROUP = "/group";
  public static final String ACCOUNT = "/account";
  public static final String PROFILE = "/profile";
  public static final String MESSAGE = "/message";
  public static final String ADMIN = "/admin";
  // init
  public static final String TYPES = "/types";
  public static final String CONFIGURATION = "/config";
  // static paths
  public static final String CREATE = "/create";
  public static final String CREATE_SAFE = "/createSafe";
  public static final String DELETE = "/delete";
  public static final String DELETE_USER = "/deleteUser";
  public static final String UPDATE = "/update";
  public static final String SAFE = "/safe";
  public static final String LIST_SAFES = "/listSafes";
  public static final String LIST_FOR_INVITE = "/listForInvite";
  public static final String LIST_USERS = "/listUsers";
  public static final String SHARE = "/share";
  public static final String STOP_SHARING = "/stopSharing";
  public static final String STOP_SHARING_WITH = "/stopSharingWith";
  public static final String REMOVE_SHARE = "/removeShare";
  public static final String DELETE_SAFE = "/deleteSafe";
  public static final String UPDATE_SAFE = "/updateSafe";
  public static final String AUDIT = "/audit";
  public static final String LIST = "/list";
  public static final String GET_MULTIPLE_PUB_KEYS = "/getMultiplePubKeys";
  public static final String ADD = "/add";
  public static final String ADD_FROM_WEB = "/web/add";
  public static final String SAFE_ATTACHMENT = "/attachment";
  public static final String ADD_ATTACHMENT = "/add/attachment";
  public static final String ADD_USER = "/addUser";
  public static final String JOIN = "/join";
  public static final String GET = "/get";
  public static final String GET_BY_URL = "/getByUrl";
  public static final String GET_BY_ID = "/getById";
  public static final String GET_SHARE_BY_ID = "/getShareById";
  public static final String LEAVE = "/leave";
  public static final String VIEW_PASSWORD = "/view/password";
  public static final String DOWNLOAD = "/download";
  public static final String USERS = "/users";
  public static final String USER_SECURITY = "/userSecurity";
  public static final String USER_DEPARTMENTS = "/userDepartments";
  public static final String DEPARTMENT = "/department";
  public static final String CREATE_DEPARTMENT = "/createDepartment";
  public static final String DEPARTMENTS = "/departments";
  public static final String DEPARTMENT_MEMBERS = "/departmentMembers";
  public static final String UPDATE_USER = "/updateUser";
  public static final String UPDATE_SECURITY = "/updateSecurity";
  public static final String UPDATE_DEPARTMENT = "/updateDepartment";
  public static final String SUSPEND_USER = "/suspendUser";
  public static final String DELETE_DEPARTMENT = "/deleteDepartment";
  public static final String REMOVE_USER_FROM_DEPT = "/removeUserFromDepartment";
  public static final String INVITE = "/invite";
  public static final String GET_PUBLIC_KEY = "/getPublicKey";
  public static final String GET_USERS_PUBLIC_KEY = "/getUsersPublicKey";

  // account functionality
  public static final String REGISTER = "/register";
  public static final String LOGIN = "/login";
  public static final String RESET_PASSWORD = "/resetPassword";
  public static final String LOGOUT = "/logout";
  // twofactor
  public static final String TF_ENABLE = "/enableTwoF";
  public static final String TF_DISABLE = "/disableTwoF";
  public static final String TF_CHECK_PASS = "/checkPass";
  public static final String TF_VERIFY = "/verifyTwoF";
  public static final String IMPORT = "/import";
  public static final String UPLOAD = "/upload";
  public static final String GET_BY_APP_ID = "/getByAppId";
  public static final String SEARCH_FOR_EMAIL = "/searchForEmail";
  public static final String GET_ROOM_PIN = "/getPin";
  public static final String FEEDBACK = "/feedback";
  public static final String SIGNAL_ENDPOINT = "/signal/";
  public static final String WS_PATH = "/ws";
  public static final String UPLOAD_CSR = "/uploadCsr";
  public static final String SETTINGS = "/settings";
  public static final String REGISTER_COMPANY = "/registerCompany";
  public static final String ADD_USER_TO_DEPARTMENTS = "/addUserToDepartments";
  public static final String SEARCH_FOR_DEPARTMENT = "/searchForDepartment";
  public static final String INVITE_USERS = "/inviteUsers";


  /**
   * Holds URL Paths
   */

  // server
  public static String SERVER_NAME = "https://keepassa.co";

  public static Map<String, String> getAllUrls() {
    try {
      Class<?> stringClass = String.class;
      Field[] fields = RestUrl.class.getDeclaredFields();
      Map<String, String> urls = new HashMap<>();
      for (Field f : fields) {
        if (Modifier.isStatic(f.getModifiers())) {
          urls.put(f.getName(), (String) (f.get(stringClass)));
        }
      }
      return urls;
    } catch (IllegalAccessException e) {
      System.err.print("ERROR IN REFLECTION:");
      return new HashMap<>();
    }

  }

}

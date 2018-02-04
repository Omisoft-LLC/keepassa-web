package com.omisoft.keepassa.endpoints;

import static com.omisoft.keepassa.constants.Constants.USER_REDIS_DTO;

import com.google.inject.Injector;
import com.omisoft.keepassa.dao.UserDAO;
import com.omisoft.keepassa.dto.LoggedUserInfo;
import com.omisoft.keepassa.dto.UserInfoDTO;
import com.omisoft.keepassa.entities.users.User;
import com.omisoft.keepassa.exceptions.NotFoundException;
import com.omisoft.keepassa.utils.InjectorHolder;
import javax.servlet.http.HttpServletRequest;

/**
 * Holds base methods for endpoints Created by dido on 27.01.17.
 */
public interface BaseEndpoint {

  /**
   * Set last action needed for history
   */
  default void setLastAction(String action) {
    Injector injector = InjectorHolder.getInjector();
    UserInfoDTO userInfoDTO = injector.getInstance(UserInfoDTO.class);
    if (userInfoDTO != null) {
      userInfoDTO.setLastAction(action);
    }
  }

  default User getDbUser(HttpServletRequest request, UserDAO userDAO) throws NotFoundException {
    LoggedUserInfo loggedUserInfo = (LoggedUserInfo) request.getAttribute(USER_REDIS_DTO);
    return userDAO.findUserByEmailWithExc(loggedUserInfo.getEmail());
  }

  default LoggedUserInfo getLoggedUser(HttpServletRequest request) throws NotFoundException {
    LoggedUserInfo loggedUserInfo = (LoggedUserInfo) request.getAttribute(USER_REDIS_DTO);
    return loggedUserInfo;
  }
}

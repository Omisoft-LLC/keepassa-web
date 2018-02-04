package com.omisoft.keepassa.endpoints;

import static com.omisoft.keepassa.constants.Constants.QR_CODE_FILE_PNG;
import static com.omisoft.keepassa.constants.Constants.QR_CODE_SIZE;
import static com.omisoft.keepassa.constants.RestUrl.GET_PUBLIC_KEY;
import static com.omisoft.keepassa.constants.RestUrl.GET_ROOM_PIN;
import static com.omisoft.keepassa.constants.RestUrl.PROFILE;
import static com.omisoft.keepassa.constants.RestUrl.SEARCH_FOR_EMAIL;
import static com.omisoft.keepassa.constants.RestUrl.SECURE;
import static com.omisoft.keepassa.constants.RestUrl.TF_CHECK_PASS;
import static com.omisoft.keepassa.constants.RestUrl.TF_DISABLE;
import static com.omisoft.keepassa.constants.RestUrl.TF_ENABLE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.zxing.WriterException;
import com.omisoft.keepassa.constants.AuditActions;
import com.omisoft.keepassa.dao.UserDAO;
import com.omisoft.keepassa.dto.rest.ContactInfoDTO;
import com.omisoft.keepassa.dto.rest.ErrorDTO;
import com.omisoft.keepassa.dto.rest.FileDTO;
import com.omisoft.keepassa.dto.rest.LoginDTO;
import com.omisoft.keepassa.dto.rest.ProfileDTO;
import com.omisoft.keepassa.dto.rest.SelectTagsSearchDTO;
import com.omisoft.keepassa.dto.rest.SuccessDTO;
import com.omisoft.keepassa.dto.rest.UserWithAESDTO;
import com.omisoft.keepassa.entities.passwords.FileData;
import com.omisoft.keepassa.entities.users.ContactInfo;
import com.omisoft.keepassa.entities.users.User;
import com.omisoft.keepassa.exceptions.NotFoundException;
import com.omisoft.keepassa.structures.RoomPool;
import com.omisoft.keepassa.structures.SecureString;
import com.omisoft.keepassa.utils.QRUtils;
import com.omisoft.keepassa.utils.TimeBasedOneTimePasswordUtil;
import com.omisoft.server.common.exceptions.DataBaseException;
import io.swagger.annotations.Api;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

/**
 * Created by nslavov on 12/6/16.
 */
@Slf4j
@Path(SECURE + PROFILE)
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@Api(tags = {"profile"}, value = SECURE + PROFILE, description = "Profile info")
public class ProfileEndpoint implements BaseEndpoint {


  private final UserDAO userDAO;
  private final ObjectMapper mapper;

  private final RoomPool roomPool;

  @Inject
  public ProfileEndpoint(UserDAO userDAO, ObjectMapper mapper,
      RoomPool roomPool) {
    this.userDAO = userDAO;
    this.mapper = mapper;
    this.roomPool = roomPool;
  }

  @POST
  @Path("/info")
  public Response profileInfo(@Context HttpServletRequest request)
      throws com.omisoft.keepassa.exceptions.NotFoundException, DataBaseException {

    User user = getDbUser(request, userDAO);

//        if (user.getContactInfo() == null) {
//          user.setContactInfo(new ContactInfo());
//          user = userDao.saveOrUpdate(user);
//        }

    ProfileDTO result = new ProfileDTO(user);
    return Response.status(200).entity(result).build();


  }

  @POST
  @Path("/edit")
  public Response profileEdit(@Context HttpServletRequest request, ProfileDTO profileDto)
      throws NotFoundException, DataBaseException {

    User user = getDbUser(request, userDAO);

    setLastAction(AuditActions.UPDATE_USER_INFO);

    userDAO.saveOrUpdate(user.mapProfile(profileDto));

    return Response.status(200).entity(new SuccessDTO("Successfully updated your profile!"))
        .build();


  }

  @POST
  @Path(GET_PUBLIC_KEY)
  public Response getPublicKey(@Context HttpServletRequest request) throws NotFoundException {

    User usr = getDbUser(request, userDAO);
    UserWithAESDTO userWithAESDTO = new UserWithAESDTO(usr.getEmail(), usr.getClientPublicKey());
    return Response.status(200).entity(userWithAESDTO).build();


  }

  @POST
  @Path("/contact")
  public Response contact(@Context HttpServletRequest request)
      throws NotFoundException, DataBaseException {

    User user = getDbUser(request, userDAO);

    if (user.getContactInfo() == null) {
      user.setContactInfo(new ContactInfo());
      setLastAction(AuditActions.UPDATE_USER_INFO);

      user = userDAO.saveOrUpdate(user);
    }
    ContactInfoDTO result = new ContactInfoDTO(user.getContactInfo());
    return Response.status(200).entity(result).build();


  }

  @POST
  @Path("/contact/edit")
  public Response editContact(@Context HttpServletRequest request, ContactInfoDTO contactInfoDTO)
      throws NotFoundException, DataBaseException {

    User user = getDbUser(request, userDAO);
    if (user.getContactInfo() == null) {
      user.setContactInfo(new ContactInfo());
    }
    setLastAction(AuditActions.UPDATE_USER_INFO);

    userDAO.saveOrUpdate(user.mapContactInfo(contactInfoDTO));
    return Response.status(200).entity(new SuccessDTO("Successfully updated your contact info!"))
        .build();

  }

  // TODO removed
//  @POST
//  @Path("/password/edit")
//  public Response editPassword(@Context HttpServletRequest request, ResetPasswordDTO passwordDTO) {
//    String reqUrl = request.getRequestURI();
//    String token = request.getHeader(AUTHORIZATION_HEADER);
//    try {
//      User user = getDbUser(request,userDao);
//      if (passwordDTO.getOldPassword() == null || passwordDTO.getNewPassword() == null) {
//        return Response.status(500)
//            .entity(new ErrorDTO(request.getRequestURI(), "Invalid or empty params!")).build();
//      }
//      user.unlockKeystore();
//      if (user.getSecureKeystore().open(MASTER_PASSWORD, passwordDTO.getOldPassword(),
//          Constants.CONSTANT_PASSWORD)) {
//        user.setSecureKeystore(userDao.changeKeystorePassword(user, passwordDTO));
//        userDao.saveOrUpdate(user);
//        authority.removeUser(token);
//        String str = mapper.writeValueAsString(
//            new LoggedUserInfo(user.getEmail(), passwordDTO.getNewPassword()));
//        authority.addUser(token, str);
//        setLastAction(AuditActions.RESET_PASSWORD_ACTION);
//        return Response.status(200).entity(new SuccessDTO("Successfully changed the password!"))
//            .build();
//      } else {
//        log.error("CAN'T OPEN KEYSTORE");
//        return Response.status(405)
//            .entity(new ErrorDTO(reqUrl, "Invalid username and password combination!")).build();
//      }
//
//    } catch (Exception e) {
//      log.error("GENERIC EXCEPTION", e);
//      return errorResponseCatchBlock(reqUrl, e.getMessage());
//    }
//
//  }

  @POST
  @Path("/avatar/edit")
  @Consumes({MediaType.MULTIPART_FORM_DATA})
  public Response editAvatar(@Context HttpServletRequest request, @MultipartForm FileDTO fileDTO)
      throws NotFoundException, DataBaseException {

    User user = getDbUser(request, userDAO);
    FileData data = new FileData();
    data.setFilename(fileDTO.getFileName());
    data.setMimeType(fileDTO.getMimeType());
    data.setContent(fileDTO.getData());
    user.setPoster(data);
    setLastAction(AuditActions.UPDATE_USER_AVATAR);
    user = userDAO.saveOrUpdate(user);
    return Response.status(200).entity(user.getPoster()).build();


  }

  @POST
  @Path("/avatar")
  public Response getAvatar(@Context HttpServletRequest request) throws NotFoundException {

    User user = getDbUser(request, userDAO);
    return Response.status(200).entity(user.getPoster()).build();


  }

  @POST
  @Path("/login/info")
  public Response getLoginInfo(@Context HttpServletRequest request) throws NotFoundException {

    User user = getDbUser(request, userDAO);
    return Response.status(200)
        .entity(new LoginDTO(user.getEmail(), user.getPoster().getContent())).build();


  }

  @POST
  @Path(TF_CHECK_PASS)
  public Response checkPassForTwoF(@Context HttpServletRequest request, SecureString pass)
      throws NotFoundException {
    User usr = getDbUser(request, userDAO);
    if (org.mindrot.jbcrypt.BCrypt.checkpw(pass.toString(), usr.getPassword())){
      return Response.status(200).entity(new SuccessDTO("Successfully authorized user!")).build();
    } else {
      return Response.status(402)
          .entity(new ErrorDTO(request.getRequestURI(), "Invalid password!")).build();
    }

  }


  @POST
  @Path(TF_ENABLE)
  public Response enableTwoFactor(@Context HttpServletRequest request)
      throws NotFoundException, IOException, WriterException, DataBaseException {

    User user = getDbUser(request, userDAO);
    user.setIsTwoFEnabled(true);
    String secretCode = TimeBasedOneTimePasswordUtil.generateBase32Secret();
    // todo encrypt secret

    user.setAuthKey(secretCode.getBytes("UTF-8"));
    setLastAction(AuditActions.ENABLE_2F);

    userDAO.saveOrUpdate(user);
    String qrContent = "otpauth://totp/keepassa.co:" + user.getEmail() + "?secret=" + secretCode
        + "&issuer=Keepassa.co\n";

    String qrCode = QRUtils.createBase64QRCode(qrContent, QR_CODE_SIZE, QR_CODE_FILE_PNG);
    return Response.status(200).entity(mapper.writeValueAsString(qrCode)).build();

  }

  @POST
  @Path(TF_DISABLE)
  public Response disableTwoFactor(@Context HttpServletRequest request)
      throws NotFoundException, DataBaseException {

    User user = getDbUser(request, userDAO);
    user.setIsTwoFEnabled(false);
    user.setAuthKey(new byte[]{});
    setLastAction(AuditActions.DISABLE_2F);

    userDAO.saveOrUpdate(user);
    return Response.status(200).entity("Successfully disabled the Two-Factor verification!")
        .build();

  }

  /**
   * Searches for user but excludes current user
   */
  @POST
  @Path(SEARCH_FOR_EMAIL)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response searchForEmail(@Context HttpServletRequest request,
      @FormParam("q") String query) throws NotFoundException {

    User currentUser = getDbUser(request, userDAO);
    if (!currentUser.getEmail().equalsIgnoreCase(query)) {

      query = query.toUpperCase();
      if (query.length() >= 3) {
        return Response.status(200).entity(mapUserEmailsForSearch(userDAO.findMatchingUsers(query)))
            .build();
      }
    }
    return Response.status(200).entity(new ArrayList<>()).build();

  }

  @GET
  @Path(GET_ROOM_PIN)
  public Response getRoomPin() {
    String roomId;
    do {
      roomId = String.valueOf(ThreadLocalRandom.current().nextInt(1000, 9999 + 1));

    } while (roomPool.hasRoomWithId(roomId));
    return Response.ok(roomId).build();
  }

  private List<SelectTagsSearchDTO> mapUserEmailsForSearch(List<User> users) {
    List<SelectTagsSearchDTO> emails = new ArrayList<>();
    for (User user : users) {
      emails.add(new SelectTagsSearchDTO(user));
    }
    return emails;
  }
}

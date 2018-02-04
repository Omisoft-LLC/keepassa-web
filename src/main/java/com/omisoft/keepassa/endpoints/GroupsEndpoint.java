package com.omisoft.keepassa.endpoints;

import static com.omisoft.keepassa.constants.Constants.USER_REDIS_DTO;
import static com.omisoft.keepassa.constants.RestUrl.ADD_USER;
import static com.omisoft.keepassa.constants.RestUrl.CREATE;
import static com.omisoft.keepassa.constants.RestUrl.CREATE_SAFE;
import static com.omisoft.keepassa.constants.RestUrl.DELETE;
import static com.omisoft.keepassa.constants.RestUrl.DELETE_SAFE;
import static com.omisoft.keepassa.constants.RestUrl.DELETE_USER;
import static com.omisoft.keepassa.constants.RestUrl.GET;
import static com.omisoft.keepassa.constants.RestUrl.GET_PUBLIC_KEY;
import static com.omisoft.keepassa.constants.RestUrl.GROUP;
import static com.omisoft.keepassa.constants.RestUrl.JOIN;
import static com.omisoft.keepassa.constants.RestUrl.LEAVE;
import static com.omisoft.keepassa.constants.RestUrl.LIST;
import static com.omisoft.keepassa.constants.RestUrl.LIST_FOR_INVITE;
import static com.omisoft.keepassa.constants.RestUrl.LIST_SAFES;
import static com.omisoft.keepassa.constants.RestUrl.LIST_USERS;
import static com.omisoft.keepassa.constants.RestUrl.SECURE;
import static com.omisoft.keepassa.constants.RestUrl.TEST;
import static com.omisoft.keepassa.constants.RestUrl.UPDATE;
import static com.omisoft.keepassa.constants.RestUrl.UPDATE_SAFE;
import static com.omisoft.keepassa.utils.Utils.getHostFromUrl;

import com.google.inject.Inject;
import com.omisoft.keepassa.authority.GroupAuthority;
import com.omisoft.keepassa.constants.AuditActions;
import com.omisoft.keepassa.constants.Constants;
import com.omisoft.keepassa.dao.GroupDAO;
import com.omisoft.keepassa.dao.PasswordSafeDAO;
import com.omisoft.keepassa.dao.PasswordSafeKeyDAO;
import com.omisoft.keepassa.dao.UserDAO;
import com.omisoft.keepassa.dao.UserWithAesDAO;
import com.omisoft.keepassa.dto.EncryptionDTO;
import com.omisoft.keepassa.dto.GroupInfoDTO;
import com.omisoft.keepassa.dto.LoggedUserInfo;
import com.omisoft.keepassa.dto.SafeBasicUserInfoDTO;
import com.omisoft.keepassa.dto.rest.ErrorDTO;
import com.omisoft.keepassa.dto.rest.GroupDTO;
import com.omisoft.keepassa.dto.rest.InviteDTO;
import com.omisoft.keepassa.dto.rest.InvitedUsersDTO;
import com.omisoft.keepassa.dto.rest.PasswordSafeDTO;
import com.omisoft.keepassa.dto.rest.SuccessDTO;
import com.omisoft.keepassa.dto.rest.UserWithAESDTO;
import com.omisoft.keepassa.entities.passwords.Group;
import com.omisoft.keepassa.entities.passwords.PasswordSafe;
import com.omisoft.keepassa.entities.passwords.PasswordSafeKey;
import com.omisoft.keepassa.entities.passwords.UserWithAES;
import com.omisoft.keepassa.entities.users.User;
import com.omisoft.keepassa.exceptions.NotFoundException;
import com.omisoft.keepassa.exceptions.SecurityException;
import com.omisoft.keepassa.services.EmailSenderService;
import com.omisoft.keepassa.structures.SecureKeystore;
import com.omisoft.keepassa.structures.SecureKeystore.KeyType;
import com.omisoft.server.common.exceptions.DataBaseException;
import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by leozhekov on 10/28/16. Endpoint for group functionality.
 */
@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path(SECURE + GROUP)
@Api(tags = {"groups"}, value = SECURE + GROUP, description = "Group creation and modification")
public class GroupsEndpoint implements BaseEndpoint {

  private final GroupDAO groupDao;
  private final GroupAuthority groupAuthority;
  private final UserDAO userDAO;
  private final EmailSenderService emailSenderService;
  private final UserWithAesDAO userWithAesDAO;
  private final PasswordSafeKeyDAO passwordSafeKeyDAO;
  private PasswordSafeDAO passwordSafeDAO;

  @Inject
  public GroupsEndpoint(GroupDAO groupDao, GroupAuthority groupAuthority,
      UserDAO userDAO,
      EmailSenderService emailSenderService,
      PasswordSafeDAO groupSafeDao, UserWithAesDAO userWithAesDAO, PasswordSafeKeyDAO passwordSafeKeyDAO) {
    this.groupDao = groupDao;
    this.groupAuthority = groupAuthority;
    this.userDAO = userDAO;
    this.emailSenderService = emailSenderService;
    this.passwordSafeDAO = groupSafeDao;
    this.userWithAesDAO = userWithAesDAO;
    this.passwordSafeKeyDAO = passwordSafeKeyDAO;
  }

  /**
   * Test REST.
   */
  @GET
  @Path(TEST)
  public Response testGroupsEndpoint(@Context HttpServletRequest request) {

    return Response.status(200).build();
  }

  /**
   * Create a new group.
   *
   * @param groupDTO {name:string, groupEmail:string, description:string, groupSafes:list}
   */
  @POST
  @Path(CREATE)
  public Response createNewGroup(@Context HttpServletRequest request, GroupDTO groupDTO)
      throws NotFoundException, DataBaseException {

    User user = getDbUser(request, userDAO);
    // create new group
    Group group = new Group(groupDTO);
    // get the adminEmail from the redis
    group.setAdmin(user);
    user.getGroups().add(group);
    group.getUsers().add(user);
    groupDao.saveOrUpdate(group);
    userDAO.saveOrUpdate(user);

    return Response.status(200).entity(new SuccessDTO("Successful group creation")).build();


  }

  @POST
  @Path(DELETE)
  public Response deleteGroup(@Context HttpServletRequest request, String groupId)
      throws NotFoundException, DataBaseException, com.omisoft.server.common.exceptions.NotFoundException {
    log.info("GROUP ID:" + groupId);

    User user = getDbUser(request, userDAO);
    Group group = groupDao.findById(UUID.fromString(groupId));
    user.getGroups().remove(group);
    // todo
    userDAO.saveOrUpdate(user);
    groupDao.remove(group);
    return Response.status(200).entity(mapGroupsToDTO(user.getGroups())).build();


  }

  @POST
  @Path(UPDATE)
  public Response updateGroup(@Context HttpServletRequest request, GroupDTO groupDTO)
      throws NotFoundException, DataBaseException, com.omisoft.server.common.exceptions.NotFoundException {
    String reqUrl = request.getRequestURI();

    User user = getDbUser(request, userDAO);
    Group group = groupDao.findById(groupDTO.getId());
    if (group.getAdmin().equals(user)) {
      group.setName(groupDTO.getName());
      group.setDescription(groupDTO.getDescription());
      group.setGroupEmail(groupDTO.getGroupEmail());
      groupDao.saveOrUpdate(group);
      return Response.status(200).entity(mapGroupsToDTO(user.getGroups())).build();
    } else {

      return Response.status(401)
          .entity(new ErrorDTO(reqUrl, "Oi, mate! Yer not the captain! Don't touch the crew!"))
          .build();
    }

  }

  /**
   * Add a user to the group.
   *
   * @param invitedUsersDTO {email:string, id:long}
   */
  // TODO
  @POST
  @Path(ADD_USER)
  public Response addUserToGroup(@Context HttpServletRequest request,
      InvitedUsersDTO invitedUsersDTO)
      throws NotFoundException, SecurityException, DataBaseException, com.omisoft.server.common.exceptions.NotFoundException {

    Group group = groupDao.findById(invitedUsersDTO.getGroupId());
    LoggedUserInfo admin = (LoggedUserInfo) request.getAttribute(USER_REDIS_DTO);
    User senderUser = getDbUser(request, userDAO);
    List<UUID> pIds = new ArrayList<>();

    for (UserWithAESDTO invitedUserEmail : invitedUsersDTO.getUserWithAES()) {
      User invitedUser = userDAO.findUserByEmailWithNull(invitedUserEmail.getEmail());
      if (invitedUser == null) {
        continue;
      }
      for (PasswordSafe passwordSafe : group.getGroupSafes()) { // collecting
        if (passwordSafe.getId().equals(invitedUserEmail.getPsId())) {
          UserWithAES ua = new UserWithAES(invitedUserEmail);
          passwordSafe.getUserWithAESList().add(ua);// TODO UPDATE
          ua.setPasswordSafe(passwordSafe);
          pIds.add(passwordSafe.getPasswordEncryptId());
          userWithAesDAO.saveOrUpdate(ua);
          passwordSafeDAO.saveOrUpdate(passwordSafe);

        }
      }



      group.getUsers().add(invitedUser);
      invitedUser.getGroups().add(group);
      userDAO.saveOrUpdate(invitedUser);
      // sending email with invite
      emailSenderService.sendGroupNotifyEmail(invitedUserEmail.getEmail(), admin.getEmail(),
          group.getName());
    }

    setLastAction(AuditActions.ADD_USER_FROM_GROUP);
    groupDao.saveOrUpdate(group);
    return Response.status(200).entity(new SuccessDTO("Successfully invited everyone!")).build();

  }

  /**
   * Remove a user from a group.
   */
  @POST
  @Path(DELETE_USER)
  @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
  public Response deleteUserFromGroup(@Context HttpServletRequest request,
      @FormParam("groupId") UUID groupId, @FormParam("email") String userEmail)
      throws SecurityException, DataBaseException, NotFoundException, com.omisoft.server.common.exceptions.NotFoundException {

    // getting the adminEmail
    LoggedUserInfo adminRedis = (LoggedUserInfo) request.getAttribute(USER_REDIS_DTO);
    Group group = groupDao.findById(groupId);
    // getting the entities from the db
    User userToRemove = userDAO.findUserByEmailWithExc(userEmail);
    User admin = userDAO.findUserByEmailWithExc(adminRedis.getEmail());
    // removing relations
    group.getUsers().remove(userToRemove);
    userToRemove.getGroups().remove(group);
    setLastAction(AuditActions.REMOVE_USER_FROM_GROUP);
    setLastAction(AuditActions.REMOVE_USER_FROM_GROUP);

    groupDao.saveOrUpdate(group);
    userDAO.saveOrUpdate(userToRemove);

    // adding a new entity for when next time the removed user logs in to remove the shared key
    // from his keystore
    // TODO think of something better if the user is currently logged in. Probably WS or
    // eventbus push to logout(or call explicit copy ? ).
    // TODO What happens if password safe is in another group ?
    List<UUID> pIds =
        group.getGroupSafes().stream().map(PasswordSafe::getPasswordEncryptId)
            .collect(Collectors.toList());
    // collecting

    return Response.status(200).entity(new SuccessDTO("Successfully removed the user!")).build();


  }

  /**
   * Lists users in group
   */
  @POST
  @Path(LIST_USERS + "/{groupId}")
  public Response listUsersInGroup(@Context HttpServletRequest request,
      @PathParam("groupId") UUID groupId)
      throws NotFoundException, DataBaseException, com.omisoft.server.common.exceptions.NotFoundException {
    // TODO should we filter

    Group group = groupDao.findById(groupId);
    List<SafeBasicUserInfoDTO> usersInGroup = new ArrayList<>();
    for (User u : group.getUsers()) {
      usersInGroup.add(new SafeBasicUserInfoDTO(u));
    }
    return Response.status(200).entity(usersInGroup).build();

  }

  /**
   * Deprecated, no need to join, as user is added on next login Join group REST.
   *
   * @param verification {verificationNumber:string}
   */
  @POST
  @Path(JOIN)
  @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
  @Deprecated
  public Response joinGroup(@Context HttpServletRequest request,
      @FormParam("verification") String verification)
      throws NotFoundException, DataBaseException, com.omisoft.server.common.exceptions.NotFoundException {
    String reqUrl = request.getRequestURI();

    // checking if the invite is in the redis
    if (groupAuthority.isExist(verification)) {
      GroupInfoDTO groupInfoDTO = groupAuthority.getGroupInfo(verification);
      // getting the accepted user
      LoggedUserInfo loggedUserInfo = (LoggedUserInfo) request.getAttribute(USER_REDIS_DTO);
      if (groupInfoDTO.getUserEmail().equals(loggedUserInfo.getEmail())) {
        // getting the entities from the db
        User user = userDAO.findUserByEmailWithExc(loggedUserInfo.getEmail());
        Group group = groupDao.findById(groupInfoDTO.getGroupId());
        // getting their keystores and putting the shared key in the user's keystore
        // SecureKeystore userKeystore = user.getSecureKeystore();
        // byte[] decodedKey = Base64.getDecoder().decode(new
        // String(groupInfoDTO.getSharedKey()));
        // SecretKey groupKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        // TODO implement share logic
        // user.setKeyStore(
        // CertAndKeyUtils.addGroupSharedKey(userKeystore, groupKey, loggedUserInfo.getPassword(),
        // loggedUserInfo.decodeCertFromPEM(), groupInfoDTO.getGroupId()));
        // setting up relations
        user.getGroups().add(group);
        group.getUsers().add(user);
        userDAO.saveOrUpdate(user);
        setLastAction(AuditActions.JOIN_GROUP);
        groupDao.saveOrUpdate(group);
        // removing the invite from the redis
        groupAuthority.removeGroup(verification);

        return Response.status(200)
            .entity(new SuccessDTO("Successfully joined the group " + group.getName() + "!"))
            .build();
      } else {
        // 401 - Unauthorized
        return Response.status(402)
            .entity(new ErrorDTO(reqUrl, "Invitation email does not match your email!")).build();
      }

    } else {
      // 401 - Unauthorized
      return Response.status(402).entity(new ErrorDTO(reqUrl, "Invalid verification number!"))
          .build();
    }


  }

  /**
   * Create a group save.
   *
   * @param dto an instance of PasswordSafeDTO
   */
  @POST
  @Path(CREATE_SAFE + "/{groupId}")
  public Response createSafeFromWeb(@Context HttpServletRequest request,
      @PathParam("groupId") final UUID groupId, PasswordSafeDTO dto)
      throws NotFoundException, SecurityException, DataBaseException, com.omisoft.server.common.exceptions.NotFoundException {

    // getting the adminEmail from redis

    LoggedUserInfo loggedUserInfo = (LoggedUserInfo) request.getAttribute(USER_REDIS_DTO);
    User sender = getDbUser(request, userDAO);
    Group group = groupDao.findById(groupId);

    PasswordSafe passwordSafe = new PasswordSafe(dto);
    // mapping the values
    EncryptionDTO encryptionDTO = SecureKeystore
        .encryptPassword(
            dto.getPassword());
    passwordSafe.setPassword(encryptionDTO.getEncryptedMessage());


    passwordSafe.setCreator(sender);
    userDAO.saveOrUpdate(sender);
    // TODO Fix user with aes

    passwordSafe.getGroups().add(group);

    passwordSafe = passwordSafeDAO.saveOrUpdate(passwordSafe);
    PasswordSafeKey key = new PasswordSafeKey();
    key.setAesKey(SecureKeystore.encryptWithKey(encryptionDTO.getEncodedAESKey(),SecureKeystore.AES_MASTER_KEY, KeyType.AES));
    key.setAesKey(SecureKeystore.encryptWithKey(encryptionDTO.getEncodedTwoFishKey(),SecureKeystore.TWOFISH_MASTER_KEY, KeyType.AES));
    key.setAesKey(SecureKeystore.encryptWithKey(encryptionDTO.getEncodedSerpentKey(),SecureKeystore.SERPENT_MASTER_KEY, KeyType.AES));
    key.setUser(sender);
    key.setPasswordSafe(passwordSafe);
    key = passwordSafeKeyDAO.saveOrUpdate(key);
    for (UserWithAESDTO userWithAESDTO : dto.getSharedWithUsers()) {
      UserWithAES userWithAES = new UserWithAES(userWithAESDTO);
      userWithAES.setPasswordSafe(passwordSafe);
      userWithAesDAO.saveOrUpdate(userWithAES);
    }
    group.getGroupSafes().add(passwordSafe);
    setLastAction(AuditActions.CREATE_GROUP_SAFE);

    groupDao.saveOrUpdate(group);
    List<UUID> pIds =
        group.getGroupSafes().stream().map(PasswordSafe::getPasswordEncryptId)
            .collect(Collectors.toList());

    for (User groupUser : group.getUsers()) {
      if (!sender.equals(groupUser)) {

      }
    }

    PasswordSafeDTO result = new PasswordSafeDTO(passwordSafe);
    return Response.status(200).entity(result).build();


  }

  /**
   * Lists the group saves.
   *
   * @param id id
   */
  @POST
  @Path(LIST_SAFES + "/{groupId}")
  public Response listSafes(@Context HttpServletRequest request, @PathParam("groupId") UUID id)
      throws NotFoundException, DataBaseException, com.omisoft.server.common.exceptions.NotFoundException {
    String reqUrl = request.getRequestURI();

    User user = getDbUser(request, userDAO);
    Group group = groupDao.findById(id);
    if (isUserInGroup(user, group)) {

      Set<PasswordSafeDTO> dtos = mapGroupSaveToDTO(group.getGroupSafes());

      return Response.status(200).entity(dtos).build();
    } else {
      // 401 - Unauthorized
      return Response.status(402)
          .entity(new ErrorDTO(reqUrl, "You are not a part of this group!")).build();
    }


  }

  @POST
  @Path(LIST_FOR_INVITE + "/{groupId}")
  public Response getSafesForInvite(@Context HttpServletRequest request,
      @PathParam("groupId") UUID id, List<String> emails)
      throws DataBaseException, NotFoundException, com.omisoft.server.common.exceptions.NotFoundException {

    InviteDTO inviteDTO = new InviteDTO();
    List<User> userList = new ArrayList<>();

    for (String email : emails) {
      userList.add(userDAO.findUserByEmailWithNull(email));
    }

    LoggedUserInfo loggedUserInfo = (LoggedUserInfo) request.getAttribute(USER_REDIS_DTO);
    User user = getDbUser(request, userDAO);

    Group group = groupDao.findById(id);
    if (group != null) {
      for (PasswordSafe passwordSafe : group.getGroupSafes()) {
        PasswordSafeDTO dto = new PasswordSafeDTO(passwordSafe);
        dto.setClientAESKey(
            getClientKeyByEmail(passwordSafe, user.getEmail()));
        inviteDTO.getPasswordSafeDTOS().add(dto);
      }

      for (User usr : userList) {
        inviteDTO.getUserWithAESDTOS()
            .add(new UserWithAESDTO(usr.getEmail(), usr.getClientPublicKey()));
      }
    }

    return Response.status(200).entity(inviteDTO).build();

  }

  /**
   * Delete group save.
   *
   * @param id the id of the save.
   */
  @POST
  @Path(DELETE_SAFE)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response deleteSave(@Context HttpServletRequest request, @FormParam("safeId") UUID id,
      @FormParam("groupId") UUID groupId)
      throws SecurityException, DataBaseException, NotFoundException, com.omisoft.server.common.exceptions.NotFoundException {

    // getting adminEmail from redis and db
    LoggedUserInfo admin = (LoggedUserInfo) request.getAttribute(USER_REDIS_DTO);
    User sender = getDbUser(request, userDAO);
    // getting group save and group from db
    PasswordSafe groupSafe = passwordSafeDAO.findById(id);
    Group group = groupDao.findById(groupId);
    // removing relations
    group.getGroupSafes().remove(groupSafe);
    passwordSafeDAO.remove(groupSafe);
    setLastAction(AuditActions.REMOVE_GROUP_SAFE);

    groupDao.saveOrUpdate(group);

    return Response.status(200).entity(mapGroupSaveToDTO(group.getGroupSafes())).build();


  }

  /**
   * Update group save.
   *
   * @param dto [can be an instance of PasswordSafeDTO or GroupSaveDTO] in this case - GroupSaveDTO
   */
  @POST
  @Path(UPDATE_SAFE)
  public Response updateSave(@Context HttpServletRequest request, PasswordSafeDTO dto,
      UUID groupId)
      throws NotFoundException, SecurityException, DataBaseException, com.omisoft.server.common.exceptions.NotFoundException {

    // getting adminEmail from redis and db, group save and group from db
    LoggedUserInfo admin = (LoggedUserInfo) request.getAttribute(USER_REDIS_DTO);
    User user = getDbUser(request, userDAO);
    PasswordSafe groupSafe = passwordSafeDAO.findById(dto.getId());
    Group group = groupDao.findById(groupId);

    groupSafe.setName(dto.getName());
    groupSafe.setUrl(getHostFromUrl(dto.getUrl()));
    groupSafe.setDescription(dto.getDescription());
    groupSafe.setUsername(dto.getAccountName());
    // mapping dto to personal save entity
//    byte[] encryptedPassword = passwordSafeDAO.encryptPassword(user, dto.getPassword(),
//        admin.getPassword(), groupSafe.getPasswordEncryptId().toString());

//    groupSafe.setPassword(encryptedPassword);
    passwordSafeDAO.update(groupSafe);
    return Response.status(200).entity(mapGroupSaveToDTO(group.getGroupSafes())).build();


  }


  /**
   * Listing all the groups the user is in.
   */
  @POST
  @Path(LIST)
  public Response listGroups(@Context HttpServletRequest request) throws NotFoundException {

    // getting user from redis and db
    User user = getDbUser(request, userDAO);

    return Response.status(200).entity(mapGroupsToDTO(user.getGroups())).build();


  }

  @POST
  @Path(GET + "/{groupId}")
  public Response getGroupById(@Context HttpServletRequest request,
      @PathParam("groupId") UUID groupId)
      throws NotFoundException, DataBaseException, com.omisoft.server.common.exceptions.NotFoundException {

    Group group = groupDao.findById(groupId);
    GroupDTO dto = new GroupDTO(group);
    List<SafeBasicUserInfoDTO> users = new ArrayList<>();
    for (User usr : group.getUsers()) {
      users.add(new SafeBasicUserInfoDTO(usr.getFirstName(), usr.getEmail()));
    }
    dto.setUsers(users);
    return Response.status(200).entity(dto).build();


  }

  @POST
  @Path(LEAVE)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response leaveGroup(@Context HttpServletRequest request,
      @FormParam("groupId") UUID groupId)
      throws NotFoundException, SecurityException, DataBaseException, com.omisoft.server.common.exceptions.NotFoundException {

    LoggedUserInfo loggedUserInfo = (LoggedUserInfo) request.getAttribute(USER_REDIS_DTO);
    User user = getDbUser(request, userDAO);
    Group group = groupDao.findById(groupId);
    group.getUsers().remove(user);
    user.getGroups().remove(group);
    List<UUID> pIds =
        group.getGroupSafes().stream().map(PasswordSafe::getPasswordEncryptId)
            .collect(Collectors.toList());
    userDAO.saveOrUpdate(user);
    groupDao.saveOrUpdate(group);

    return Response.status(200).entity(mapGroupsToDTO(user.getGroups())).build();


  }

  @POST
  @Path(GET_PUBLIC_KEY + "/{groupId}")
  public Response getPublicKey(@Context HttpServletRequest request,
      @PathParam("groupId") UUID groupId)
      throws NotFoundException, DataBaseException, com.omisoft.server.common.exceptions.NotFoundException {
    log.info("GROUP USERS GET PUBLIC KEY");

    Group group = groupDao.findById(groupId);
    List<UserWithAESDTO> withAESDTOS = new ArrayList<>();
    for (User user : group.getUsers()) {
      withAESDTOS.add(new UserWithAESDTO(user.getEmail(), user.getClientPublicKey()));
    }

    return Response.status(200).entity(withAESDTOS).build();


  }

  /**
   * Private method that checks if the user is in the specified group.
   *
   * @param user user to check
   * @param group group he should be in
   */
  private boolean isUserInGroup(User user, Group group) {
    for (User usr : group.getUsers()) {
      if (user.equals(usr)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Private method that maps List<GroupSafe> to List<GroupSaveDTO>
   *
   * @param groupSafes List<GroupSafe> entities
   */
  private Set<PasswordSafeDTO> mapGroupSaveToDTO(final Set<PasswordSafe> groupSafes) {
    Set<PasswordSafeDTO> groupSaveDTOs = new HashSet<>();
    for (PasswordSafe passwordSafe : groupSafes) {
      groupSaveDTOs.add(new PasswordSafeDTO(passwordSafe));
    }
    log.info("PASSWORD SAFE COUNT:" + groupSafes.size());

    return groupSaveDTOs;
  }

  private List<GroupDTO> mapGroupsToDTO(Set<Group> groups) {
    List<GroupDTO> returnGroups = new ArrayList<>();
    for (Group group : groups) {
      returnGroups.add(new GroupDTO(group));
    }
    return returnGroups;
  }


  private byte[] getClientKeyByEmail(PasswordSafe passwordSafe, String email)
      throws DataBaseException {
    UserWithAES usr = userWithAesDAO.findByPasswordSafeAndEmail(passwordSafe, email);
    return org.apache.commons.codec.binary.Base64.decodeBase64(usr.getKey());

  }
}

package com.omisoft.keepassa.endpoints;

import static com.omisoft.keepassa.constants.Constants.USER_REDIS_DTO;
import static com.omisoft.keepassa.constants.RestUrl.ADD;
import static com.omisoft.keepassa.constants.RestUrl.ADD_ATTACHMENT;
import static com.omisoft.keepassa.constants.RestUrl.ADD_FROM_WEB;
import static com.omisoft.keepassa.constants.RestUrl.DELETE;
import static com.omisoft.keepassa.constants.RestUrl.DOWNLOAD;
import static com.omisoft.keepassa.constants.RestUrl.GET_BY_APP_ID;
import static com.omisoft.keepassa.constants.RestUrl.GET_BY_ID;
import static com.omisoft.keepassa.constants.RestUrl.GET_BY_URL;
import static com.omisoft.keepassa.constants.RestUrl.GET_MULTIPLE_PUB_KEYS;
import static com.omisoft.keepassa.constants.RestUrl.GET_SHARE_BY_ID;
import static com.omisoft.keepassa.constants.RestUrl.LIST;
import static com.omisoft.keepassa.constants.RestUrl.REMOVE_SHARE;
import static com.omisoft.keepassa.constants.RestUrl.SAFE;
import static com.omisoft.keepassa.constants.RestUrl.SAFE_ATTACHMENT;
import static com.omisoft.keepassa.constants.RestUrl.SECURE;
import static com.omisoft.keepassa.constants.RestUrl.SHARE;
import static com.omisoft.keepassa.constants.RestUrl.STOP_SHARING;
import static com.omisoft.keepassa.constants.RestUrl.STOP_SHARING_WITH;
import static com.omisoft.keepassa.constants.RestUrl.TEST;
import static com.omisoft.keepassa.constants.RestUrl.UPDATE;
import static com.omisoft.keepassa.constants.RestUrl.VIEW_PASSWORD;
import static com.omisoft.keepassa.utils.Utils.getHostFromUrl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import com.omisoft.keepassa.authority.UserAuthority;
import com.omisoft.keepassa.constants.AuditActions;
import com.omisoft.keepassa.constants.Constants;
import com.omisoft.keepassa.dao.FileDAO;
import com.omisoft.keepassa.dao.PasswordSafeDAO;
import com.omisoft.keepassa.dao.PasswordSafeKeyDAO;
import com.omisoft.keepassa.dao.UserDAO;
import com.omisoft.keepassa.dao.UserWithAesDAO;
import com.omisoft.keepassa.dto.EncryptionDTO;
import com.omisoft.keepassa.dto.LoggedUserInfo;
import com.omisoft.keepassa.dto.rest.ErrorDTO;
import com.omisoft.keepassa.dto.rest.FileDTO;
import com.omisoft.keepassa.dto.rest.PasswordSafeDTO;
import com.omisoft.keepassa.dto.rest.SuccessDTO;
import com.omisoft.keepassa.dto.rest.UserWithAESDTO;
import com.omisoft.keepassa.entities.passwords.FileData;
import com.omisoft.keepassa.entities.passwords.PasswordSafe;
import com.omisoft.keepassa.entities.passwords.PasswordSafeKey;
import com.omisoft.keepassa.entities.passwords.UserWithAES;
import com.omisoft.keepassa.entities.users.User;
import com.omisoft.keepassa.exceptions.NotFoundException;
import com.omisoft.keepassa.exceptions.SecurityException;
import com.omisoft.keepassa.services.EmailSenderService;
import com.omisoft.keepassa.structures.SecureKeystore;
import com.omisoft.keepassa.structures.SecureKeystore.KeyType;
import com.omisoft.keepassa.structures.SecureString;
import com.omisoft.server.common.exceptions.DataBaseException;
import io.swagger.annotations.Api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

/**
 * Created by leozhekov on 10/28/16. Endpoint for password safe functionality.
 */
@Slf4j
@Path(SECURE + SAFE)
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@Api(tags = {"passwords"}, value = SECURE + SAFE, description = "Password safe endpoint")

public class PasswordSafeEndpoint implements BaseEndpoint {

    private final UserDAO userDAO;
    private final PasswordSafeDAO passwordSafeDAO;
    private final UserAuthority authority;
    private final FileDAO fileDao;
    private final EmailSenderService emailSenderService;
    private final UserWithAesDAO userWithAesDAO;
    private final PasswordSafeKeyDAO passwordSafeKeyDAO;


    @Inject
    public PasswordSafeEndpoint(PasswordSafeKeyDAO passwordSafeKeyDAO, UserDAO userDAO,
                                PasswordSafeDAO passwordSafeDAO, UserAuthority authority,
                                FileDAO fileDao, EmailSenderService emailSenderService,
                                UserWithAesDAO userWithAesDAO) {
        this.userDAO = userDAO;
        this.passwordSafeDAO = passwordSafeDAO;
        this.authority = authority;
        this.fileDao = fileDao;
        this.emailSenderService = emailSenderService;
        this.userWithAesDAO = userWithAesDAO;
        this.passwordSafeKeyDAO = passwordSafeKeyDAO;
    }

    /**
     * Test REST.
     */
    @GET
    @Path(TEST)
    public Response testUserEndpoint(@Context HttpServletRequest request) throws NotFoundException {

        userDAO.findUserByEmailWithExc("asdasdasd");

        return Response.status(200).build();
    }


    /**
     * Create personal save.
     *
     * @param dto [can be an instance of PasswordSafeDTO or GroupSaveDTO] in this case -
     *            PasswordSafeDTO
     */
    @POST
    @Path(ADD)
    @Deprecated
    public Response createSave(@Context HttpServletRequest request, PasswordSafeDTO dto)
            throws DataBaseException, SecurityException, NotFoundException, JsonProcessingException {

        // get user from redis and db
        LoggedUserInfo loggedUserInfo = (LoggedUserInfo) request.getAttribute(USER_REDIS_DTO);
        User user = getDbUser(request, userDAO);
        // mapping dto to personal save entity
        PasswordSafe ps = new PasswordSafe(dto);
        log.info("PS APP ID 1:" + ps.getAppId());

        ps.getUsers().add(user);
        // getting the personal secret key to encrypt the save's password

        EncryptionDTO encryptionDTO =
                SecureKeystore.encryptPassword(dto.getPassword());
        ps.setPassword(encryptionDTO.getEncryptedMessage());
        PasswordSafeKey passwordSafeKey = new PasswordSafeKey();
        passwordSafeKey.setAesKey(SecureKeystore.encryptWithKey(encryptionDTO.getEncodedAESKey(), SecureKeystore.AES_MASTER_KEY,
                KeyType.AES));
        passwordSafeKey.setTwofishKey(SecureKeystore.encryptWithKey(encryptionDTO.getEncodedTwoFishKey(), SecureKeystore.TWOFISH_MASTER_KEY, KeyType.AES));
        passwordSafeKey.setSerpentKey(SecureKeystore.encryptWithKey(encryptionDTO.getEncodedSerpentKey(), SecureKeystore.SERPENT_MASTER_KEY, KeyType.AES));
        passwordSafeKey.setUser(user);
        passwordSafeKey.setPasswordSafe(ps);
        passwordSafeKey = passwordSafeKeyDAO.saveOrUpdate(passwordSafeKey);
        ps.getKeys().add(passwordSafeKey);
        user.getKeys().add(passwordSafeKey);

        ps.setCreator(user);
        user.getPasswordSafes().add(passwordSafeDAO.saveOrUpdate(ps));

        setLastAction(AuditActions.CREATE_NEW_PASSWORD);

        userDAO.saveOrUpdate(user);
        log.info("PS APP ID 2:" + ps.getAppId());
        return Response.status(200).entity(mapPersonalSavesToDTO(user.getPasswordSafes())).build();


    }

    /**
     * Create personal save.
     *
     * @param dto [can be an instance of PasswordSafeDTO or GroupSaveDTO] in this case -
     *            PasswordSafeDTO
     */
    @POST
    @Path(ADD_FROM_WEB)
    public Response createSaveFromWeb(@Context HttpServletRequest request, PasswordSafeDTO dto)
            throws JsonProcessingException, DataBaseException, SecurityException, com.omisoft.keepassa.exceptions.NotFoundException {
//      log.info(mapper.writeValueAsString(dto.getClientAESKey()));

        // get user from redis and db
        LoggedUserInfo loggedUserInfo = (LoggedUserInfo) request.getAttribute(USER_REDIS_DTO);
        User user = getDbUser(request, userDAO);
        // mapping dto to personal save entity
        PasswordSafe ps = new PasswordSafe(dto);
        PasswordSafeKey passwordSafeKey = new PasswordSafeKey();
        // TODO move to method
        EncryptionDTO encryptionDTO =
                SecureKeystore.encryptPassword(dto.getPassword());
        passwordSafeKey.setAesKey(SecureKeystore.encryptWithKey(encryptionDTO.getEncodedAESKey(), SecureKeystore.AES_MASTER_KEY,
                KeyType.AES));
        passwordSafeKey.setTwofishKey(SecureKeystore.encryptWithKey(encryptionDTO.getEncodedTwoFishKey(), SecureKeystore.TWOFISH_MASTER_KEY, KeyType.AES));
        passwordSafeKey.setSerpentKey(SecureKeystore.encryptWithKey(encryptionDTO.getEncodedSerpentKey(), SecureKeystore.SERPENT_MASTER_KEY, KeyType.AES));
        passwordSafeKey.setUser(user);
        passwordSafeKey.setPasswordSafe(ps);
        passwordSafeKey = passwordSafeKeyDAO.saveOrUpdate(passwordSafeKey);
        ps.getKeys().add(passwordSafeKey);
        user.getKeys().add(passwordSafeKey);

        ps.getUsers().add(user);

        // getting the personal secret key to encrypt the save's password
        ps.setCreator(user);
        ps = passwordSafeDAO.saveOrUpdate(ps);

        user.getPasswordSafes().add(ps);
        userDAO.saveOrUpdate(user);

        PasswordSafeDTO result = new PasswordSafeDTO(ps);
        return Response.status(200).entity(result).build();


    }

    /**
     * @param request
     * @param safeId
     * @return
     */
    @POST
    @Path(SAFE_ATTACHMENT + "/{safeId}")
    public Response getAttachment(@Context HttpServletRequest request,
                                  @PathParam("safeId") final UUID safeId) {

        List<FileData> result = fileDao.findFileDataByPasswordSafe(safeId);
        return Response.status(200).entity(result).build();

    }

    @POST
    @Path(ADD_ATTACHMENT + "/{safeId}")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response addAttachment(@Context HttpServletRequest request, @MultipartForm FileDTO fileDTO,
                                  @PathParam("safeId") final UUID safeId)
            throws SecurityException, DataBaseException, NotFoundException, com.omisoft.server.common.exceptions.NotFoundException {

        PasswordSafe pas = passwordSafeDAO.findById(safeId);
        LoggedUserInfo loggedUserInfo = (LoggedUserInfo) request.getAttribute(USER_REDIS_DTO);
        User user = getDbUser(request, userDAO);


        FileData data = new FileData();
        data.setName(fileDTO.getName());
        data.setFilename(fileDTO.getFileName());
        data.setMimeType(fileDTO.getMimeType());
        data.setEncrypt(fileDTO.getEncrypt());
        if (fileDTO.getEncrypt().equals(true)) {
            PasswordSafeKey key = passwordSafeKeyDAO.findKeysByUserAndPasswordSafe(user.getId(), pas.getId());
            EncryptionDTO encryptionDTO = SecureKeystore.decrtptEncryptionKeys(key);
          encryptionDTO = SecureKeystore.encryptWithKeys(fileDTO.getData(), encryptionDTO);
            data.setContent(encryptionDTO.getEncryptedMessage());
            userDAO.saveOrUpdate(user);
        } else {
            data.setContent(fileDTO.getData());
        }
        data = fileDao.saveOrUpdate(data);
        if (pas.getAttachedFiles() != null) {
            pas.addAttachedFiles(data);
        } else {
            pas.setAttachedFiles(new HashSet<>());
            pas.addAttachedFiles(data);
        }
        setLastAction(AuditActions.ADD_ATTACHMENT_TO_PASSWORD_SAFE);
        passwordSafeDAO.saveOrUpdate(pas);

        return Response.status(Response.Status.OK).build();
    }


    /**
     * Lists personal saves.
     */
    @POST
    @Path(LIST)
    public Response listSaves(@Context HttpServletRequest request)
            throws NotFoundException, DataBaseException {

        // getting user from redis and db
        User user = getDbUser(request, userDAO);
        // getting the personal secret key to decrypt the passwords.
        Set<PasswordSafe> safes = passwordSafeDAO.findPersonalSafes(user);

        return Response.status(200).entity(mapPersonalSavesWithUserToDTO(safes, user)).build();


    }

    /**
     * Delete personal save.
     *
     * @param id the id of the save.
     */
    @POST
    @Path(DELETE + "/{safeId}")
    public Response deleteSafe(@Context HttpServletRequest request, @PathParam("safeId") UUID id)
            throws NotFoundException, SecurityException, DataBaseException, com.omisoft.server.common.exceptions.NotFoundException {
        String reqUrl = request.getRequestURI();

        // get user from redis and db
        LoggedUserInfo loggedUserInfo = (LoggedUserInfo) request.getAttribute(USER_REDIS_DTO);
        User user = getDbUser(request, userDAO);
        PasswordSafe saveForRemoval = passwordSafeDAO.findById(id);
        user.getPasswordSafes().remove(saveForRemoval);
        userDAO.saveOrUpdate(user);

        if (!saveForRemoval.getInShares() && !saveForRemoval.getInGroup()) {
            passwordSafeDAO.remove(saveForRemoval);
        } else {
            // removing relations
            if (user.equals(saveForRemoval.getCreator())) {
                List<UUID> pIds = new ArrayList<>();
                pIds.add(saveForRemoval.getPasswordEncryptId());
                for (User usr : saveForRemoval.getUsers()) {
                    usr.getPasswordSafes().remove(saveForRemoval);

                }
                passwordSafeDAO.remove(saveForRemoval);
                userDAO.saveAll(saveForRemoval.getUsers());
            }
        }
        setLastAction(AuditActions.REMOVE_PASSWORD_SAFE);

        return Response.status(200)
                .entity(mapPersonalSavesWithUserToDTO(passwordSafeDAO.findPersonalSafes(user), user))
                .build();


    }

    /**
     * Updates a personal save.
     *
     * @param dto [can be an instance of PasswordSafeDTO or GroupSaveDTO] in this case -
     *            PasswordSafeDTO
     */
    @POST
    @Path(UPDATE)
    public Response updateSafe(@Context HttpServletRequest request, PasswordSafeDTO dto)
            throws DataBaseException, NotFoundException, SecurityException, com.omisoft.server.common.exceptions.NotFoundException {
        log.info("PASSWORD SAFE DTO:" + dto);

        // getting the user from redis and db
        // and the personal save from db
        LoggedUserInfo loggedUserInfo = getLoggedUser(request);
        User user = getDbUser(request, userDAO);
        PasswordSafe ps = passwordSafeDAO.findById(dto.getId());
        // getting the personal secret key to encrypt the updated password
        if (dto.getPassword() != null) {
            PasswordSafeKey key = passwordSafeKeyDAO.findKeysByUserAndPasswordSafe(user.getId(), ps.getId());
            EncryptionDTO encryptionDTO = SecureKeystore.decrtptEncryptionKeys(key);
          ps.setPassword(
                    SecureKeystore.encryptWithKeys(dto.getPassword().toBytes(), encryptionDTO).getEncryptedMessage());
        }

        ps.setName(dto.getName());
        ps.setDescription(dto.getDescription());
        ps.setUsername(dto.getAccountName());
        ps.setUrl(getHostFromUrl(dto.getUrl()));
        setLastAction(AuditActions.UPDATE_PASSWORD_SAFE);
        passwordSafeDAO.saveOrUpdate(ps);
        // return Response.status(200).entity(mapPersonalSavesToDTO(user.getPasswordSafes())).build();
        return Response.status(200).build();

    }


    /**
     * Searches for passwords by url
     */
    @POST
    @Path(GET_BY_URL)
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response getPersonalSafesByUrl(@Context HttpServletRequest request,
                                          @FormParam("url") String url) throws NotFoundException {

        User user = getDbUser(request, userDAO);
        // finding the personal saves that match the url
        List<PasswordSafe> foundSaves = passwordSafeDAO.findSafeByUrl(user.getId(), url);
        List<PasswordSafeDTO> dtos = mapPersonalSavesToDTO(foundSaves);
        return Response.status(200).entity((dtos)).build();


    }

    @POST
    @Path(GET_BY_APP_ID)
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response getPersonalSafesByAppId(@Context HttpServletRequest request,
                                            @FormParam("app_id") String appId) throws NotFoundException {
        User user = getDbUser(request, userDAO);
        Set<PasswordSafe> passwordSafes = passwordSafeDAO.findByAppId(user, appId);
        List<PasswordSafeDTO> dtos = mapPersonalSavesToDTO(passwordSafes);
        // finding the personal saves that match the url
        return Response.status(200).entity((dtos)).build();


    }

    /**
     * Get User personal safe
     *
     * @param id PasswordSafe
     * @return PasswordSafeDTO
     */
    @POST
    @Path(GET_BY_ID)
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response getPersonalSafesById(@Context HttpServletRequest request,
                                         @FormParam("id") UUID id)
            throws NotFoundException, DataBaseException, com.omisoft.server.common.exceptions.NotFoundException {

        LoggedUserInfo loggedUserInfo = getLoggedUser(request);
        PasswordSafeDTO passwordSafeDTO = new PasswordSafeDTO(passwordSafeDAO.findById(id));
        if (passwordSafeDTO.getSharedWithUsers() != null) {
            for (UserWithAESDTO userWithAESDTO : passwordSafeDTO.getSharedWithUsers()) {
                if (loggedUserInfo.getEmail().equals(userWithAESDTO.getEmail())) {
                    passwordSafeDTO.setClientAESKey(
                            org.apache.commons.codec.binary.Base64.decodeBase64(userWithAESDTO.getKey()));
                }
            }
        }
        passwordSafeDTO.setPassword(null);
        return Response.status(200).entity(passwordSafeDTO).build();


    }

    @POST
    @Path(GET_SHARE_BY_ID + "/{shareId}")
    public Response getSharedPersonalSafesById(@Context HttpServletRequest request,
                                               @PathParam("shareId") UUID id)
            throws NotFoundException, DataBaseException, com.omisoft.server.common.exceptions.NotFoundException {

        User user = getDbUser(request, userDAO);
        PasswordSafe ps = passwordSafeDAO.findById(id);
        PasswordSafeDTO passwordSafeDTO = new PasswordSafeDTO(ps, user.equals(ps.getCreator()),
                ps.getUsers().stream().map(User::getEmail).collect(Collectors.toList()));
        passwordSafeDTO.setPassword(null);
        return Response.status(200).entity(passwordSafeDTO).build();


    }

    /**
     * Get password from personal safe
     *
     * @param id PasswordSafe
     * @return SecureString
     */
    @POST
    @Path(VIEW_PASSWORD)
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response viewPersonalSafesById(@Context HttpServletRequest request,
                                          @FormParam("id") UUID id)
            throws SecurityException, NotFoundException, DataBaseException, com.omisoft.server.common.exceptions.NotFoundException {

        LoggedUserInfo loggedUserInfo = getLoggedUser(request);
        User user = getDbUser(request, userDAO);
        PasswordSafe passwordSafe = passwordSafeDAO.findById(id);
        PasswordSafeKey key = passwordSafeKeyDAO.findKeysByUserAndPasswordSafe(user.getId(), passwordSafe.getId());
        EncryptionDTO encryptionDTO = SecureKeystore.decrtptEncryptionKeys(key);
        encryptionDTO.setEncryptedMessage(passwordSafe.getPassword());


        SecureString decryptedPassword = SecureKeystore
                .decryptPassword(encryptionDTO);

//    secureKeystore.close(Constants.MASTER_PASSWORD, loggedUserInfo.getPassword(),
//        Constants.CONSTANT_PASSWORD);

        PasswordSafeDTO dto = new PasswordSafeDTO(passwordSafe);

        if (user.equals(passwordSafe.getCreator())) {
            dto.setCreator(true);
        }
        if (!passwordSafe.getInShares() && !passwordSafe.getInGroup()) {
            dto.setClientAESKey(passwordSafe.getClientAESKey());

        } else {
            dto.setClientAESKey(getClientKeyByEmail(passwordSafe, user.getEmail()));

        }

        dto.setPassword(decryptedPassword);
        return Response.status(200).entity(dto).build();

    }

    private byte[] getClientKeyByEmail(PasswordSafe passwordSafe, String email)
            throws DataBaseException {
        UserWithAES usr = userWithAesDAO.findByPasswordSafeAndEmail(passwordSafe, email);
        return org.apache.commons.codec.binary.Base64.decodeBase64(usr.getKey());
    }


    @GET
    @Path(DOWNLOAD + "/{id}")
    // @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFile(@Context HttpServletRequest request,
                            @CookieParam("authorization") Cookie cookie, @PathParam(value = "id") final UUID id)
            throws DataBaseException, com.omisoft.server.common.exceptions.NotFoundException {

//        try {
//            LoggedUserInfo loggedUserInfo = authority.getUser(cookie.getValue());
//            FileData data = fileDao.findById(id);
//            // TODO
//            byte[] result;
//            if (data.getEncrypt().equals(true)) {
//                User user = userDAO.findUserByEmailWithExc(loggedUserInfo.getEmail());
//                PasswordSafeKey key = passwordSafeKeyDAO.findKeysByUserAndPasswordSafe(user.getId(), pas.getId());
//                EncryptionDTO encryptionDTO = SecureKeystore.decrtptEncryptionKeys(key);
//                encryptionDTO.setEncryptedMessage(data.getContent());
//
//                result =
//                        SecureKeystore.decryptFile(encryptionDTO);
//
//            } else {
//                result = data.getContent();
//            }
//            // return Response.ok(result)
//            // .header("Content-Disposition", "attachment; filename=" + data.getFilename() + "").build();
//            data.setContent(result);
//
//            return Response.ok(data).build();
//
//        } catch (com.omisoft.keepassa.exceptions.NotFoundException | SecurityException e) {
//            log.error("FILE NOT FOUND", e);
//            return Response.status(404).entity(new ErrorDTO(request.getRequestURI(), "File not found!"))
//                    .build();
//        }
      return null;


    }

    /**
     * Share password with other users
     */
    //TODO
    @POST
    @Path(SHARE)
    public Response sharePassword(@Context HttpServletRequest request,
                                  PasswordSafeDTO sharePasswordDTO)
            throws DataBaseException, NotFoundException, SecurityException {

        LoggedUserInfo userDTO = getLoggedUser(request);
        User senderUser = getDbUser(request, userDAO);
        PasswordSafe sharedSafe = passwordSafeDAO.saveOrUpdate(new PasswordSafe(sharePasswordDTO));
        sharedSafe.setCreator(senderUser);


//    sharedSafe.setPassword(encryptedPassword);
        senderUser.getPasswordSafes().add(sharedSafe);
        sharedSafe = passwordSafeDAO.saveOrUpdate(sharedSafe);
        senderUser = userDAO.saveOrUpdate(senderUser);

        List<UUID> pIds = new ArrayList<>();
        pIds.add(sharedSafe.getPasswordEncryptId());

        for (UserWithAESDTO userWithAESDTO : sharePasswordDTO.getSharedWithUsers()) {

            User invitedUser = userDAO.findUserByEmailWithNull(userWithAESDTO.getEmail());
            if (invitedUser == null) {
                // send invite email
                continue;
            }

            UserWithAES userWithAES = new UserWithAES(userWithAESDTO);
            userWithAES.setPasswordSafe(sharedSafe);
            sharedSafe.getUserWithAESList().add(userWithAES);
            userWithAesDAO.saveOrUpdate(userWithAES);

            // sharing keys
            setLastAction(AuditActions.SHARE_PASSWORD_SAFE);
            // UserWithAES userWithAES = userWithAesDao.saveOrUpdate(new UserWithAES(userWithAes));
            // sharedSafe.getUserWithAESList().add(new UserWithAES(userWithAes));
            PasswordSafeKey key = new PasswordSafeKey();
            key.setUser(invitedUser);
            key.setPasswordSafe(sharedSafe);
            invitedUser.getPasswordSafes().add(sharedSafe);
            sharedSafe.getUsers().add(invitedUser);
            invitedUser = userDAO.saveOrUpdate(invitedUser);
            sharedSafe = passwordSafeDAO.saveOrUpdate(sharedSafe);

            // sending email with share
            emailSenderService.sendShareNotifyEmail(invitedUser.getEmail(), userDTO.getEmail(),
                    sharedSafe.getName());


        }
        return Response.status(200).entity(new SuccessDTO("Successfully shared the password safe!"))
                .build();

    }

    @POST
    @Path(LIST + SHARE)
    public Response listShares(@Context HttpServletRequest request) throws NotFoundException {
        User user = getDbUser(request, userDAO);
        Set<PasswordSafe> shared = new HashSet<>();
        for (PasswordSafe safe : user.getPasswordSafes()) {
            if (safe.getInShares() == Boolean.TRUE) {
                shared.add(safe);
            }
        }
        return Response.status(200).entity(mapSharedSavesToDTO(shared, user)).build();

    }

    @POST
    @Path(GET_MULTIPLE_PUB_KEYS)
    public Response getPubKeys(@Context HttpServletRequest request, List<String> emails) {

        List<UserWithAESDTO> users = new ArrayList<>();
        for (String email : emails) {
            User usr = userDAO.findUserByEmailWithNull(email);
            if (usr != null) {
                users.add(new UserWithAESDTO(usr.getEmail(), usr.getClientPublicKey()));
            }
        }
        return Response.status(200).entity(users).build();

    }


    @POST
    @Path(STOP_SHARING + "/{shareId}")
    public Response stopSharing(@Context HttpServletRequest request,
                                @PathParam("shareId") UUID shareId)
            throws NotFoundException, SecurityException, DataBaseException, com.omisoft.server.common.exceptions.NotFoundException {
        String reqUrl = request.getRequestURI();
        LoggedUserInfo userDTO = getLoggedUser(request);
        User creator = getDbUser(request, userDAO);
        PasswordSafe ps = passwordSafeDAO.findById(shareId);
        List<UUID> pIds = new ArrayList<>();
        pIds.add(ps.getPasswordEncryptId());
        if (creator.equals(ps.getCreator())) {
            for (Iterator<User> i = ps.getUsers().iterator(); i.hasNext(); ) {
                User usr = i.next();
                if (!usr.equals(creator)) {
                    i.remove();
                    usr.getPasswordSafes().remove(ps);
                    setLastAction(AuditActions.REMOVE_SHARE);

                    userDAO.saveOrUpdate(usr);
                }
            }
            passwordSafeDAO.saveOrUpdate(ps);
        } else {
            Response.status(404).entity(new ErrorDTO(reqUrl,
                    "You cannot remove a shared password which you don't have rights for!")).build();
        }
        return Response.status(200)
                .entity(new SuccessDTO("Successfully stopped sharing the password safe!")).build();

    }

    @POST
    @Path(STOP_SHARING_WITH)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response stopSharingWith(@Context HttpServletRequest request,
                                    @FormParam("shareId") UUID shareId, @FormParam("userEmail") String userEmail)
            throws NotFoundException, SecurityException, DataBaseException, com.omisoft.server.common.exceptions.NotFoundException {
        String reqUrl = request.getRequestURI();
        LoggedUserInfo userDTO = getLoggedUser(request);
        User creator = getDbUser(request, userDAO);
        PasswordSafe ps = passwordSafeDAO.findById(shareId);
        List<UUID> pIds = new ArrayList<>();
        pIds.add(ps.getPasswordEncryptId());
        if (creator.equals(ps.getCreator())) {
            if (userEmail.equals(creator.getEmail())) {
                return Response.status(402)
                        .entity(new ErrorDTO(reqUrl,
                                "You are the creator of this safe. You cannot stop sharing with yourself!"))
                        .build();
            }
            for (User usr : ps.getUsers()) {
                if (userEmail.equals(usr.getEmail())) {
                    ps.getUsers().remove(usr);
                    usr.getPasswordSafes().remove(ps);
                    setLastAction(AuditActions.REMOVE_SHARE);
                    userDAO.saveOrUpdate(usr);
                    passwordSafeDAO.saveOrUpdate(ps);
                    break;
                }
            }

        } else {
            Response.status(404).entity(new ErrorDTO(reqUrl,
                    "You cannot remove a shared password which you don't have rights for!")).build();
        }
        PasswordSafeDTO passwordSafeDTO = new PasswordSafeDTO(ps, creator.equals(ps.getCreator()),
                ps.getUsers().stream().map(User::getEmail).collect(Collectors.toList()));
        return Response.status(200).entity(passwordSafeDTO).build();

    }

    @POST
    @Path(REMOVE_SHARE + "/{shareId}")
    public Response removeShare(@Context HttpServletRequest request,
                                @PathParam("shareId") String shareId)
            throws NotFoundException, SecurityException, DataBaseException, com.omisoft.server.common.exceptions.NotFoundException {

        UUID shareUUID = UUID.fromString(shareId);
        LoggedUserInfo userDTO = getLoggedUser(request);
        User user = getDbUser(request, userDAO);
        PasswordSafe ps = passwordSafeDAO.findById(shareUUID);
        List<UUID> pIds = new ArrayList<>();
        pIds.add(ps.getPasswordEncryptId());
        ps.getUsers().remove(user);
        user.getPasswordSafes().remove(ps);
        setLastAction(AuditActions.REMOVE_SHARE);
        userDAO.saveOrUpdate(user);
        passwordSafeDAO.saveOrUpdate(ps);

        return Response.status(200)
                .entity(new SuccessDTO("This password safe will no longer be shared with you!")).build();


    }


    private List<PasswordSafeDTO> mapPersonalSavesToDTO(Set<PasswordSafe> saves) {
        List<PasswordSafeDTO> dtos = new ArrayList<>();
        for (PasswordSafe save : saves) {
            dtos.add(new PasswordSafeDTO(save));
        }
        return dtos;
    }

    private List<PasswordSafeDTO> mapPersonalSavesToDTO(List<PasswordSafe> saves) {
        List<PasswordSafeDTO> dtos = new ArrayList<>();
        for (PasswordSafe save : saves) {
            dtos.add(new PasswordSafeDTO(save));
        }
        return dtos;
    }

    private List<PasswordSafeDTO> mapPersonalSavesWithUserToDTO(Set<PasswordSafe> saves, User user) {
        List<PasswordSafeDTO> dtos = new ArrayList<>();
        for (PasswordSafe save : saves) {
            if (user.equals(save.getCreator())) {
                dtos.add(new PasswordSafeDTO(save, true));
            } else {
                dtos.add(new PasswordSafeDTO(save, false));
            }
        }
        return dtos;
    }

    private List<PasswordSafeDTO> mapSharedSavesToDTO(Set<PasswordSafe> saves, User user) {
        List<PasswordSafeDTO> dtos = new ArrayList<>();
        for (PasswordSafe save : saves) {
            if (save.getCreator().equals(user)) {
                dtos.add(new PasswordSafeDTO(save, true,
                        save.getUsers().stream().map(User::getEmail).collect(Collectors.toList())));
            } else {
                dtos.add(new PasswordSafeDTO(save, false,
                        save.getUsers().stream().map(User::getEmail).collect(Collectors.toList())));
            }
        }
        return dtos;
    }


}


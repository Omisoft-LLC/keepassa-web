package com.omisoft.keepassa.endpoints;

import static com.omisoft.keepassa.constants.Constants.AUTHORIZATION_HEADER;
import static com.omisoft.keepassa.constants.Constants.RECAPTCHA_KEY;
import static com.omisoft.keepassa.constants.Constants.RECAPTCHA_VERIFY;
import static com.omisoft.keepassa.constants.RestUrl.ACCOUNT;
import static com.omisoft.keepassa.constants.RestUrl.LOGIN;
import static com.omisoft.keepassa.constants.RestUrl.LOGOUT;
import static com.omisoft.keepassa.constants.RestUrl.REGISTER;
import static com.omisoft.keepassa.constants.RestUrl.REGISTER_COMPANY;
import static com.omisoft.keepassa.constants.RestUrl.TF_VERIFY;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.omisoft.keepassa.authority.UserAuthority;
import com.omisoft.keepassa.configuration.FileConfigService;
import com.omisoft.keepassa.constants.AuditActions;
import com.omisoft.keepassa.constants.Constants;
import com.omisoft.keepassa.dao.CompanyDAO;
import com.omisoft.keepassa.dao.CompanyInviteDAO;
import com.omisoft.keepassa.dao.LdapDAO;
import com.omisoft.keepassa.dao.TrustStoreDAO;
import com.omisoft.keepassa.dao.UserDAO;
import com.omisoft.keepassa.dto.LoggedUserInfo;
import com.omisoft.keepassa.dto.UserInfoDTO;
import com.omisoft.keepassa.dto.rest.BasicUserDTO;
import com.omisoft.keepassa.dto.rest.ErrorDTO;
import com.omisoft.keepassa.dto.rest.LoggedUserDTO;
import com.omisoft.keepassa.dto.rest.RegisterDTO;
import com.omisoft.keepassa.dto.rest.SuccessDTO;
import com.omisoft.keepassa.entities.users.Company;
import com.omisoft.keepassa.entities.users.User;
import com.omisoft.keepassa.exceptions.SecurityException;
import com.omisoft.keepassa.services.EmailSenderService;
import com.omisoft.keepassa.utils.AuthUtils;
import com.omisoft.keepassa.utils.CertAndKeyUtils;
import com.omisoft.keepassa.utils.TimeBasedOneTimePasswordUtil;
import com.omisoft.keepassa.utils.Token;
import com.omisoft.server.common.exceptions.DataBaseException;
import io.swagger.annotations.Api;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Created by leozhekov on 11/16/16. Endpoint for login/register/logout and all other REST methods
 * that should not go through the Authority filter.
 */
@Singleton
@Slf4j
@Path(ACCOUNT)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(tags = {"account"}, value = ACCOUNT, description = "Account creation")
public class AccountEndpoint implements BaseEndpoint {

  public static final Pattern PASSWORD_VALIDATION_REGEX =
      Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$");
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("dd/MM/YYYY");
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm");
  private final UserDAO userDAO;
  private final UserAuthority authority;
  private final EmailSenderService emailSenderService;
  private final LdapDAO ldapDAO;
  private final TrustStoreDAO trustStoreDAO;
  private final CompanyDAO companyDao;
  private final CompanyInviteDAO companyInviteDAO;
  private ObjectMapper mapper;
  @Inject
  private Injector injector;

  @Inject
  public AccountEndpoint(ObjectMapper mapper, UserDAO userDAO, UserAuthority authority,
      EmailSenderService emailSenderService,
      LdapDAO ldapDAO, TrustStoreDAO trustStoreDAO, CompanyDAO companyDao,
      CompanyInviteDAO companyInviteDAO) {
    this.mapper = mapper;
    this.userDAO = userDAO;
    this.authority = authority;
    this.emailSenderService = emailSenderService;
    this.ldapDAO = ldapDAO;
    this.trustStoreDAO = trustStoreDAO;
    this.companyDao = companyDao;
    this.companyInviteDAO = companyInviteDAO;
  }


  @GET
  public Response checkMutualSSL(@Context HttpServletRequest request)
      throws ServletException, IOException {
    String certEncoded = request.getHeader("X-SSL-ClientCert-Base64");
    log.info(certEncoded);

    // which encoding
    X509Certificate certificate = CertAndKeyUtils
        .getCertificateFromBytes(Base64.decodeBase64(certEncoded));
    if (trustStoreDAO.checkIfCertificateIsValid(request, certificate)) {
      LoggedUserInfo loggedUserInfo = authority
          .getUser(request.getHeader(Constants.AUTHORIZATION_HEADER));
      User user = userDAO.findUserByEmailWithNull(loggedUserInfo.getEmail());
      try {
        String currentNumber = TimeBasedOneTimePasswordUtil
            .generateCurrentNumber(user.getMutualSslOTPKey());
        return Response.ok(currentNumber).build();
      } catch (GeneralSecurityException e) {
        e.printStackTrace();
      }
    } else {
      authority.getUser(request.getHeader(Constants.AUTHORIZATION_HEADER));
      authority.removeUser(request.getHeader(Constants.AUTHORIZATION_HEADER));
    }

    return Response.status(417).entity(
        new ErrorDTO("Error validating user certificate", "Error validating user certificate"))
        .build();
  }


  /**
   * A quick test REST to be run from a browser.
   */
  @GET
  @Path("/test")
  public Response test(@Context HttpServletRequest request) {
    log.info(request.getRequestURI());
    return Response.status(200).build();
  }

  /**
   * Login REST.
   *
   * @param loginDTO {email:string, password:string}
   */
  @POST
  @Path(LOGIN)
  public Response login(@Context HttpServletRequest request, BasicUserDTO loginDTO)
      throws JsonProcessingException, Exception {
    String reqUrl = request.getRequestURI();

    if (FileConfigService.getInstance().isDev()) {
      loginDTO.setDisableCaptcha(true);
    }
//      if (!loginDTO.getDisableCaptcha()) {
//        if (StringUtils.isBlank(loginDTO.getVerify()) && !verify(loginDTO.getVerify())) {
//          return Response.status(404).entity(new ErrorDTO(reqUrl, "Bad Verification !")).build();
//        }
//      }

    if (ldapDAO.ldapIsActive() && !ldapDAO.isExist(loginDTO.getEmail())) {
      return Response.status(404)
          .entity(new ErrorDTO(reqUrl, "Invalid username and password combination!")).build();
    }

    // finding user by email
    User usr = userDAO.findUserByEmailWithNull(loginDTO.getEmail());
    if (usr == null) {
      return Response.status(404).entity(
          new ErrorDTO(reqUrl, "User with this username and password combination is not found!"))
          .build();
    }
    if (BCrypt.checkpw(usr.getPassword(), loginDTO.getPassword().toString())) {

      // security checks
      if (usr.getIsSuspended()) {
        return Response.status(402).entity(new ErrorDTO(reqUrl, "This account is suspended!"))
            .build();
      } else if (usr.getIsExpiredEnabled()) {
        if (usr.getIsExpired()) {
          return Response.status(402).entity(new ErrorDTO(reqUrl, "Account expired on "
              + DATE_FORMATTER.print(new DateTime(usr.getExpirationDate())))).build();
        }
      } else if (usr.getIsWorkHoursAllowed()) {
        if (!isBetweenWorkHours(usr)) {
          return Response.status(402)
              .entity(new ErrorDTO(reqUrl,
                  "You are restricted to use this account in the hours "
                      + TIME_FORMATTER.print(new DateTime(usr.getBeginWorkHour())) + " - "
                      + TIME_FORMATTER.print(new DateTime(usr.getEndWorkHour()))
                      + "! The time on the server is: " + TIME_FORMATTER.print(new DateTime())))
              .build();
        }


      }

      final Token token;
      token = AuthUtils.createToken(request.getRemoteHost(), usr.getId().toString());
      NewCookie cookie;
      if (FileConfigService.getInstance().isDev()) { // Allow express proxy, so set cookie to
        // secure - false
        cookie = new NewCookie(AUTHORIZATION_HEADER, token.getToken(), "/", "", 1,"Auth Cookie",
            -1,  false);
      } else {
        cookie = new NewCookie(AUTHORIZATION_HEADER, token.getToken(), "/", "", 1, "Auth Cookie",
            -1, false);
      }

      // Add to cache TODO - do we need it if we are using session >
      authority.addUser(token.getToken(), usr, loginDTO.getPassword());
      // Add to session-scope
      UserInfoDTO userInfoDTO = injector.getInstance(UserInfoDTO.class);
      userInfoDTO.setProps(usr, request.getRemoteAddr());

      LoggedUserDTO dto =
          new LoggedUserDTO(usr.getEmail(), usr.getClientPublicKey(), usr.getIsTwoFEnabled());
      dto.setToken(token.getToken());
      // boolean isAndroid = CLIENT_ID_VALUE.equals(request.getHeader(CLIENT_ID));
      return Response.status(200).entity(dto).header(AUTHORIZATION_HEADER, token.getToken())
          .cookie(cookie).build();
    } else {
      log.error("CAN'T OPEN KEYSTORE");
      return Response.status(404)
          .entity(new ErrorDTO(reqUrl, "Invalid username and password combination!")).build();
    }


  }

  @POST
  @Path(REGISTER_COMPANY)
  public Response registerCompany(@Context HttpServletRequest request, RegisterDTO registerDTO)
      throws DataBaseException, SecurityException {
    if (!companyDao.existsCompany(registerDTO.getCompanyName())) {
      Response response = register(request, registerDTO);
      if (response.getStatus() != 200) {
        return Response.status(405)
            .entity(new ErrorDTO("Please, choose another email", "Please, choose another email"))
            .build();
      } else {// add company to user
        User user = userDAO.findUserByEmailWithNull(registerDTO.getEmail());
        Company company = new Company(user, registerDTO.getCompanyName());

        companyDao.saveOrUpdate(company);

        user.setCompany(company);
        userDAO.saveOrUpdate(user);
        return response;
      }
    } else {
      return Response.status(405)
          .entity(new ErrorDTO("Company already exists!", "Company already exists!")).build();
    }
  }


  /**
   * Register REST.
   *
   * @param registerDTO {emailSenderService:string, password:string}
   */
  @POST
  @Path(REGISTER)
  public Response register(@Context HttpServletRequest request, RegisterDTO registerDTO)
      throws SecurityException, DataBaseException {
    log.info("REGISTER ENDPOINT");
    // log.info(mapper.writeValueAsString(registerDTO));

//      if (StringUtils.isBlank(registerDTO.getVerify()) && !verify(registerDTO.getVerify())) {
//        return Response.status(404)
//            .entity(new ErrorDTO(request.getRequestURI(), "Bad Verification !")).build();
//      }
    if (StringUtils.isNotBlank(registerDTO.getInviteCode())) {
      companyInviteDAO.findByInviteCode(registerDTO.getInviteCode());
    }
    User foundUser = userDAO.findUserByEmailWithNull(registerDTO.getEmail());
    if (foundUser == null) {
      User user = new User();

      // user.setFirstName(resetPasswordDTO.getName());
      user.setEmail(registerDTO.getEmail());
      user.setClientPublicKey(registerDTO.getPublicKey());
      user.setPassword(BCrypt.hashpw(registerDTO.getPassword().toString(),BCrypt.gensalt(12)));
      setLastAction(AuditActions.REGISTER_ACTION);


      user = userDAO.saveOrUpdate(user);

      // sending him an emailSenderService

      emailSenderService.sendRegistrationVerificationEmail(registerDTO.getEmail());

      log.info("SUCCESS REGISTER!");
      return Response.status(200).entity(new SuccessDTO("Successful registration!")).build();
    } else {
      return Response.status(405)
          .entity(new ErrorDTO(request.getRequestURI(), "User with this email already exists!"))
          .build();
    }

  }

  /**
   * Logout REST.
   */
  @POST
  @Path(LOGOUT)
  public Response logout(@Context HttpServletRequest request) {

    log.info("logged out");
    String authorization = request.getHeader(AUTHORIZATION_HEADER);
    authority.removeUser(authorization);
    return Response.status(200).entity(new SuccessDTO("Successful logout!")).build();

  }

  @POST
  @Path(TF_VERIFY + "/{verification}")
  public Response verifyTwoF(@Context HttpServletRequest request,
      @PathParam("verification") String verification)
      throws  Exception {

    // LoggedUserInfo userRedisDTO = authority.getDbUser(request.getHeader(AUTHORIZATION_HEADER));
    // User usr = userDao.findUserByEmailWithExc(userRedisDTO.getEmail());
    User usr = getDbUser(request, userDAO);
    String currentNumber =
        TimeBasedOneTimePasswordUtil.generateCurrentNumber(new String(usr.getAuthKey(), "UTF-8"));
    if (currentNumber.equals(verification)) {
      return Response.status(200).entity(new SuccessDTO("Successful verification!")).build();
    } else {
      return Response.status(402)
          .entity(new ErrorDTO(request.getRequestURI(), "Wrong verification number!")).build();
    }


  }

  /**
   * that is a check for if the time of login is between the work hours
   */
  private boolean isBetweenWorkHours(User user) {
    DateTime beginWork = new DateTime(user.getBeginWorkHour());
    DateTime endWork = new DateTime(user.getEndWorkHour());
    DateTime now = new DateTime();

    return beginWork.isBefore(now) && endWork.isAfter(now);
  }

  private Boolean checkPasswordExpirationDate(Date passwordExpirationDate) {
    DateTime now = new DateTime();

    DateTime passwordExpirationDateTime = new DateTime(passwordExpirationDate);
    return !passwordExpirationDateTime.isAfter(now);
  }

  private Boolean checkAccountExpirationDate(Date expirationDate) {
    DateTime now = new DateTime();
    DateTime accountExpirationDate = new DateTime(expirationDate);
    return !accountExpirationDate.isAfter(now);
  }

  private boolean verify(String token) {
    try {
      HttpClient httpclient = HttpClients.createDefault();
      HttpPost httppost = new HttpPost(RECAPTCHA_VERIFY);
      List<NameValuePair> params = new ArrayList<>(2);
      params.add(new BasicNameValuePair("secret", RECAPTCHA_KEY));
      params.add(new BasicNameValuePair("response", token));
      httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
      HttpResponse response = httpclient.execute(httppost);
      HttpEntity entity = response.getEntity();
      String responseString = EntityUtils.toString(entity, "UTF-8");
      JsonNode responseNode = mapper.readValue(responseString, JsonNode.class);
      JsonNode success = responseNode.get("success");
      return success != null && success.asText().equals("true");
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

  }
}

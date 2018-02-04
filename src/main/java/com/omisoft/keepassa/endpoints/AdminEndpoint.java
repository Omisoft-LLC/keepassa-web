package com.omisoft.keepassa.endpoints;

import static com.omisoft.keepassa.constants.RestUrl.ADD_USER_TO_DEPARTMENTS;
import static com.omisoft.keepassa.constants.RestUrl.ADMIN;
import static com.omisoft.keepassa.constants.RestUrl.CREATE_DEPARTMENT;
import static com.omisoft.keepassa.constants.RestUrl.DELETE_DEPARTMENT;
import static com.omisoft.keepassa.constants.RestUrl.DELETE_USER;
import static com.omisoft.keepassa.constants.RestUrl.DEPARTMENT;
import static com.omisoft.keepassa.constants.RestUrl.DEPARTMENTS;
import static com.omisoft.keepassa.constants.RestUrl.DEPARTMENT_MEMBERS;
import static com.omisoft.keepassa.constants.RestUrl.INVITE_USERS;
import static com.omisoft.keepassa.constants.RestUrl.REMOVE_USER_FROM_DEPT;
import static com.omisoft.keepassa.constants.RestUrl.SEARCH_FOR_DEPARTMENT;
import static com.omisoft.keepassa.constants.RestUrl.SECURE;
import static com.omisoft.keepassa.constants.RestUrl.SUSPEND_USER;
import static com.omisoft.keepassa.constants.RestUrl.UPDATE_DEPARTMENT;
import static com.omisoft.keepassa.constants.RestUrl.UPDATE_SECURITY;
import static com.omisoft.keepassa.constants.RestUrl.UPDATE_USER;
import static com.omisoft.keepassa.constants.RestUrl.UPLOAD_CSR;
import static com.omisoft.keepassa.constants.RestUrl.USER;
import static com.omisoft.keepassa.constants.RestUrl.USERS;
import static com.omisoft.keepassa.constants.RestUrl.USER_DEPARTMENTS;
import static com.omisoft.keepassa.constants.RestUrl.USER_SECURITY;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.omisoft.keepassa.constants.AuditActions;
import com.omisoft.keepassa.dao.CompanyDAO;
import com.omisoft.keepassa.dao.CompanyInviteDAO;
import com.omisoft.keepassa.dao.DepartmentDAO;
import com.omisoft.keepassa.dao.SettingsDAO;
import com.omisoft.keepassa.dao.TrustStoreDAO;
import com.omisoft.keepassa.dao.UserDAO;
import com.omisoft.keepassa.dto.UserInfoDTO;
import com.omisoft.keepassa.dto.rest.AddToDepartmentsDTO;
import com.omisoft.keepassa.dto.rest.CertInTrustStoreDTO;
import com.omisoft.keepassa.dto.rest.DepartmentInfoDTO;
import com.omisoft.keepassa.dto.rest.ErrorDTO;
import com.omisoft.keepassa.dto.rest.FileDTO;
import com.omisoft.keepassa.dto.rest.FileWithPassDTO;
import com.omisoft.keepassa.dto.rest.MessageSendDTO;
import com.omisoft.keepassa.dto.rest.SecurityDTO;
import com.omisoft.keepassa.dto.rest.SelectTagsSearchDTO;
import com.omisoft.keepassa.dto.rest.SuccessDTO;
import com.omisoft.keepassa.dto.rest.UserInDeptDTO;
import com.omisoft.keepassa.entities.users.Company;
import com.omisoft.keepassa.entities.users.CompanyInvite;
import com.omisoft.keepassa.entities.users.Department;
import com.omisoft.keepassa.entities.users.User;
import com.omisoft.keepassa.exceptions.NotFoundException;
import com.omisoft.keepassa.exceptions.SecurityException;
import com.omisoft.keepassa.interceptors.AdminRequest;
import com.omisoft.keepassa.services.EmailSenderService;
import com.omisoft.keepassa.structures.SecureString;
import com.omisoft.keepassa.utils.TimeBasedOneTimePasswordUtil;
import com.omisoft.server.common.exceptions.DataBaseException;
import io.swagger.annotations.Api;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Created by leozhekov on 1/11/17.
 */
@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path(SECURE + ADMIN)
@Api(tags = {"admin"}, value = SECURE + ADMIN, description = "Admin functionality for hosted mode")
@AdminRequest
public class AdminEndpoint implements BaseEndpoint {

  private static final String MUTUAL_SSL = "MUTUAL_SSL";
  private static final String DATE_PATTERN = "DD/MM/YYYY";
  private final ObjectMapper mapper;
  private final UserDAO userDAO;
  private final DepartmentDAO departmentDao;
  private final SettingsDAO settingsDAO;
  private final TrustStoreDAO trustStoreDAO;
  private final Executor executor;
  private final CompanyDAO companyDAO;
  private final CompanyInviteDAO companyInviteDAO;
  private final EmailSenderService emailService;

  @Inject
  public AdminEndpoint(ObjectMapper mapper, UserDAO userDAO, DepartmentDAO departmentDao,
      CompanyInviteDAO companyInviteDAO,
      SettingsDAO settingsDAO, TrustStoreDAO trustStoreDAO, Executor executor,
      CompanyDAO companyDAO, EmailSenderService emailService) {
    this.mapper = mapper;
    this.userDAO = userDAO;
    this.departmentDao = departmentDao;
    this.settingsDAO = settingsDAO;
    this.trustStoreDAO = trustStoreDAO;
    this.executor = executor;
    this.companyDAO = companyDAO;
    this.companyInviteDAO = companyInviteDAO;
    this.emailService = emailService;
  }

  @POST
  @Path(USER + "/{userId}")
  public Response getUserById(@Context HttpServletRequest request, @PathParam("userId") UUID userId)
      throws NotFoundException, SecurityException {
    User admin = getDbUser(request, userDAO);
    User userForEdit = userDAO.findByIdAndCompany(userId, admin.getCompany());
    UserInfoDTO userInfoDTO = new UserInfoDTO(userForEdit);
    return Response.status(200).entity(userInfoDTO).build();

  }

  @POST
  @Path(USER_DEPARTMENTS + "/{userId}")
  public Response getUserDepts(@Context HttpServletRequest request,
      @PathParam("userId") UUID userId) throws NotFoundException, SecurityException {
    User admin = getDbUser(request, userDAO);
    User userForEdit = userDAO.findByIdAndCompany(userId, admin.getCompany());
    return Response.status(200).entity(mapDepartmentsToDTO(userForEdit.getDepartments())).build();

  }

  @POST
  @Path(USER_SECURITY + "/{userId}")
  public Response getUserSecurity(@Context HttpServletRequest request,
      @PathParam("userId") UUID userId) throws NotFoundException, SecurityException {
    User admin = getDbUser(request, userDAO);

    User userForEdit = userDAO.findByIdAndCompany(userId, admin.getCompany());
    return Response.status(200).entity(new SecurityDTO(userForEdit)).build();

  }

  @POST
  @Path(USERS)
  public Response getUsers(@Context HttpServletRequest request)
      throws com.omisoft.keepassa.exceptions.NotFoundException {
    User admin = getDbUser(request, userDAO);

    List<User> usersFromDb = userDAO.findAllByCompany(admin.getCompany());
    List<UserInfoDTO> usersForWeb = new ArrayList<>();
    for (User user : usersFromDb) {
      usersForWeb.add(new UserInfoDTO(user, getDepartmentsNames(user.getDepartments())));
    }
    return Response.status(200).entity(usersForWeb).build();

  }

  @POST
  @Path(CREATE_DEPARTMENT)
  public Response createDepartment(@Context HttpServletRequest request,
      DepartmentInfoDTO departmentInfoDTO) throws NotFoundException, DataBaseException {
    User admin = getDbUser(request, userDAO);

    User departmentHead = userDAO
        .findUserByEmailAndCompany(departmentInfoDTO.getDepartmentHeadEmail(), admin.getCompany());
    if (departmentHead == null) {
      return Response.status(404).entity(new ErrorDTO("Please, select a user from your company",
          "Please, select a user from your company")).build();
    } else {
      Department department = new Department(departmentInfoDTO, departmentHead);
      department.getUsers().add(departmentHead);
      department.setCompany(admin.getCompany());
      Company c = admin.getCompany();
      c.getDepartments().add(department);
      setLastAction(AuditActions.CREATE_NEW_DEPARTMENT);

      Department savedDept = departmentDao.saveOrUpdate(department);
      departmentHead.getDepartments().add(savedDept);
      setLastAction(AuditActions.SET_DEPARTMENT_HEAD);
      userDAO.saveOrUpdate(departmentHead);
      companyDAO.saveOrUpdate(c);
      return Response.status(200).entity(new SuccessDTO("Successful department creation!")).build();
    }
  }


  @POST
  @Path(DEPARTMENT + "/{departmentId}")
  public Response getDepartmentById(@Context HttpServletRequest request,
      @PathParam("departmentId") UUID departmentId) throws NotFoundException, DataBaseException {
    User admin = getDbUser(request, userDAO);

    Department department = departmentDao.findByIdAndCompany(departmentId, admin.getCompany());

    DepartmentInfoDTO departmentInfoDTO = new DepartmentInfoDTO(department);
    return Response.status(200).entity(departmentInfoDTO).build();

  }

  @POST
  @Path(DEPARTMENT_MEMBERS + "/{departmentId}")
  public Response getDepartmentMembers(@Context HttpServletRequest request,
      @PathParam("departmentId") UUID departmentId) throws NotFoundException, DataBaseException {
    User admin = getDbUser(request, userDAO);

    Department department = departmentDao.findByIdAndCompany(departmentId, admin.getCompany());

    return Response.status(200).entity(mapUsersToMembers(department.getUsers())).build();

  }


  @POST
  @Path(DEPARTMENTS)
  public Response getDepartments(@Context HttpServletRequest request)
      throws NotFoundException, DataBaseException {
    User admin = getDbUser(request, userDAO);
    List<Department> departmentsFromDb = departmentDao.findAllByCompany(admin.getCompany());
    List<DepartmentInfoDTO> departmentsForWeb = new ArrayList<>();
    for (Department department : departmentsFromDb) {
      departmentsForWeb.add(new DepartmentInfoDTO(department));
    }
    return Response.status(200).entity(departmentsForWeb).build();

  }

  @POST
  @Path(UPDATE_USER + "/{userId}")
  public Response updateUser(@Context HttpServletRequest request,
      @PathParam("userId") UUID userId, UserInfoDTO userInfoDTO)
      throws DataBaseException, NotFoundException, SecurityException {
    User admin = getDbUser(request, userDAO);

    User userForEdit = userDAO.findByIdAndCompany(userId, admin.getCompany());
    userForEdit.setFirstName(userInfoDTO.getFirstName());
    userForEdit.setLastName(userInfoDTO.getLastName());
    userForEdit.setPosition(userInfoDTO.getPosition());
    setLastAction(AuditActions.UPDATE_USER_INFO);

    userDAO.saveOrUpdate(userForEdit);
    return Response.status(200).entity(new SuccessDTO("Successfully updated user!"))
        .build();

  }

  @POST
  @Path(UPDATE_SECURITY + "/{userId}")
  public Response updateSecurity(@Context HttpServletRequest request,
      @PathParam("userId") UUID userId, SecurityDTO securityDTO)
      throws DataBaseException, NotFoundException, SecurityException {
    User admin = getDbUser(request, userDAO);

    User userForUpdate = userDAO.findByIdAndCompany(userId, admin.getCompany());
    userForUpdate.setIsExpiredEnabled(securityDTO.isExpiredEnabled());
//      userForUpdate.setIsPasswordExpiredEnabled(securityDTO.isPasswordExpiredEnabled());
    userForUpdate.setIsWorkHoursAllowed(securityDTO.isWorkHoursAllowed());
    userForUpdate.setIsSuspended(securityDTO.isSuspended());
    if (securityDTO.isExpiredEnabled() && StringUtils
        .isNotBlank(securityDTO.getAccExpDateString())) {
      DateTime date = DateTimeFormat.forPattern(DATE_PATTERN)
          .parseDateTime(securityDTO.getAccExpDateString());
      userForUpdate.setExpirationDate(date.toDate());
    }
//      if (securityDTO.isPasswordExpiredEnabled() && StringUtils.isNotBlank(securityDTO.getPassExpDateString())) {
//        DateTime date = DateTimeFormat.forPattern(DATE_PATTERN).parseDateTime(securityDTO.getPassExpDateString());
//        userForUpdate.setPasswordExpirationDate(date.toDate());
//      }
    if (securityDTO.isWorkHoursAllowed()) {
      String[] workFrom = securityDTO.getWorkFrom().split(":");
      String[] workTo = securityDTO.getWorkTo().split(":");
      DateTime beginWork = new DateTime()
          .withTime(Integer.parseInt(workFrom[0]), Integer.parseInt(workFrom[1]), 0, 0);
      DateTime endWork = new DateTime()
          .withTime(Integer.parseInt(workTo[0]), Integer.parseInt(workTo[1]), 0, 0);
      userForUpdate.setBeginWorkHour(beginWork.toDate());
      userForUpdate.setEndWorkHour(endWork.toDate());
    }
    setLastAction(AuditActions.UPDATE_USER_SECURITY);

    userDAO.saveOrUpdate(userForUpdate);
    return Response.status(200).entity(new SuccessDTO("Successfully put someone the restrictions!"))
        .build();

  }

  @POST
  @Path(DELETE_USER + "/{userId}")
  public Response deleteUser(@Context HttpServletRequest request,
      @PathParam("userId") UUID userId)
      throws NotFoundException, DataBaseException, SecurityException {
    User admin = getDbUser(request, userDAO);
    User userForUpdate = userDAO.findByIdAndCompany(userId, admin.getCompany());
    setLastAction(AuditActions.REMOVE_USER);
    userDAO.remove(userForUpdate);
    return Response.status(200).entity(new SuccessDTO("Successfully deleted the user!"))
        .build();

  }

  @POST
  @Path(SUSPEND_USER + "/{userId}")
  public Response suspendUser(@Context HttpServletRequest request, @PathParam("userId") UUID userId)
      throws NotFoundException, DataBaseException, SecurityException, com.omisoft.server.common.exceptions.NotFoundException {
    User admin = getDbUser(request, userDAO);
    User userForUpdate = userDAO.findByIdAndCompany(userId, admin.getCompany());

    setLastAction(AuditActions.SUSPEND_USER);

    userDAO.suspendUserById(userForUpdate.getId());
    return Response.status(200)
        .entity(new SuccessDTO("Successfully changed the security settings of the user!")).build();

  }

  @POST
  @Path(UPDATE_DEPARTMENT + "/{departmentId}")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response updateDepartment(@Context HttpServletRequest request,
      @FormParam("email") String email, @FormParam("name") String name,
      final @PathParam("departmentId") UUID departmentId)
      throws DataBaseException, NotFoundException, com.omisoft.server.common.exceptions.NotFoundException {
    User admin = getDbUser(request, userDAO);
    User newHeadOfDept = userDAO.findUserByEmailAndCompany(email, admin.getCompany());

    Department dept = departmentDao.findById(departmentId);
    dept.setDepartmentName(name);
    dept.setDepartmentHead(newHeadOfDept);
    setLastAction(AuditActions.UPDATE_DEPARTMENT);
    departmentDao.saveOrUpdate(dept);
    return Response.status(200).entity(new SuccessDTO("Successfully updated department!")).build();

  }


  @POST
  @Path(DELETE_DEPARTMENT + "/{departmentId}")
  public Response deleteDepartment(@Context HttpServletRequest request,
      @PathParam("departmentId") UUID departmentId)
      throws NotFoundException, DataBaseException, com.omisoft.server.common.exceptions.NotFoundException {
    User admin = getDbUser(request, userDAO);

    Department department = departmentDao.findByIdAndCompany(departmentId, admin.getCompany());
    setLastAction(AuditActions.REMOVE_DEPARTMENT);

    departmentDao.removeById(department.getId());
    return Response.status(200).entity(new SuccessDTO("Successfully removed the department!"))
        .build();

  }

  @POST
  @Path(REMOVE_USER_FROM_DEPT + "/{departmentId}/{userId}")
  public Response removeUserFromDept(@Context HttpServletRequest request,
      @PathParam("departmentId") UUID departmentId, @PathParam("userId") UUID userId)
      throws NotFoundException, DataBaseException, SecurityException {
    User admin = getDbUser(request, userDAO);
    Department dpt = departmentDao.findByIdAndCompany(departmentId, admin.getCompany());
    User userForRemoval = userDAO.findByIdAndCompany(userId, admin.getCompany());
    dpt.getUsers().remove(userForRemoval);
    userForRemoval.getDepartments().remove(dpt);
    setLastAction(AuditActions.REMOVE_USER_FROM_DEPARTMENT);
    departmentDao.saveOrUpdate(dpt);
    userDAO.saveOrUpdate(userForRemoval);
    return Response.status(200)
        .entity(new SuccessDTO("Successfully removed the user from the department!"))
        .build();

  }

  @POST
  @Path(ADD_USER_TO_DEPARTMENTS)
  public Response searchForDepartment(@Context HttpServletRequest request,
      AddToDepartmentsDTO addToDepartmentsDTO)
      throws DataBaseException, JsonProcessingException, NotFoundException, SecurityException {
    User admin = getDbUser(request, userDAO);
    User userToAdd = userDAO.findByIdAndCompany(addToDepartmentsDTO.getId(), admin.getCompany());
    for (UUID departmentId : addToDepartmentsDTO.getDepartments()) {
      Department department = departmentDao.findByIdAndCompany(departmentId, admin.getCompany());
      department.getUsers().add(userToAdd);
      userToAdd.getDepartments().add(department);
      setLastAction(AuditActions.ADD_USER_TO_DEPARTMENT);
      departmentDao.saveOrUpdate(department);
    }
    setLastAction(AuditActions.ADD_USER_TO_DEPARTMENT);
    userDAO.saveOrUpdate(userToAdd);
    return Response.status(200)
        .entity(new SuccessDTO("Successfully added the user to the departments!")).build();

  }

  @POST
  @Path(SEARCH_FOR_DEPARTMENT)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response searchForDepartment(@Context HttpServletRequest request,
      @FormParam("q") String query) throws NotFoundException, DataBaseException {
    User admin = getDbUser(request, userDAO);
    query = query.toUpperCase();
    if (query.length() > 1) {
      return Response.status(200).entity(
          mapDepartmentsForSearch(departmentDao.findMatchingDepartments(query, admin.getCompany())))
          .build();
    }
    return Response.status(200).entity(new ArrayList<>()).build();

  }
//
//// TODO
//  @POST
//  @Path(UPLOAD_CSR)
//  @Consumes({MediaType.MULTIPART_FORM_DATA})
//  public void uploadCsr(@Context HttpServletRequest request,
//      @MultipartForm final FileWithPassDTO fileDTO, @Suspended final AsyncResponse asyncResponse) {
//    X509Certificate clientCert = null;
//    executor.execute(new Runnable() {
//      @Override
//      public void run() {
//        X509Certificate clientCert = null;
//        try {
//          clientCert = trustStoreDAO.uploadCsr(fileDTO.getData());
//          if (clientCert != null) {
//            X500Name x500name = new JcaX509CertificateHolder(clientCert).getSubject();
//            RDN cn = x500name.getRDNs(BCStyle.CN)[0];
//            String userEmail = IETFUtils.valueToString(cn.getFirst().getValue());
//            User user = userDAO.findUserByEmailWithNull(userEmail);
//            String sslKey = TimeBasedOneTimePasswordUtil.generateBase32Secret();
//            user.setMutualSslOTPKey(sslKey);
//            userDAO.saveOrUpdate(user);
//          }
//        } catch (SecurityException | CertificateEncodingException | DataBaseException e) {
//          e.printStackTrace();
//          asyncResponse.cancel();
//        }
//        if (clientCert != null) {
//          FileDTO dto = new FileDTO();
//          try {
//            dto.setData(clientCert.getEncoded());
//          } catch (CertificateEncodingException e) {
//            e.printStackTrace();
//          }
//          asyncResponse.resume(dto);
//        } else {
//          asyncResponse
//              .resume(new ErrorDTO("Error generating certificate", "Can't generate certificate"));
//        }
//      }
//    });
//
//
//  }


  @POST
  @Path("/getCertificateEntries")
  public Response getCertificateEntries(@Context HttpServletRequest request, SecureString password)
      throws SecurityException {

    List<CertInTrustStoreDTO> entries = trustStoreDAO.getAllCertificatesForWeb(password);
    return Response.status(200).entity(entries).build();


  }

  @POST
  @Path("/revoke/{alias}")
  public Response revoke(@Context HttpServletRequest request, @PathParam("alias") String alias,
      SecureString password) throws SecurityException {

    trustStoreDAO.revokeCertificate(alias, password);
    List<CertInTrustStoreDTO> entries = trustStoreDAO.getAllCertificatesForWeb(password);
    return Response.status(200).entity(entries).build();


  }

  @POST
  @Path(INVITE_USERS)
  public Response inviteUsers(@Context HttpServletRequest request, MessageSendDTO messageDTO)
      throws NotFoundException, DataBaseException {
    log.info("IN REQUEST" + messageDTO);
    User admin = getDbUser(request, userDAO);
    CompanyInvite companyInvite = new CompanyInvite();
    companyInvite.setCompanyId(admin.getCompany().getId());
    companyInvite.setInvitedEmails(new HashSet<>(messageDTO.getEmailsTo()));
    companyInvite.setInviteCode(RandomStringUtils.random(8));
    companyInviteDAO.saveOrUpdate(companyInvite);
    executor.execute(new Runnable() {

      @Override
      public void run() {
        for (String to : messageDTO.getEmailsTo()) {
          emailService.sendAddCompanyUsersEmail(to, admin.getEmail(), messageDTO.getSubject(),
              messageDTO.getMessage(), companyInvite.getInviteCode());
        }
      }
    });

    return Response.status(200).entity(new SuccessDTO("SUCCESS")).build();
  }

  private List<SelectTagsSearchDTO> mapDepartmentsForSearch(List<Department> departments) {
    List<SelectTagsSearchDTO> dpts = new ArrayList<>();
    for (Department department : departments) {
      dpts.add(new SelectTagsSearchDTO(department));
    }
    return dpts;
  }


  private String getDepartmentsNames(Set<Department> departments) {
    StringBuilder buf = new StringBuilder();
    for (Department department : departments) {
      buf.append(department.getDepartmentName()).append(", ");
    }
    if (departments.size() > 0) {
      buf.delete(buf.length() - 2, buf.length());
    }
    return buf.toString();
  }

  private Set<DepartmentInfoDTO> mapDepartmentsToDTO(Set<Department> departments) {
    Set<DepartmentInfoDTO> dep = new HashSet<>();
    for (Department dept : departments) {
      dep.add(new DepartmentInfoDTO(dept));
    }
    return dep;
  }

  private Set<UserInDeptDTO> mapUsersToMembers(Set<User> users) {
    Set<UserInDeptDTO> usrs = new HashSet<>();
    for (User usr : users) {
      usrs.add(new UserInDeptDTO(usr));
    }
    return usrs;
  }
}


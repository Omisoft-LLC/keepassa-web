package com.omisoft.keepassa.endpoints;

import static com.omisoft.keepassa.constants.RestUrl.CONFIGURATION;
import static com.omisoft.keepassa.constants.RestUrl.INIT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.omisoft.keepassa.configuration.Configuration;
import com.omisoft.keepassa.configuration.FileConfigService;
import com.omisoft.keepassa.constants.Constants;
import com.omisoft.keepassa.constants.RestUrl;
import com.omisoft.keepassa.dao.UserDAO;
import com.omisoft.keepassa.dto.SetupDTO;
import com.omisoft.keepassa.dto.rest.ConfigurationDTO;
import com.omisoft.keepassa.dto.rest.ErrorDTO;
import com.omisoft.keepassa.entities.users.User;
import com.omisoft.server.common.exceptions.DataBaseException;
import io.swagger.annotations.Api;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * Endpoint for initializing default and constant properties and stuff. Created by leozhekov on
 * 11/2/16.
 */
@Slf4j
@Path(INIT)
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@Api(tags = {"init"}, value = INIT, description = "Init method, used to return constants with urls")
public class InitEndpoint {

  @Inject
  private ObjectMapper mapper;
  @Inject
  private UserDAO userDAO;

  private final Configuration config = FileConfigService.getInstance().getConfig();

  @GET
  @Path(CONFIGURATION)
  public Response sendConfig(@Context HttpServletRequest request) {
    ConfigurationDTO config = new ConfigurationDTO();
    config.setRestUrls(RestUrl.getAllUrls());
    return Response.status(200).entity(config).build();
  }

  @POST
  @Path("/firstSetup")
  public Response firstSetup(@Context HttpServletRequest request, SetupDTO setupDTO)
      throws DataBaseException {

    User foundUser = userDAO.findUserByEmailWithNull(setupDTO.getEmail());
    if (foundUser == null) {
      User user = new User();
      user.setEmail(setupDTO.getEmail());
      user = userDAO.saveOrUpdate(user);
      user.setClientPublicKey(setupDTO.getPublicKey());
      Configuration config = FileConfigService.getInstance().getConfig();
      config.setInit_done(Boolean.TRUE);
      Constants.MASTER_PASSWORD = setupDTO.getPassword();
      request.getServletContext().setAttribute(Constants.SETUP, Boolean.TRUE);
      FileConfigService.getInstance().saveConfig();
      return Response.status(200).build();
    }

    return Response.status(500).entity(new ErrorDTO("ERROR", "App not setup")).build();

  }

  @POST
  @Path("/is/setup")
  public Response isSetup(@Context HttpServletRequest request)
      throws DataBaseException {
    log.info(String.valueOf(config.getInit_done()));
    log.info(String.valueOf(request.getServletContext().getAttribute(Constants.SETUP) == null));
    if (!config.getInit_done()
        && request.getServletContext().getAttribute(Constants.SETUP) == null) {
      return Response.status(200).entity(Boolean.FALSE).build();
    }
    return Response.status(200).entity(Boolean.TRUE).build();
  }

//  @POST
//  @Path("/rebootSetup")
//  public Response rebootSetup(@Context HttpServletRequest request, RebootSetupDTO rebootSetupDTO)
//      throws com.omisoft.keepassa.exceptions.NotFoundException, JsonProcessingException {
//
//    User user = userDAO.findUserByEmailWithExc(rebootSetupDTO.getAdminEmail());
//    user.unlockKeystore();
//    SecureKeystore secureKeystore = user.getSecureKeystore();
//    log.info(mapper.writeValueAsString(rebootSetupDTO));
//    if (secureKeystore.open(rebootSetupDTO.getMasterPassword(), rebootSetupDTO.getAdminPassword(),
//        Constants.CONSTANT_PASSWORD, true)) {
//      Constants.MASTER_PASSWORD = rebootSetupDTO.getMasterPassword();
//      request.getServletContext().setAttribute("REBOOT", Boolean.TRUE);
//      return Response.status(200).build();
//    } else {
//      return Response.status(417)
//          .entity(new ErrorDTO(request.getRequestURI(), "Wrong master password or user password!"))
//          .build();
//    }
//
//  }


}


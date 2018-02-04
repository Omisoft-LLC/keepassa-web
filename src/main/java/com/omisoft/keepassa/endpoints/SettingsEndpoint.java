package com.omisoft.keepassa.endpoints;

import static com.omisoft.keepassa.constants.Constants.MUTUAL_SSL;
import static com.omisoft.keepassa.constants.RestUrl.LIST;
import static com.omisoft.keepassa.constants.RestUrl.SECURE;
import static com.omisoft.keepassa.constants.RestUrl.SETTINGS;
import static com.omisoft.keepassa.constants.RestUrl.UPDATE;

import com.omisoft.keepassa.constants.AuditActions;
import com.omisoft.keepassa.dao.SettingsDAO;
import com.omisoft.keepassa.dto.rest.SuccessDTO;
import com.omisoft.keepassa.entities.settings.Settings;
import com.omisoft.keepassa.exceptions.NotFoundException;
import com.omisoft.server.common.exceptions.DataBaseException;
import io.swagger.annotations.Api;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * Settings Endpoint. Used only in hosted environment
 * Created by dido on 15.03.17.
 */
@Slf4j
@Path(SECURE + SETTINGS)
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@Api(tags = {"settings"}, value = SECURE + SETTINGS, description = "Settings for hosted mode")
public class SettingsEndpoint implements BaseEndpoint {

  private final SettingsDAO settingsDAO;

  @Inject
  public SettingsEndpoint(SettingsDAO settingsDAO) {
    this.settingsDAO = settingsDAO;
  }

  @POST
  @Path(LIST)
  public Response getSettings(@Context HttpServletRequest request)
      throws NotFoundException, com.omisoft.server.common.exceptions.NotFoundException {

    return Response.status(200).entity(settingsDAO.findAll()).build();

  }

  @POST
  @Path(UPDATE)
  public Response updateSettings(@Context HttpServletRequest request, final List<Settings> settings)
      throws DataBaseException {
    setLastAction(AuditActions.UPDATE_SETTINGS);
    settingsDAO.saveAll(settings);
    return Response.status(200).entity(new SuccessDTO("Successfully updated the settings!"))
        .build();

  }

  @POST
  @Path("/certificateInfo")
  public Response certificateInfo(@Context HttpServletRequest request) throws NotFoundException {

    Settings setting = settingsDAO.findSettingByName(MUTUAL_SSL);
    return Response.status(200).entity(setting).build();

  }

}

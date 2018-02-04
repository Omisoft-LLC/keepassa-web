package com.omisoft.keepassa.endpoints;

import static com.omisoft.keepassa.constants.RestUrl.ADMIN;
import static com.omisoft.keepassa.constants.RestUrl.AUDIT;
import static com.omisoft.keepassa.constants.RestUrl.SECURE;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.omisoft.keepassa.constants.RestUrl;
import com.omisoft.keepassa.dao.HistoryDAO;
import com.omisoft.keepassa.dao.UserDAO;
import com.omisoft.keepassa.dto.rest.HistoryDTO;
import com.omisoft.keepassa.entities.history.HistoryData;
import com.omisoft.keepassa.entities.users.User;
import com.omisoft.server.common.exceptions.DataBaseException;
import com.omisoft.server.common.exceptions.NotFoundException;
import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * Audit endpoint
 * Created by dido on 27.01.17.
 */
@Singleton
@Slf4j
@Path(SECURE + ADMIN + AUDIT)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(tags = {"audit"}, value = SECURE + ADMIN + AUDIT, description = "Audit user actions")
public class AuditEndpoint implements BaseEndpoint {

  private final HistoryDAO historyDAO;
  private final UserDAO userDAO;

  @Inject
  public AuditEndpoint(HistoryDAO historyDAO, UserDAO userDAO) {
    this.userDAO = userDAO;
    this.historyDAO = historyDAO;
  }

  /**
   * Get history records for user
   *
   * @param user id
   * @return history list or 404
   */
  @POST
  @Path(RestUrl.GET + "/{userId}")
  public Response getHistoryForUser(@PathParam("userId") UUID id)
      throws DataBaseException, NotFoundException {
    User user;
      user = userDAO.findById(id);

      if (user != null) {
        List<HistoryData> revEntries = historyDAO.getHistorybyUsername(user.getEmail());
        List<HistoryDTO> historyDTOList = new ArrayList<>();
        for (HistoryData hData : revEntries) {
          historyDTOList.add(new HistoryDTO(hData));
        }
        return Response.ok().entity(historyDTOList).build();
      } else {
        return Response.status(404).build();
      }


  }
}

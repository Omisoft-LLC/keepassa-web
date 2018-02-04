package com.omisoft.keepassa.endpoints;

import static com.omisoft.keepassa.constants.Constants.USER_REDIS_DTO;
import static com.omisoft.keepassa.constants.RestUrl.GROUP;
import static com.omisoft.keepassa.constants.RestUrl.INVITE;
import static com.omisoft.keepassa.constants.RestUrl.MESSAGE;
import static com.omisoft.keepassa.constants.RestUrl.SECURE;
import static com.omisoft.keepassa.constants.RestUrl.USER;

import com.google.inject.Inject;
import com.omisoft.keepassa.dao.GroupDAO;
import com.omisoft.keepassa.dto.LoggedUserInfo;
import com.omisoft.keepassa.dto.rest.MessageSendDTO;
import com.omisoft.keepassa.dto.rest.SuccessDTO;
import com.omisoft.keepassa.entities.passwords.Group;
import com.omisoft.keepassa.entities.users.User;
import com.omisoft.keepassa.services.EmailSenderService;
import com.omisoft.server.common.exceptions.DataBaseException;
import com.omisoft.server.common.exceptions.NotFoundException;
import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by leozhekov on 1/5/17.
 */
@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path(SECURE + MESSAGE)
@Api(tags = {"passwords"}, value = SECURE + MESSAGE, description = "Messaging Endpoint")
public class MessagingEndpoint {

  private final EmailSenderService emailSenderService;
  private final GroupDAO groupDao;


  @Inject
  public MessagingEndpoint(EmailSenderService emailSenderService,
      GroupDAO groupDao) {
    this.emailSenderService = emailSenderService;
    this.groupDao = groupDao;
  }

  @Path(USER)
  @POST
  public Response sendUserMessage(@Context HttpServletRequest request,
      MessageSendDTO messageSendDTO) {

    LoggedUserInfo userFrom = (LoggedUserInfo) request.getAttribute(USER_REDIS_DTO);
    for (String email : messageSendDTO.getEmailsTo()) {
      emailSenderService.sendMessage(userFrom.getEmail(), email, messageSendDTO.getSubject(),
          messageSendDTO.getMessage());
    }
    return Response.status(200).entity(new SuccessDTO("Successfully sent the message!")).build();

  }

  @Path(GROUP + "/{groupId}/{to}")
  @POST
  public Response sendGroupMessage(@Context HttpServletRequest request,
      @PathParam("groupId") UUID groupId, @PathParam("to") String to,
      MessageSendDTO messageSendDTO)
      throws com.omisoft.keepassa.exceptions.NotFoundException, DataBaseException, NotFoundException {

    LoggedUserInfo userFrom = (LoggedUserInfo) request.getAttribute(USER_REDIS_DTO);
    Group group = groupDao.findById(groupId);

    switch (to) {
      case "all":
        List<String> emails = new ArrayList<>();
        for (User user : group.getUsers()) {
          emails.add(user.getEmail());
        }
        for (String email : emails) {
          emailSenderService.sendMessage(userFrom.getEmail(), email, messageSendDTO.getSubject(),
              messageSendDTO.getMessage());
        }
        break;
      case "group":
        emailSenderService.sendGroupMessage(userFrom.getEmail(), group.getGroupEmail(),
            messageSendDTO.getSubject(), messageSendDTO.getMessage());
        break;
      default:
        log.info("value -> " + to + " - default switch statement -- do nothing - findbugs patch");
    }
    return Response.status(200).entity(new SuccessDTO("Successfully sent the message!")).build();


  }

  @POST
  @Path(INVITE)
  public Response inviteUserToKeepassa(@Context HttpServletRequest request, List<String> emails) {

    LoggedUserInfo userFrom = (LoggedUserInfo) request.getAttribute(USER_REDIS_DTO);
    for (String email : emails) {
      emailSenderService.sendKeepassaInvitationEmail(userFrom.getEmail(), email);
    }
    return Response.status(200).entity(new SuccessDTO("Successfully sent the invitations!"))
        .build();

  }
}

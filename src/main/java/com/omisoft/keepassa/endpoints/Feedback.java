package com.omisoft.keepassa.endpoints;

import static com.omisoft.keepassa.constants.Constants.DEV_MODE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.omisoft.keepassa.authority.UserAuthority;
import com.omisoft.keepassa.configuration.FileConfigService;
import com.omisoft.keepassa.constants.Constants;
import com.omisoft.keepassa.constants.RestUrl;
import com.omisoft.keepassa.dto.LoggedUserInfo;
import com.omisoft.keepassa.dto.feedback.FeedbackDTO;
import com.omisoft.keepassa.dto.feedback.RedmineIssueDTO;
import com.omisoft.keepassa.dto.feedback.RedmineIssueImgDTO;
import com.omisoft.keepassa.dto.feedback.RedmineIssueImgResponseDTO;
import com.omisoft.keepassa.dto.feedback.RedmineIssueObjDTO;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import io.swagger.annotations.Api;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.ImageHtmlEmail;
import org.apache.commons.mail.resolver.DataSourceFileResolver;

/**
 * Feedback rest endpoint Created by dido on 10/19/16.
 */
@Path(RestUrl.FEEDBACK)
@Slf4j
@Api(tags = {"feedback"}, value = RestUrl.FEEDBACK, description = "Feedback to developers")

public class Feedback {

  private static final String EMAIL_HOST = "smtp.googlemail.com";
  private static final int EMAIL_PORT = 465;
  private static final String EMAIL_FROM_ERRORS = "support@omisoft.eu";
  private static final String SUBJECT = "Error from Keepassa";
  private static final String REDMINE_SUBJECT = "Feedback from Keepassa web";
  private static final String PASSWORD = "support123@1";
  private static final String TO = "devs@omisoft.eu";
  private static final String LOGO_URL = "https://keepassa.eu/images/keppassa_logo_128.png";
  private static final String BUGTRACKER_URL = "http://bugs.omisoft.eu";
  private static final String ISSUES_JSON = "/issues.json";
  private static final String UPLOADS_JSON = "/uploads.json";
  private static final String PROJECT_ID = "1"; // for keepassa project
  private static final String TRACKER_ID = "1"; // for issue tracker
  private static final String STATUS_OPEN = "open";
  private static final String STATUS_CLOSED = "closed";
  private static final String REDMINE_API_KEY = "36fb016d1936705ab22b6f2f3e6ed218a3e79c94";
  private static final String DATE_FORMAT = "dd/MM/yyyy - HH:mm";
  private static final String SPACER = "\n";
  private static Template template;

  static {
    Configuration conf = new Configuration(Configuration.VERSION_2_3_23);
    conf.setClassForTemplateLoading(Feedback.class, "/ftl");
    conf.setDefaultEncoding("UTF-8");
    conf.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    conf.setLogTemplateExceptions(false);
    try {
      template = conf.getTemplate("errorEmail.ftl");
    } catch (IOException e) {
      log.error("IO EXCEPTION", e);

    }
  }

  private final UserAuthority authority;
  private final ObjectMapper mapper;

  @Inject
  public Feedback(UserAuthority authority, ObjectMapper mapper) {
    this.authority = authority;
    this.mapper = mapper;

  }

  @GET
  @Path("/test")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response testFeedback() {
    try {
      FeedbackDTO feedbackDTO = new FeedbackDTO();
      feedbackDTO.setNote("This is a test");
      feedbackDTO.setImageUrl(
          LOGO_URL);
      return Response.status(200).entity(mapper.writeValueAsString(createNewIssue(feedbackDTO)))
          .build();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public Response postFeedback(@Context HttpServletRequest request,
      @FormParam("feedback") String body,
      @CookieParam(Constants.AUTHORIZATION_HEADER) Cookie cookie) {
    File tempFile = null;
    try {

      if (!FileConfigService.getInstance().getConfig().getMode().equals(DEV_MODE)) {
        LoggedUserInfo user = authority.getUser(cookie.getValue());
        FeedbackDTO feedbackDTO = mapper.readValue(body, FeedbackDTO.class);
        feedbackDTO.setBrowser(request.getHeader("User-Agent"));
        feedbackDTO.setIp(request.getRemoteAddr());
        feedbackDTO.setUserEmail(user.getEmail());
        createNewIssue(feedbackDTO);
        ImageHtmlEmail email = new ImageHtmlEmail();
        email.setCharset("UTF-8");
        email.setHostName(EMAIL_HOST);
        if (StringUtils.isNotBlank(feedbackDTO.getImageUrl())) {
          File tempDir = new File(System.getProperty("java.io.tmpdir"));
          tempFile = File.createTempFile(UUID.randomUUID().toString(), ".png", tempDir);
          FileOutputStream bos = new FileOutputStream(tempFile);
          bos.write(
              org.apache.commons.codec.binary.Base64.decodeBase64(
                  feedbackDTO.getImageUrl().replaceFirst("^data:image/[^;]*;base64,?", "")));
          bos.close();
          email.setDataSourceResolver(new DataSourceFileResolver(tempFile));
          feedbackDTO.setImageCid(email.embed(tempFile, "Screenshot"));
        }
        feedbackDTO.setDatePosted(new SimpleDateFormat(DATE_FORMAT).format(new Date()));
        email.setSmtpPort(EMAIL_PORT);
        email.setAuthenticator(new DefaultAuthenticator(EMAIL_FROM_ERRORS, PASSWORD));
        email.setSSLOnConnect(true);
        email.setFrom(EMAIL_FROM_ERRORS);
        email.setSubject(SUBJECT + " " + feedbackDTO.getSubject());
        HashMap<String, FeedbackDTO> data = new HashMap<>();
        data.put("data", feedbackDTO);
        StringWriter out = new StringWriter();
        template.process(data, out);
        email.setHtmlMsg(out.toString());
        email.addTo(TO);
        email.send();
      }

    } catch (Exception e) {
      log.error("ERROR SENDING EMAIL", e);
      return Response.serverError().build();
    } finally {

      if (tempFile != null) {
        tempFile.delete();
      }
    }
    return null;
  }


  private String createNewIssue(FeedbackDTO feedbackDTO) {
    try {
      // upload image
      RedmineIssueDTO redmineIssueDTO = new RedmineIssueDTO();
      if (StringUtils.isNotBlank(feedbackDTO.getImageUrl())) {
        String imageBase64Info =
            feedbackDTO.getImageUrl().replaceFirst("^data:image/[^;]*;base64,?", "");
        byte[] screenshot = Base64.decodeBase64(imageBase64Info);
        URL redmineUploadUrl = new URL(BUGTRACKER_URL + UPLOADS_JSON);
        URLConnection uploadImgCon = redmineUploadUrl.openConnection();
        uploadImgCon.setDoOutput(true);
        uploadImgCon.setRequestProperty("Content-Type", "application/octet-stream");
        uploadImgCon.setRequestProperty("X-Redmine-API-Key", REDMINE_API_KEY);
        uploadImgCon.setRequestProperty("Accept-Charset", "UTF-8");
        try (OutputStream output = uploadImgCon.getOutputStream()) {
          output.write(screenshot);
        }
        InputStream uploadIs = uploadImgCon.getInputStream();
        BufferedReader uploadRd = new BufferedReader(new InputStreamReader(uploadIs, "UTF-8"));
        StringBuilder uploadRes = new StringBuilder();
        String lineRsp;
        while ((lineRsp = uploadRd.readLine()) != null) {
          uploadRes.append(lineRsp);
          uploadRes.append('\r');
        }
        uploadRd.close();
        // parse response
        RedmineIssueImgResponseDTO redmineIssueImgResponseDTO =
            mapper.readValue(uploadRes.toString(), RedmineIssueImgResponseDTO.class);

        // add to list
        List<RedmineIssueImgDTO> redmineImgs = new ArrayList<>();
        RedmineIssueImgDTO image = redmineIssueImgResponseDTO.getUpload();
        image.setContent_type("image/png");
        image.setFilename("attachment.png");
        redmineImgs.add(image);
        // add to issue
        redmineIssueDTO.setUploads(redmineImgs);
      }
      // format issue
      String thisTime = new SimpleDateFormat(DATE_FORMAT).format(new Date());
      redmineIssueDTO
          .setSubject(REDMINE_SUBJECT + " - " + feedbackDTO.getSubject() + " - " + thisTime);
      String buf = "Feedback from: " + feedbackDTO.getUserEmail() + SPACER + feedbackDTO.getIp()
          + SPACER + feedbackDTO.getBrowser() + SPACER + "Resolution: " + feedbackDTO
          .getWindowWidth() + "x" + feedbackDTO.getWindowHeight() + SPACER + feedbackDTO.getNote();
      redmineIssueDTO.setDescription(buf);
      redmineIssueDTO.setProject_id(PROJECT_ID);
      redmineIssueDTO.setTracker_id(TRACKER_ID);
      redmineIssueDTO.setStatus_id(STATUS_OPEN);
      RedmineIssueObjDTO issue = new RedmineIssueObjDTO(redmineIssueDTO);
      String body = mapper.writeValueAsString(issue);
      URL redmineUrl = new URL(BUGTRACKER_URL + ISSUES_JSON);
      URLConnection con = redmineUrl.openConnection();

      // add request header
      con.setRequestProperty("X-Redmine-API-Key", REDMINE_API_KEY);
      con.setRequestProperty("Content-Type", "application/json");
      con.setRequestProperty("Accept-Charset", "UTF-8");
      con.setDoOutput(true);
      try (OutputStream output = con.getOutputStream()) {
        output.write(body.getBytes("UTF-8"));
      }

      InputStream is = con.getInputStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
      StringBuilder response = new StringBuilder();
      String line;
      while ((line = rd.readLine()) != null) {
        response.append(line);
        response.append('\r');
      }
      rd.close();

      return response.toString();
    } catch (Exception e) {
      log.error("GENERIC EXCEPTION", e);
      return null;
    }

  }
}

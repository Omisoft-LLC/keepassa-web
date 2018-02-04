package com.omisoft.keepassa.endpoints;

import static com.omisoft.keepassa.constants.Constants.AUTHORIZATION_HEADER;
import static com.omisoft.keepassa.constants.RestUrl.SECURE;

import com.omisoft.keepassa.authority.UserAuthority;
import com.omisoft.keepassa.constants.AuditActions;
import com.omisoft.keepassa.constants.RestUrl;
import com.omisoft.keepassa.dao.PasswordSafeDAO;
import com.omisoft.keepassa.dao.PasswordSafeKeyDAO;
import com.omisoft.keepassa.dao.UserDAO;
import com.omisoft.keepassa.dto.EncryptionDTO;
import com.omisoft.keepassa.dto.LoggedUserInfo;
import com.omisoft.keepassa.dto.rest.ImportDTO;
import com.omisoft.keepassa.dto.rest.SuccessDTO;
import com.omisoft.keepassa.entities.passwords.PasswordSafe;
import com.omisoft.keepassa.entities.passwords.PasswordSafeKey;
import com.omisoft.keepassa.entities.users.User;
import com.omisoft.keepassa.exceptions.SecurityException;
import com.omisoft.keepassa.structures.SecureKeystore;
import com.omisoft.keepassa.structures.SecureKeystore.KeyType;
import com.omisoft.keepassa.structures.SecureString;
import com.omisoft.server.common.exceptions.DataBaseException;
import io.swagger.annotations.Api;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

/**
 * Realize import functionality
 * Created by dido on 12.01.17.
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path(SECURE + RestUrl.IMPORT)
@Slf4j
@Api(tags = {"import"}, value = SECURE
    + RestUrl.IMPORT, description = "Import password safes from other systems")

public class ImportEndpoint implements BaseEndpoint {

  public static final String END_OF_LINE = "\\r?\\n";
  public static final String LASTPASS = "lastpass";
  public static final String DASHLINE_CSV = "dashline_csv";
  public static final String DASHLINE_XLS = "dashline_xls";
  public static final String CSV = "csv";
  private final PasswordSafeDAO passwordSafeDAO;
  private final UserDAO userDAO;
  private final UserAuthority userAuthority;
  private final PasswordSafeKeyDAO passwordSafeKeyDAO;

  @Inject
  public ImportEndpoint(PasswordSafeDAO passwordSafeDAO, UserDAO userDAO,
      UserAuthority userAuthority, PasswordSafeKeyDAO passwordSafeKeyDAO) {
    this.passwordSafeDAO = passwordSafeDAO;
    this.userDAO = userDAO;
    this.userAuthority = userAuthority;
    this.passwordSafeKeyDAO = passwordSafeKeyDAO;
  }

  @POST
  @Path(RestUrl.UPLOAD)
  @Consumes({MediaType.MULTIPART_FORM_DATA})
  public Response importFile(@Context HttpServletRequest request, @MultipartForm ImportDTO fileDTO)
      throws DataBaseException, SecurityException {
    String token = request.getHeader(AUTHORIZATION_HEADER);

    LoggedUserInfo loggedUserInfo = userAuthority.getUser(token);
    User user = userDAO.findUserByEmailWithNull(loggedUserInfo.getEmail());
    List<PasswordSafe> passwordSafesList = null;
    switch (fileDTO.getType()) {
      case LASTPASS: {
        passwordSafesList = parseLastPass(fileDTO.getData());
        break;
      }
      case DASHLINE_CSV: {
        passwordSafesList = parseDashLineCSV(fileDTO.getData());
        break;
      }
      case DASHLINE_XLS: {
        passwordSafesList = parseDashLineXls(fileDTO.getData());
        break;
      }
      case CSV: {
        passwordSafesList = parseCsv(fileDTO.getData());
        break;
      }


    }
    importPasswords(user, passwordSafesList);

    return Response.ok(new SuccessDTO("Success")).build();


  }

  /**
   * Parse internal csv format
   */
  private List<PasswordSafe> parseCsv(byte[] data) {
    List<PasswordSafe> passwordSafesList = new ArrayList<>();

    String stringData = new String(data, StandardCharsets.UTF_8);
    String[] lines = stringData.split(END_OF_LINE);
    if (lines.length > 1) {
      for (int i = 1; i < lines.length; i++) { // Skip first line as it holds the account email
        PasswordSafe passwordSafe = new PasswordSafe();
        String[] parsedLine = lines[i].split(",");
        passwordSafe.setName(parsedLine[0]);
        passwordSafe.setUrl(parsedLine[1]);
        passwordSafe.setAppId(parsedLine[2]);
        passwordSafe.setUsername(parsedLine[3]);
        passwordSafe.setDecodedPassword(new SecureString(parsedLine[4].toCharArray()));
        passwordSafe.setDescription(parsedLine[5]);
        passwordSafesList.add(passwordSafe);

      }
    }
    return passwordSafesList;

  }

  /**
   * Imports passwords in secure keystore
   */
  private void importPasswords(User user, List<PasswordSafe> passwordSafesList) throws SecurityException, DataBaseException {
    List<PasswordSafeKey> keys = new ArrayList<>();
    for (PasswordSafe p : passwordSafesList) {
      EncryptionDTO encryptionDTO = SecureKeystore.encryptPassword(p.getDecodedPassword());
      p.setPassword(encryptionDTO.getEncryptedMessage());

      p.getUsers().add(user);
      p.setCreator(user);
      user.getPasswordSafes().add(p);
      PasswordSafe passwordSafe = passwordSafeDAO.saveOrUpdate(p);
      PasswordSafeKey key = new PasswordSafeKey();
      key.setPasswordSafe(passwordSafe);
      key.setUser(user);
      key.setAesKey(SecureKeystore.encryptWithKey(encryptionDTO.getEncodedAESKey(),SecureKeystore.AES_MASTER_KEY, KeyType.AES));
      key.setTwofishKey(SecureKeystore.encryptWithKey(encryptionDTO.getEncodedAESKey(),SecureKeystore.TWOFISH_MASTER_KEY, KeyType.AES));
      key.setSerpentKey(SecureKeystore.encryptWithKey(encryptionDTO.getEncodedAESKey(),SecureKeystore.SERPENT_MASTER_KEY, KeyType.AES));
      key.setUser(user);
      passwordSafeKeyDAO.saveOrUpdate(key);



    }
    setLastAction(AuditActions.IMPORT_PASSWORDS);

    userDAO.saveOrUpdate(user);
  }

  /**
   * Parse dashline xls
   */
  private List<PasswordSafe> parseDashLineXls(byte[] data) {
    HSSFWorkbook wb = null;
    List<PasswordSafe> passwordSafesList = new ArrayList<>();
    try {
      wb = new HSSFWorkbook(new ByteArrayInputStream(data));
      HSSFSheet sheet = wb.getSheetAt(0);
      int rows = sheet.getPhysicalNumberOfRows();

      for (int r = 1; r < rows; r++) { // Skip first row
        HSSFRow row = sheet.getRow(r);
        PasswordSafe passwordSafe = new PasswordSafe();

        HSSFCell cell1 = row.getCell(0);
        if (StringUtils.isBlank(cell1.getStringCellValue())) {
          break;
        }
        passwordSafe.setName(row.getCell(0).getStringCellValue());
        passwordSafe.setUrl(row.getCell(1).getStringCellValue());
        passwordSafe.setUsername(row.getCell(2).getStringCellValue());
        passwordSafe.setDecodedPassword(
            new SecureString(row.getCell(3).getStringCellValue().toCharArray()));
        passwordSafe.setDescription(row.getCell(4).getStringCellValue());
        passwordSafesList.add(passwordSafe);
      }
    } catch (IOException e) {
      log.error("IO Exception", e);
    } finally {
      if (wb != null) {
        try {
          wb.close();
        } catch (IOException e) {
          log.error("IO Exception", e);
        }
      }
    }

    return passwordSafesList;

  }

  private List<PasswordSafe> parseDashLineCSV(byte[] data) {
    List<PasswordSafe> passwordSafesList = new ArrayList<>();

    String stringData = new String(data, StandardCharsets.UTF_8);
    String[] lines = stringData.split(END_OF_LINE);
    if (lines.length > 1) {
      for (int i = 1; i < lines.length; i++) { // Skip first line as it holds the account email
        log.info("LINE:" + lines[i]);

        PasswordSafe passwordSafe = new PasswordSafe();
        String[] parsedLine = lines[i].split(",");
        passwordSafe.setName(parsedLine[0].replaceAll("\"", ""));
        passwordSafe.setUrl(parsedLine[1].replaceAll("\"", ""));
        passwordSafe.setUsername(parsedLine[2].replaceAll("\"", ""));
        passwordSafe
            .setDecodedPassword(new SecureString(parsedLine[3].replaceAll("\"", "").toCharArray()));
        passwordSafe.setDescription(parsedLine[4].replaceAll("\"", ""));
        passwordSafesList.add(passwordSafe);

      }
    }
    return passwordSafesList;
  }

  /**
   * Parsing LastPass passwords
   */
  private List<PasswordSafe> parseLastPass(byte[] data) {
    List<PasswordSafe> passwordSafesList = new ArrayList<>();

    String stringData = new String(data, StandardCharsets.UTF_8);
    String[] lines = stringData.split(END_OF_LINE);
    if (lines.length > 1) {
      for (int i = 1; i < lines.length; i++) { // Skip first line as is descriptive
        log.info("LINE:" + lines[i]);
        PasswordSafe passwordSafe = new PasswordSafe();
        String[] parsedLine = lines[i].split(",");
        passwordSafe.setUrl(parsedLine[0]);
        passwordSafe.setUsername(parsedLine[1]);
        passwordSafe.setDecodedPassword(new SecureString(parsedLine[2].toCharArray()));
        passwordSafe.setName(parsedLine[4]);
        passwordSafesList.add(passwordSafe);
      }
    }
    return passwordSafesList;
  }
}

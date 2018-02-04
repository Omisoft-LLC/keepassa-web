package com.omisoft.keepassa.providers;

import com.omisoft.keepassa.dto.rest.ErrorDTO;
import com.omisoft.keepassa.exceptions.SecurityException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

/**
 * Mapper for Secure Keystore Exception
 * Created by dido on 16.03.17.
 */
@Provider
@Slf4j
public class SecurityExceptionProvider implements ExceptionMapper<SecurityException> {

  @Override
  public Response toResponse(SecurityException e) {
    log.error("Security Exception:", e);

    return Response.status(417).entity(new ErrorDTO("SECURITY ERROR", e.getMessage()))
        .build();
  }
}

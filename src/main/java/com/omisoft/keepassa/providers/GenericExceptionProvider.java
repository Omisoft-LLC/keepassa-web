package com.omisoft.keepassa.providers;

import com.omisoft.keepassa.dto.rest.ErrorDTO;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by dido on 16.03.17.
 */
@Slf4j
@Provider
public class GenericExceptionProvider implements ExceptionMapper<Exception> {

  @Override
  public Response toResponse(Exception e) {
    log.info("GENERIC ERROR");
    log.error("EXCEPTION:", e);
    return Response.status(500).entity(new ErrorDTO("SERVER ERROR", e.getMessage())).build();
  }
}

package com.omisoft.keepassa.providers;

import com.omisoft.keepassa.dto.rest.ErrorDTO;
import com.omisoft.keepassa.exceptions.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by dido on 16.03.17.
 */
@Provider
@Slf4j
public class NotFoundExceptionProvider implements ExceptionMapper<NotFoundException> {

  @Override
  public Response toResponse(NotFoundException e) {
    log.error("NOT FOUND EXCEPTION:", e);

    return Response.status(404).entity(new ErrorDTO("NOT FOUND", e.getMessage())).build();
  }
}

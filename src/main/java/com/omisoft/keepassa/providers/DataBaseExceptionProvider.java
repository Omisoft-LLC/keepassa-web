package com.omisoft.keepassa.providers;

import com.omisoft.keepassa.dto.rest.ErrorDTO;
import com.omisoft.server.common.exceptions.DataBaseException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by dido on 16.03.17.
 */
@Slf4j
@Provider
public class DataBaseExceptionProvider implements ExceptionMapper<DataBaseException> {

  @Override
  public Response toResponse(DataBaseException e) {
    log.info("DB ERROR");
    log.error("EXCEPTION", e);
    return Response.status(500).entity(new ErrorDTO("DATABASE ERROR", e.getMessage())).build();
  }
}

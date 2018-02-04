package com.omisoft.keepassa.structures;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.omisoft.keepassa.dto.rest.BasicUserDTO;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecureStringSerializer extends StdSerializer<SecureString> {

  public SecureStringSerializer() {
    this(null);
  }

  public SecureStringSerializer(Class<SecureString> t) {
    super(t);
  }

  public static void main(String[] args) {
    BasicUserDTO basicUserDTO = new BasicUserDTO();
    basicUserDTO.setEmail("aaa@aa.com");
    basicUserDTO.setPassword(new SecureString(new char[]{'a', 'b', 'c'}));
    try {
      String serialized = new ObjectMapper().writeValueAsString(basicUserDTO);
      log.info(serialized);
      BasicUserDTO test = new ObjectMapper().readValue(serialized, BasicUserDTO.class);
      log.info(test.toString());
      log.info(new ObjectMapper().writeValueAsString(test));
    } catch (IOException e) {
      log.error("GENERIC EXCEPTION", e);
    }

  }

  @Override
  public void serialize(SecureString value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException {
    jgen.writeStartArray();

    for (int i = 0; i < value.length(); i++) {
      jgen.writeString(value.toCharArray(), i, 1);
    }
    jgen.writeEndArray();
  }
}

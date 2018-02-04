package com.omisoft.keepassa.di;

import com.omisoft.keepassa.utils.InjectorHolder;
import javax.websocket.server.ServerEndpointConfig;

public class GuiceWebSocketConfigurator extends ServerEndpointConfig.Configurator {

  @Override
  public <T> T getEndpointInstance(Class<T> clazz)
      throws InstantiationException {
    return InjectorHolder.getInjector().getInstance(clazz);
  }
}
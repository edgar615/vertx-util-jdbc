package com.github.edgar615.util.vertx.jdbc;

import com.github.edgar615.util.exception.CustomErrorCode;
import com.github.edgar615.util.exception.SystemException;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;

/**
 * A MessageCodec for ServiceException
 *
 * @author <a href="mailto:oreilldf@gmail.com">Dan O'Reilly</a>
 */
public class SystemExceptionMessageCodec implements MessageCodec<SystemExceptionAdapter,
        SystemException> {

  @Override
  public void encodeToWire(Buffer buffer, SystemExceptionAdapter body) {
    JsonObject jsonObject = new JsonObject()
            .put("code", body.failureCode())
            .put("message", body.getMessage());
    body.systemException().getProperties().forEach((k, v) -> {
      jsonObject.put(k, v);
    });
    jsonObject.writeToBuffer(buffer);
  }

  @Override
  public SystemException decodeFromWire(int pos, Buffer buffer) {
    JsonObject jsonObject = buffer.toJsonObject();
    int code = jsonObject.getInteger("code", 999);
    String message = jsonObject.getString("message", "unkown");
    CustomErrorCode errorCode = CustomErrorCode.create(code, message);
    SystemException systemException = SystemException.create(errorCode);
    jsonObject.remove("code");
    jsonObject.remove("message");
    jsonObject.forEach(e -> systemException.set(e.getKey(), e.getValue()));
    return systemException;
  }

  @Override
  public SystemException transform(SystemExceptionAdapter systemExceptionAdapter) {
    return systemExceptionAdapter.systemException();
  }

  @Override
  public String name() {
    return "SystemException";
  }

  @Override
  public byte systemCodecID() {
    return -1;
  }

}


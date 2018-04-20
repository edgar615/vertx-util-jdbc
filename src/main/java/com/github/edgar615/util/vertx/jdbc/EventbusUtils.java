package com.github.edgar615.util.vertx.jdbc;

import com.github.edgar615.util.exception.DefaultErrorCode;
import com.github.edgar615.util.exception.SystemException;
import com.github.edgar615.util.validation.ValidationException;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Edgar on 2017/7/5.
 *
 * @author Edgar  Date 2017/7/5
 */
public class EventbusUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(EventbusUtils.class);

  private EventbusUtils() {
    throw new AssertionError("Not instantiable: " + EventbusUtils.class);
  }

  public static void onFailure(Message<JsonObject> received, Throwable throwable) {
    if (throwable instanceof SystemException) {
      SystemException ex = (SystemException) throwable;
      received.fail(ex.getErrorCode().getNumber(), ex.getMessage());
    } else if (throwable instanceof ValidationException) {
      received.fail(DefaultErrorCode.INVALID_ARGS.getNumber(),
                    DefaultErrorCode.INVALID_ARGS.getMessage());
    } else {
      received.fail(999, throwable.getMessage());
    }
  }

}

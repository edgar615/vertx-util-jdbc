package com.github.edgar615.util.vertx.jdbc.action;

import com.google.common.base.Strings;

import com.github.edgar615.util.db.SQLBindings;
import com.github.edgar615.util.exception.DefaultErrorCode;
import com.github.edgar615.util.exception.SystemException;
import com.github.edgar615.util.vertx.jdbc.JdbcAction;
import com.github.edgar615.util.vertx.jdbc.JdbcUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLConnection;

public class UpdateByIdAction implements JdbcAction<Integer> {
  private final String table;

  private final JsonObject data;

  private final Object id;

  private UpdateByIdAction(String table, JsonObject data, Object id) {
    this.table = table;
    this.data = data;
    this.id = id;
  }

  public static UpdateByIdAction create(String table, JsonObject data, Object id) {
    return new UpdateByIdAction(table, data, id);
  }

  @Override
  public void execute(SQLConnection connection, Handler<AsyncResult<Integer>> handler) {
    if (Strings.isNullOrEmpty(table)) {
      SystemException exception = SystemException.create(DefaultErrorCode.MISSING_ARGS)
              .setDetails("table");
      throw exception;
    }
    if (data == null || data.isEmpty()) {
      SystemException exception = SystemException.create(DefaultErrorCode.MISSING_ARGS)
              .setDetails("data");
      throw exception;
    }
    if (id == null) {
      SystemException exception = SystemException.create(DefaultErrorCode.MISSING_ARGS)
              .setDetails("id");
      throw exception;
    }
    SQLBindings sqlBindings = JdbcUtils.updateById(table, data, id);
    updateOrDelete(connection, sqlBindings, handler);
  }
}

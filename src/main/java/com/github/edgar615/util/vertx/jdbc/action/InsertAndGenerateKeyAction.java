package com.github.edgar615.util.vertx.jdbc.action;

import com.github.edgar615.util.db.SQLBindings;
import com.github.edgar615.util.exception.DefaultErrorCode;
import com.github.edgar615.util.exception.SystemException;
import com.github.edgar615.util.vertx.jdbc.JdbcAction;
import com.github.edgar615.util.vertx.jdbc.JdbcUtils;
import com.google.common.base.Strings;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;

public class InsertAndGenerateKeyAction implements JdbcAction<Integer> {
  private final String table;
  private final JsonObject data;

  public static InsertAndGenerateKeyAction create(String table, JsonObject data) {
    return new InsertAndGenerateKeyAction(table, data);
  }

  private InsertAndGenerateKeyAction(String table, JsonObject data) {
    this.table = table;
    this.data = data;
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
    SQLBindings sqlBindings = JdbcUtils.insert(table, data);
    log(sqlBindings);
    connection.updateWithParams(sqlBindings.sql(),
            new JsonArray(sqlBindings.bindings()), ar -> {
              if (ar.failed()) {
                handler.handle(Future.failedFuture(ar.cause()));
                return;
              }
              try {
                UpdateResult updateResult = ar.result();
                JsonArray jsonArray = updateResult.getKeys();
                Integer result = jsonArray.getInteger(0);
                handler.handle(Future.succeededFuture(result));
              } catch (Exception e) {
                handler.handle(Future.failedFuture(e));
              }
            });
  }
}

package com.github.edgar615.util.vertx.jdbc.action;

import com.github.edgar615.util.exception.DefaultErrorCode;
import com.github.edgar615.util.exception.SystemException;
import com.github.edgar615.util.search.Example;
import com.github.edgar615.util.vertx.jdbc.JdbcAction;
import com.google.common.base.Strings;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLConnection;

import java.util.List;

public class FindFirstByExampleAction implements JdbcAction<JsonObject> {
  private final String table;
  private final Example example;

  public static FindFirstByExampleAction create(String table, Example example) {
    return new FindFirstByExampleAction(table, example);
  }

  private FindFirstByExampleAction(String table, Example example) {
    this.table = table;
    this.example = example;
  }

  @Override
  public void execute(SQLConnection connection, Handler<AsyncResult<JsonObject>> handler) {
    if (Strings.isNullOrEmpty(table)) {
      SystemException exception = SystemException.create(DefaultErrorCode.MISSING_ARGS)
              .setDetails("table");
      throw exception;
    }
    if (example == null) {
      SystemException exception = SystemException.create(DefaultErrorCode.MISSING_ARGS)
              .setDetails("example");
      throw exception;
    }
    FindByExampleAction action = FindByExampleAction.create(table, example, 0, 1);
    action.execute(connection, ar -> {
      if (ar.failed()) {
        handler.handle(Future.failedFuture(ar.cause()));
        return;
      }
      List<JsonObject> results = ar.result();
      if (results.isEmpty()) {
        handler.handle(Future.succeededFuture(null));
        return;
      }
      handler.handle(Future.succeededFuture(results.get(0)));
    });

  }

}

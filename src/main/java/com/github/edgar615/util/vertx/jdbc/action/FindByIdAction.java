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
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLConnection;

import java.util.ArrayList;
import java.util.List;

public class FindByIdAction implements JdbcAction<JsonObject> {
  private final String table;
  private final List<String> fields;
  private final Object id;

  public static FindByIdAction create(String table, Object id) {
    return new FindByIdAction(table, id, new ArrayList<>());
  }

  public static FindByIdAction create(String table, Object id, List<String> fields) {
    return new FindByIdAction(table, id, fields);
  }

  private FindByIdAction(String table, Object id, List<String> fields) {
    this.table = table;
    if (fields == null) {
      this.fields = new ArrayList<>();
    } else {
      this.fields = fields;
    }
    this.id = id;
  }

  @Override
  public void execute(SQLConnection connection, Handler<AsyncResult<JsonObject>> handler) {
      if (Strings.isNullOrEmpty(table)) {
        SystemException exception = SystemException.create(DefaultErrorCode.MISSING_ARGS)
                .setDetails("table");
        throw exception;
      }
      if (id == null) {
        SystemException exception = SystemException.create(DefaultErrorCode.MISSING_ARGS)
                .setDetails("id");
        throw exception;
      }
      SQLBindings sqlBindings = JdbcUtils.findById(table, id, fields);
      query(connection, sqlBindings, ar -> {
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

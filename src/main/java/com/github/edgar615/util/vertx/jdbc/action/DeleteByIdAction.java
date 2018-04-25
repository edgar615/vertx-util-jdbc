package com.github.edgar615.util.vertx.jdbc.action;

import com.github.edgar615.util.db.SQLBindings;
import com.github.edgar615.util.exception.DefaultErrorCode;
import com.github.edgar615.util.exception.SystemException;
import com.github.edgar615.util.vertx.jdbc.JdbcAction;
import com.github.edgar615.util.vertx.jdbc.JdbcUtils;
import com.google.common.base.Strings;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.sql.SQLConnection;

public class DeleteByIdAction implements JdbcAction<Integer> {
  private final String table;
  private final Object id;

  public static DeleteByIdAction create(String table, Object id) {
    return new DeleteByIdAction(table, id);
  }

  private DeleteByIdAction(String table, Object id) {
    this.table = table;
    this.id = id;
  }

  @Override
  public void execute(SQLConnection connection, Handler<AsyncResult<Integer>> handler) {
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
      SQLBindings sqlBindings = JdbcUtils.deleteById(table, id);
      updateOrDelete(connection, sqlBindings, handler);
  }
}

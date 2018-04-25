package com.github.edgar615.util.vertx.jdbc.action;

import com.github.edgar615.util.base.StringUtils;
import com.github.edgar615.util.db.SQLBindings;
import com.github.edgar615.util.db.SqlBuilder;
import com.github.edgar615.util.exception.DefaultErrorCode;
import com.github.edgar615.util.exception.SystemException;
import com.github.edgar615.util.search.Example;
import com.github.edgar615.util.vertx.jdbc.JdbcAction;
import com.github.edgar615.util.vertx.jdbc.JdbcUtils;
import com.google.common.base.Strings;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.sql.SQLConnection;

import java.util.ArrayList;
import java.util.List;

public class DeleteByExampleAction implements JdbcAction<Integer> {
  private final String table;
  private final Example example;

  public static DeleteByExampleAction create(String table, Example example) {
    return new DeleteByExampleAction(table, example);
  }

  private DeleteByExampleAction(String table, Example example) {
    this.table = table;
    this.example = example;
  }

  @Override
  public void execute(SQLConnection connection, Handler<AsyncResult<Integer>> handler) {
    try {
      SQLBindings sqlBindings = createSqlBindings();
      updateOrDelete(connection, sqlBindings, handler);
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
    }

  }

  private SQLBindings createSqlBindings() {
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

    Example newExample = JdbcUtils.removeUndefinedField(table, example);
    String sql = "delete  from "
            + StringUtils.underscoreName(table);
    SQLBindings sqlBindings = SqlBuilder.whereSql(newExample.criteria());
    if (!newExample.criteria().isEmpty()) {
      sql += " where " + sqlBindings.sql();
    }
    List<Object> args = new ArrayList<>(sqlBindings.bindings());
    return SQLBindings.create(sql, args);
  }

}

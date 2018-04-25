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
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;

import java.util.ArrayList;
import java.util.List;

public class CountByExampleAction implements JdbcAction<Integer> {
  private final String table;
  private final Example example;

  public static CountByExampleAction create(String table, Example example) {
    return new CountByExampleAction(table, example);
  }

  private CountByExampleAction(String table, Example example) {
    this.table = table;
    this.example = example;
  }

  @Override
  public void execute(SQLConnection connection, Handler<AsyncResult<Integer>> handler) {
    SQLBindings sqlBindings = createSqlBindings();
    connection.queryWithParams(sqlBindings.sql(),
            new JsonArray(sqlBindings.bindings()), result -> {
              if (result.failed()) {
                handler.handle(Future.failedFuture(result.cause()));
                return;
              }
              try {
                ResultSet resultSet = result.result();
                int count = resultSet.getResults().get(0).getInteger(0);
                handler.handle(Future.succeededFuture(count));
              } catch (Exception e) {
                e.printStackTrace();
                handler.handle(Future.failedFuture(e));
              }
            });
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
    Example nextExample = JdbcUtils.removeUndefinedField(table, example);
    String sql = "select count(*) from "
            + StringUtils.underscoreName(table);
    SQLBindings sqlBindings = SqlBuilder.whereSql(nextExample.criteria());
    if (!nextExample.criteria().isEmpty()) {
      sql += " where " + sqlBindings.sql();
    } else {
      sql += "  " + sqlBindings.sql();
    }
    List<Object> args = new ArrayList<>(sqlBindings.bindings());
    return SQLBindings.create(sql, args);
  }
}

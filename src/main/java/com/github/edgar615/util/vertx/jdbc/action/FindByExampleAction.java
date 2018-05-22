package com.github.edgar615.util.vertx.jdbc.action;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import com.github.edgar615.util.base.StringUtils;
import com.github.edgar615.util.db.SQLBindings;
import com.github.edgar615.util.db.SqlBuilder;
import com.github.edgar615.util.exception.DefaultErrorCode;
import com.github.edgar615.util.exception.SystemException;
import com.github.edgar615.util.search.Example;
import com.github.edgar615.util.vertx.jdbc.JdbcAction;
import com.github.edgar615.util.vertx.jdbc.JdbcUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FindByExampleAction implements JdbcAction<List<JsonObject>> {
  private final String table;

  private final Example example;

  private final Integer start;

  private final Integer limit;

  private FindByExampleAction(String table, Example example, Integer start, Integer limit) {
    this.table = table;
    this.start = start;
    this.limit = limit;
    this.example = example;
  }

  public static FindByExampleAction create(String table, Example example) {
    return new FindByExampleAction(table, example, null, null);
  }

  public static FindByExampleAction create(String table, Example example, Integer start,
                                           Integer limit) {
    return new FindByExampleAction(table, example, start, limit);
  }

  @Override
  public void execute(SQLConnection connection, Handler<AsyncResult<List<JsonObject>>> handler) {
    SQLBindings sqlBindings = createSqlBindings();
    query(connection, sqlBindings, handler);
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
    String selectedField = "*";
    if (!newExample.fields().isEmpty()) {
      selectedField = Joiner.on(",")
              .join(newExample.fields().stream()
                            .map(f -> StringUtils.underscoreName(f))
                            .collect(Collectors.toList()));
    }

    String sql = "select " + selectedField + " from "
                 + StringUtils.underscoreName(table);
    SQLBindings sqlBindings = SqlBuilder.whereSql(newExample.criteria());
    if (!newExample.criteria().isEmpty()) {
      sql += " where " + sqlBindings.sql();
    } else {
      sql += "  " + sqlBindings.sql();
    }
    if (!newExample.orderBy().isEmpty()) {
      sql += SqlBuilder.orderSql(newExample.orderBy());
    }
    List<Object> args = new ArrayList<>(sqlBindings.bindings());
    if (limit != null) {
      sql += " limit ?";
      args.add(limit);
    }
    if (start != null) {
      if (limit == null) {
        sql += " limit ?";
        args.add(10);
      }
      sql += " offset ?";
      args.add(start);
    }
    return SQLBindings.create(sql, args);
  }
}

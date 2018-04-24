package com.github.edgar615.util.vertx.jdbc.handler;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import com.github.edgar615.util.base.StringUtils;
import com.github.edgar615.util.db.SQLBindings;
import com.github.edgar615.util.db.SqlBuilder;
import com.github.edgar615.util.exception.DefaultErrorCode;
import com.github.edgar615.util.exception.SystemException;
import com.github.edgar615.util.search.Example;
import com.github.edgar615.util.vertx.jdbc.JdbcUtils;
import com.github.edgar615.util.vertx.jdbc.SystemExceptionAdapter;
import com.github.edgar615.util.vertx.jdbc.dataobj.CountExample;
import com.github.edgar615.util.vertx.jdbc.dataobj.FindExample;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Edgar on 2018/4/20.
 *
 * @author Edgar  Date 2018/4/20
 */
public class CountByExampleHandler implements JdbcHandler {


  public void handle(AsyncSQLClient sqlClient,
                     CountExample countExample,
                     Handler<AsyncResult<Integer>> handler) {
    SQLBindings sqlBindings;
    try {
      sqlBindings = createSqlBindings(countExample);
    } catch (Exception e) {
      if (e instanceof SystemException) {
        handler.handle(Future.failedFuture(new SystemExceptionAdapter((SystemException) e)));
        return;
      }
      handler.handle(Future.failedFuture(e));
      return;
    }
    log(sqlBindings);
    sqlClient.getConnection(ar -> {
      if (ar.failed()) {
        handler.handle(Future.failedFuture(ar.cause()));
        return;
      }
      SQLConnection connection = ar.result();
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
    });
  }

  private SQLBindings createSqlBindings(CountExample countExample) {
    String table = countExample.getResource();
    if (Strings.isNullOrEmpty(table)) {
      SystemException exception = SystemException.create(DefaultErrorCode.MISSING_ARGS)
              .setDetails("resource");
      throw exception;
    }
    Example example = Example.create();
    example.addQuery(countExample.getQuery());
    example = JdbcUtils.removeUndefinedField(table, example);
    String sql = "select count(*) from "
                 + StringUtils.underscoreName(table);
    SQLBindings sqlBindings = SqlBuilder.whereSql(example.criteria());
    if (!example.criteria().isEmpty()) {
      sql += " where " + sqlBindings.sql();
    } else {
      sql += "  " + sqlBindings.sql();
    }
    List<Object> args = new ArrayList<>(sqlBindings.bindings());
    return SQLBindings.create(sql, args);
  }
}

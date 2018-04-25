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
import com.github.edgar615.util.vertx.jdbc.dataobj.FindExample;
import com.github.edgar615.util.vertx.jdbc.JdbcUtils;
import com.github.edgar615.util.vertx.jdbc.SystemExceptionAdapter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.SQLConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Edgar on 2018/4/20.
 *
 * @author Edgar  Date 2018/4/20
 */
public class FindByExampleHandler implements JdbcHandler {

  public void handle2(SQLConnection connection,
                     FindExample findExample,
                     Handler<AsyncResult<List<JsonObject>>> handler) {
    SQLBindings sqlBindings;
    try {
      sqlBindings = createSqlBindings(findExample);
    } catch (Exception e) {
      if (e instanceof SystemException) {
        handler.handle(Future.failedFuture(new SystemExceptionAdapter((SystemException) e)));
        return;
      }
      handler.handle(Future.failedFuture(e));
      return;
    }
    queryInConn(connection, sqlBindings, handler);
  }

  public void handle(AsyncSQLClient sqlClient,
                     FindExample findExample,
                     Handler<AsyncResult<List<JsonObject>>> handler) {
    SQLBindings sqlBindings;
    try {
      sqlBindings = createSqlBindings(findExample);
    } catch (Exception e) {
      if (e instanceof SystemException) {
        handler.handle(Future.failedFuture(new SystemExceptionAdapter((SystemException) e)));
        return;
      }
      handler.handle(Future.failedFuture(e));
      return;
    }
    query(sqlClient, sqlBindings, handler);
  }

  private SQLBindings createSqlBindings(FindExample findExample) {
    String table = findExample.getResource();
    if (Strings.isNullOrEmpty(table)) {
      SystemException exception = SystemException.create(DefaultErrorCode.MISSING_ARGS)
              .setDetails("resource");
      throw exception;
    }
    Example example = Example.create();
    example.addQuery(findExample.getQuery());
    String fields = findExample.getFields();
    if (!Strings.isNullOrEmpty(fields)) {
      Splitter.on(",").omitEmptyStrings()
              .trimResults()
              .splitToList(fields)
              .forEach(f -> example.addField(f));
    }
    example.orderBy(findExample.getSorted());
    Example newExample = JdbcUtils.removeUndefinedField(table, example);
    String selectedField = "*";
    if (!fields.isEmpty()) {
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
    if (findExample.getLimit() != null) {
      sql += " limit ?";
      args.add(findExample.getLimit());
    }
    if (findExample.getStart() != null) {
      if (findExample.getLimit() == null) {
        sql += " limit ?";
        args.add(10);
      }
      sql += " offset ?";
      args.add(findExample.getStart());
    }
    return SQLBindings.create(sql, args);
  }
}

package com.github.edgar615.util.vertx.jdbc.handler;

import com.google.common.base.Strings;

import com.github.edgar615.util.base.StringUtils;
import com.github.edgar615.util.db.SQLBindings;
import com.github.edgar615.util.db.SqlBuilder;
import com.github.edgar615.util.exception.DefaultErrorCode;
import com.github.edgar615.util.exception.SystemException;
import com.github.edgar615.util.search.Example;
import com.github.edgar615.util.vertx.jdbc.dataobj.DeleteById;
import com.github.edgar615.util.vertx.jdbc.dataobj.DeleteExample;
import com.github.edgar615.util.vertx.jdbc.JdbcUtils;
import com.github.edgar615.util.vertx.jdbc.SystemExceptionAdapter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.SQLConnection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edgar on 2018/4/20.
 *
 * @author Edgar  Date 2018/4/20
 */
public class DeleteByExampleHandler implements JdbcHandler {

  public void handle2(SQLConnection connection, DeleteExample deleteExample,
                      Handler<AsyncResult<Integer>> handler) {
    SQLBindings sqlBindings;
    try {
      sqlBindings = createSqlBindings(deleteExample);
    } catch (Exception e) {
      if (e instanceof SystemException) {
        handler.handle(Future.failedFuture(new SystemExceptionAdapter((SystemException) e)));
        return;
      }
      handler.handle(Future.failedFuture(e));
      return;
    }
    updateOrDelete(connection, sqlBindings, handler);
  }

  public void handle(AsyncSQLClient sqlClient,
                     DeleteExample deleteExample,
                     Handler<AsyncResult<Integer>> handler) {
    SQLBindings sqlBindings;
    try {
      sqlBindings = createSqlBindings(deleteExample);
    } catch (Exception e) {
      if (e instanceof SystemException) {
        handler.handle(Future.failedFuture(new SystemExceptionAdapter((SystemException) e)));
        return;
      }
      handler.handle(Future.failedFuture(e));
      return;
    }
    updateOrDelete(sqlClient, sqlBindings, handler);
  }

  private SQLBindings createSqlBindings(DeleteExample deleteExample) {
    String table = deleteExample.getResource();
    if (Strings.isNullOrEmpty(table)) {
      SystemException exception = SystemException.create(DefaultErrorCode.MISSING_ARGS)
              .setDetails("resource");
      throw exception;
    }
    Example example = Example.create();
    example.addQuery(deleteExample.getQuery());
    example = JdbcUtils.removeUndefinedField(table, example);
    String sql = "delete  from "
                 + StringUtils.underscoreName(table);
    SQLBindings sqlBindings = SqlBuilder.whereSql(example.criteria());
    if (!example.criteria().isEmpty()) {
      sql += " where " + sqlBindings.sql();
    }
    List<Object> args = new ArrayList<>(sqlBindings.bindings());
    return SQLBindings.create(sql, args);
  }
}

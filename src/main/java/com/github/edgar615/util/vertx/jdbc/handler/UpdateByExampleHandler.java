package com.github.edgar615.util.vertx.jdbc.handler;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import com.github.edgar615.util.base.StringUtils;
import com.github.edgar615.util.db.SQLBindings;
import com.github.edgar615.util.db.SqlBuilder;
import com.github.edgar615.util.exception.DefaultErrorCode;
import com.github.edgar615.util.exception.SystemException;
import com.github.edgar615.util.search.Example;
import com.github.edgar615.util.vertx.jdbc.JdbcUtils;
import com.github.edgar615.util.vertx.jdbc.SystemExceptionAdapter;
import com.github.edgar615.util.vertx.jdbc.dataobj.DeleteExample;
import com.github.edgar615.util.vertx.jdbc.dataobj.UpdateExample;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.SQLConnection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edgar on 2018/4/20.
 *
 * @author Edgar  Date 2018/4/20
 */
public class UpdateByExampleHandler implements JdbcHandler {

  public void handle2(SQLConnection connection, UpdateExample updateExample,
                      Handler<AsyncResult<Integer>> handler) {
    SQLBindings sqlBindings;
    try {
      sqlBindings = createSqlBindings(updateExample);
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

  public void handle(AsyncSQLClient sqlClient, UpdateExample updateExample,
                     Handler<AsyncResult<Integer>> handler) {
    SQLBindings sqlBindings;
    try {
      sqlBindings = createSqlBindings(updateExample);
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

  private SQLBindings createSqlBindings(UpdateExample updateExample) {
    String table = updateExample.getResource();
    if (Strings.isNullOrEmpty(table)) {
      SystemException exception = SystemException.create(DefaultErrorCode.MISSING_ARGS)
              .setDetails("resource");
      throw exception;
    }
    JsonObject data = updateExample.getData();
    if (data == null || data.isEmpty()) {
      SystemException exception = SystemException.create(DefaultErrorCode.MISSING_ARGS)
              .setDetails("data");
      throw exception;
    }
    Example example = Example.create();
    example.addQuery(updateExample.getQuery());
    example = JdbcUtils.removeUndefinedField(table, example);

    List<String> columns = new ArrayList<>();
    List<Object> params = new ArrayList<>();
    List<String> virtualFields = JdbcUtils.getTable(table).getVirtualFields();
    List<String> fields = JdbcUtils.getTable(table).getFields();
    data.forEach(e -> {
      String columnName = StringUtils.underscoreName(e.getKey());
      if (e.getValue() != null && !virtualFields.contains(columnName)
          && fields.contains(columnName)) {
        columns.add(columnName + " = ?");
        params.add(e.getValue());
      }
    });
    StringBuilder sql = new StringBuilder();
    sql.append("update ")
            .append(StringUtils.underscoreName(table))
            .append(" set ")
            .append(Joiner.on(",").join(columns));
    SQLBindings sqlBindings = SqlBuilder.whereSql(example.criteria());
    if (!example.criteria().isEmpty()) {
      sql.append(" where " + sqlBindings.sql());
    }
    params.addAll(sqlBindings.bindings());
    return SQLBindings.create(sql.toString(), new ArrayList<>(params));
  }
}

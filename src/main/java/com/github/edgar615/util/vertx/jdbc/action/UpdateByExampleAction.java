package com.github.edgar615.util.vertx.jdbc.action;

import com.github.edgar615.util.base.StringUtils;
import com.github.edgar615.util.db.SQLBindings;
import com.github.edgar615.util.db.SqlBuilder;
import com.github.edgar615.util.exception.DefaultErrorCode;
import com.github.edgar615.util.exception.SystemException;
import com.github.edgar615.util.search.Example;
import com.github.edgar615.util.vertx.jdbc.JdbcAction;
import com.github.edgar615.util.vertx.jdbc.JdbcUtils;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLConnection;

import java.util.ArrayList;
import java.util.List;

public class UpdateByExampleAction implements JdbcAction<Integer> {
  private final String table;
  private final JsonObject data;
  private final Example example;

  public static UpdateByExampleAction create(String table, JsonObject data, Example example) {
    return new UpdateByExampleAction(table, data, example);
  }

  private UpdateByExampleAction(String table, JsonObject data, Example example) {
    this.table = table;
    this.data = data;
    this.example = example;
  }

  @Override
  public void execute(SQLConnection connection, Handler<AsyncResult<Integer>> handler) {
    try {
      SQLBindings sqlBindings = createSqlBindings();
      updateOrDelete(connection, sqlBindings, handler);
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
      return;
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
    if (data == null || data.isEmpty()) {
      SystemException exception = SystemException.create(DefaultErrorCode.MISSING_ARGS)
              .setDetails("data");
      throw exception;
    }
    Example newExample = JdbcUtils.removeUndefinedField(table, example);

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
    SQLBindings sqlBindings = SqlBuilder.whereSql(newExample.criteria());
    if (!newExample.criteria().isEmpty()) {
      sql.append(" where " + sqlBindings.sql());
    }
    params.addAll(sqlBindings.bindings());
    return SQLBindings.create(sql.toString(), new ArrayList<>(params));
  }

}

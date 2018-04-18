package com.github.edgar615.util.vertx.jdbc;

import com.google.common.base.Joiner;

import com.github.edgar615.util.base.MorePreconditions;
import com.github.edgar615.util.base.StringUtils;
import com.github.edgar615.util.db.Persistent;
import com.github.edgar615.util.db.SQLBindings;
import com.github.edgar615.util.db.SqlBuilder;
import com.github.edgar615.util.search.Example;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Edgar on 2017/8/8.
 *
 * @author Edgar  Date 2017/8/8
 */
class VertxJdbcImpl implements VertxJdbc {

  private final SQLConnection connection;

  VertxJdbcImpl(SQLConnection connection) {this.connection = connection;}

  @Override
  public <ID> void insert(Persistent<ID> persistent, Handler<AsyncResult<ID>> handler) {
    SQLBindings sqlBindings = SqlBuilder.insert(persistent);
    connection.updateWithParams(sqlBindings.sql(),
                                new JsonArray(sqlBindings.bindings()), result -> {
              if (result.failed()) {
                handler.handle(Future.failedFuture(result.cause()));
                return;
              }
              try {
                UpdateResult updateResult = result.result();
                JsonArray jsonArray = updateResult.getKeys();
                ID id = (ID) jsonArray.getValue(0);
                handler.handle(Future.succeededFuture(id));
              } catch (Exception e) {
                handler.handle(Future.failedFuture(e));
              }
            });
  }

  @Override
  public <ID, T extends Persistent<ID>> void deleteById(Class<T> elementType, ID id,
                                                        Handler<AsyncResult<Integer>> handler) {
    SQLBindings sqlBindings = SqlBuilder.deleteById(elementType, id);
    connection.updateWithParams(sqlBindings.sql(),
                                new JsonArray(sqlBindings.bindings()), result -> {
              if (result.failed()) {
                handler.handle(Future.failedFuture(result.cause()));
                return;
              }
              try {
                UpdateResult updateResult = result.result();
                handler.handle(Future.succeededFuture(updateResult.getUpdated()));
              } catch (Exception e) {
                handler.handle(Future.failedFuture(e));
              }
            });
  }

  @Override
  public <ID, T extends Persistent<ID>> void deleteByExample(Class<T> elementType,
                                                             Example example,
                                                             Handler<AsyncResult<Integer>>
                                                                     handler) {
    example = removeUndefinedField(elementType, example);

    SQLBindings sqlBindings = SqlBuilder.whereSql(example.criteria());
    String tableName = StringUtils.underscoreName(elementType.getSimpleName());
    String sql = "delete from "
                 + tableName;
    if (!example.criteria().isEmpty()) {
      sql += " where " + sqlBindings.sql();
    }
    connection.updateWithParams(sql,
                                new JsonArray(sqlBindings.bindings()), result -> {
              if (result.failed()) {
                handler.handle(Future.failedFuture(result.cause()));
                return;
              }
              try {
                UpdateResult updateResult = result.result();
                handler.handle(Future.succeededFuture(updateResult.getUpdated()));
              } catch (Exception e) {
                handler.handle(Future.failedFuture(e));
              }
            });
  }

  @Override
  public <ID, T extends Persistent<ID>> void updateById(Persistent<ID> persistent, ID id,
                                                        Handler<AsyncResult<Integer>> handler) {
    boolean noUpdated = persistent.toMap().values().stream()
            .allMatch(v -> v == null);
    if (noUpdated) {
      handler.handle(Future.succeededFuture(0));
    }
    SQLBindings sqlBindings = SqlBuilder.updateById(persistent, id);
    connection.updateWithParams(sqlBindings.sql(),
                                new JsonArray(sqlBindings.bindings()), result -> {
              if (result.failed()) {
                handler.handle(Future.failedFuture(result.cause()));
                return;
              }
              try {
                UpdateResult updateResult = result.result();
                handler.handle(Future.succeededFuture(updateResult.getUpdated()));
              } catch (Exception e) {
                handler.handle(Future.failedFuture(e));
              }
            });
  }

  @Override
  public <ID, T extends Persistent<ID>> void updateByExample(Persistent<ID> persistent,
                                                             Example example,
                                                             Handler<AsyncResult<Integer>>
                                                                     handler) {
    boolean noUpdated = persistent.toMap().values().stream()
            .allMatch(v -> v == null);
    if (noUpdated) {
      handler.handle(Future.succeededFuture(0));
    }
    //对example做一次清洗，将表中不存在的条件删除，避免频繁出现500错误
    example = example.removeUndefinedField(persistent.fields());
    Map<String, Object> map = persistent.toMap();
    List<String> columns = new ArrayList<>();
    List<Object> params = new ArrayList<>();
    List<String> virtualFields = persistent.virtualFields();
    map.forEach((k, v) -> {
      if (v != null && !virtualFields.contains(k)) {
        columns.add(StringUtils.underscoreName(k) + " = ?");
        params.add(v);
      }
    });
    if (columns.isEmpty()) {
      handler.handle(Future.succeededFuture(0));
    }
    MorePreconditions.checkNotEmpty(columns, "no update field");

    String tableName = StringUtils.underscoreName(persistent.getClass().getSimpleName());
    StringBuilder sql = new StringBuilder();
    sql.append("update ")
            .append(tableName)
            .append(" set ")
            .append(Joiner.on(",").join(columns));
    List<Object> args = new ArrayList<>(params);
    if (!example.criteria().isEmpty()) {
      SQLBindings sqlBindings = SqlBuilder.whereSql(example.criteria());
      sql.append(" where ")
              .append(sqlBindings.sql());
      args.addAll(sqlBindings.bindings());
    }
    connection.updateWithParams(sql.toString(),
                                new JsonArray(args), result -> {
              if (result.failed()) {
                handler.handle(Future.failedFuture(result.cause()));
                return;
              }
              try {
                UpdateResult updateResult = result.result();
                handler.handle(Future.succeededFuture(updateResult.getUpdated()));
              } catch (Exception e) {
                handler.handle(Future.failedFuture(e));
              }
            });
  }

  @Override
  public <ID, T extends Persistent<ID>> void setNullById(Class<T> elementType, List<String> fields,
                                                         ID id,
                                                         Handler<AsyncResult<Integer>> handler) {
    List<String> columns = removeUndefinedColumn(elementType, fields);
    if (columns.isEmpty()) {
      handler.handle(Future.succeededFuture(0));
    }
    SQLBindings sqlBindings = SqlBuilder.setNullById(elementType, columns, id);
    connection.updateWithParams(sqlBindings.sql(),
                                new JsonArray(sqlBindings.bindings()), result -> {
              if (result.failed()) {
                handler.handle(Future.failedFuture(result.cause()));
                return;
              }
              try {
                UpdateResult updateResult = result.result();
                handler.handle(Future.succeededFuture(updateResult.getUpdated()));
              } catch (Exception e) {
                handler.handle(Future.failedFuture(e));
              }
            });
  }

  @Override
  public <ID, T extends Persistent<ID>> void setNullByExample(Class<T> elementType,
                                                              List<String> fields,
                                                              Example example,
                                                              Handler<AsyncResult<Integer>>
                                                                      handler) {
    List<String> columns = removeUndefinedColumn(elementType, fields);
    if (columns.isEmpty()) {
      handler.handle(Future.succeededFuture(0));
    }
    List<String> updatedColumn = columns.stream()
            .map(c -> c + " = null")
            .collect(Collectors.toList());

    String tableName = StringUtils.underscoreName(elementType.getSimpleName());
    StringBuilder sql = new StringBuilder();
    sql.append("update ")
            .append(tableName)
            .append(" set ")
            .append(Joiner.on(",").join(updatedColumn));
    List<Object> args = new ArrayList<>();
    if (!example.criteria().isEmpty()) {
      SQLBindings sqlBindings = SqlBuilder.whereSql(example.criteria());
      sql.append(" where ")
              .append(sqlBindings.sql());
      args.addAll(sqlBindings.bindings());
    }
    connection.updateWithParams(sql.toString(),
                                new JsonArray(args), result -> {
              if (result.failed()) {
                handler.handle(Future.failedFuture(result.cause()));
                return;
              }
              try {
                UpdateResult updateResult = result.result();
                handler.handle(Future.succeededFuture(updateResult.getUpdated()));
              } catch (Exception e) {
                handler.handle(Future.failedFuture(e));
              }
            });
  }

  @Override
  public <ID, T extends Persistent<ID>> void findById(Class<T> elementType, ID id,
                                                      List<String> fields,
                                                      Function<JsonObject, T> function,
                                                      Handler<AsyncResult<T>> handler) {
    if (function == null) {
      handler.handle(Future.failedFuture("function is null"));
      return;
    }
    Persistent<ID> persistent = newDomain(elementType);
    fields.removeIf(f -> persistent.fields().contains(f));
    SQLBindings sqlBindings = SqlBuilder.findById(elementType, id, fields);
    connection.queryWithParams(sqlBindings.sql(),
                               new JsonArray(sqlBindings.bindings()), result -> {
              if (result.failed()) {
                handler.handle(Future.failedFuture(result.cause()));
                return;
              }
              try {
                ResultSet resultSet = result.result();
                List<JsonObject> results = resultSet.getRows();
                if (results.isEmpty()) {
                  handler.handle(Future.succeededFuture(null));
                  return;
                }
                List<T> data = results.stream()
                        .map(json -> function.apply(json))
                        .collect(Collectors.toList());
                handler.handle(Future.succeededFuture(data.get(0)));
              } catch (Exception e) {
                handler.handle(Future.failedFuture(e));
              }
            });
  }

  @Override
  public <ID, T extends Persistent<ID>> void findByExample(Class<T> elementType,
                                                           Example example,
                                                           Function<JsonObject, T> function,
                                                           Handler<AsyncResult<List<T>>> handler) {
    if (function == null) {
      handler.handle(Future.failedFuture("function is null"));
      return;
    }
    example = removeUndefinedField(elementType, example);
    SQLBindings sqlBindings = SqlBuilder.whereSql(example.criteria());
    String tableName = StringUtils.underscoreName(elementType.getSimpleName());
    String sql = "select *  from "
                 + tableName;
    if (!example.criteria().isEmpty()) {
      sql += " where " + sqlBindings.sql();
    } else {
      sql += "  " + sqlBindings.sql();
    }
    if (!example.orderBy().isEmpty()) {
      sql += SqlBuilder.orderSql(example.orderBy());
    }
    connection.queryWithParams(sql,
                               new JsonArray(sqlBindings.bindings()), result -> {
              if (result.failed()) {
                handler.handle(Future.failedFuture(result.cause()));
                return;
              }
              try {
                ResultSet resultSet = result.result();
                List<JsonObject> results = resultSet.getRows();
                List<T> data = results.stream()
                        .map(json -> function.apply(json))
                        .collect(Collectors.toList());
                handler.handle(Future.succeededFuture(data));
              } catch (Exception e) {
                handler.handle(Future.failedFuture(e));
              }
            });
  }

  @Override
  public <ID, T extends Persistent<ID>> void findByExample(Class<T> elementType, Example example,
                                                           int start, int limit,
                                                           Function<JsonObject, T> function,
                                                           Handler<AsyncResult<List<T>>> handler) {
    if (function == null) {
      handler.handle(Future.failedFuture("function is null"));
      return;
    }
    example = removeUndefinedField(elementType, example);
    SQLBindings sqlBindings = SqlBuilder.whereSql(example.criteria());
    String tableName = StringUtils.underscoreName(elementType.getSimpleName());
    String sql = "select *  from "
                 + tableName;
    if (!example.criteria().isEmpty()) {
      sql += " where " + sqlBindings.sql();
    } else {
      sql += "  " + sqlBindings.sql();
    }
    if (!example.orderBy().isEmpty()) {
      sql += SqlBuilder.orderSql(example.orderBy());
    }
    sql += " limit ?, ?";
    List<Object> args = new ArrayList<>(sqlBindings.bindings());
    args.add(start);
    args.add(limit);
    connection.queryWithParams(sql,
                               new JsonArray(args), result -> {
              if (result.failed()) {
                handler.handle(Future.failedFuture(result.cause()));
                return;
              }
              try {
                ResultSet resultSet = result.result();
                List<JsonObject> results = resultSet.getRows();
                List<T> data = results.stream()
                        .map(json -> function.apply(json))
                        .collect(Collectors.toList());
                handler.handle(Future.succeededFuture(data));
              } catch (Exception e) {
                handler.handle(Future.failedFuture(e));
              }
            });
  }

  @Override
  public <ID, T extends Persistent<ID>> void countByExample(Class<T> elementType,
                                                            Example example,
                                                            Handler<AsyncResult<Integer>> handler) {
    example = removeUndefinedField(elementType, example);
    SQLBindings sqlBindings = SqlBuilder.whereSql(example.criteria());
    String tableName = StringUtils.underscoreName(elementType.getSimpleName());
    String sql = "select count(*) from "
                 + tableName;
    if (!example.criteria().isEmpty()) {
      sql += " where " + sqlBindings.sql();
    } else {
      sql += "  " + sqlBindings.sql();
    }
    connection.queryWithParams(sql,
                               new JsonArray(sqlBindings.bindings()), result -> {
              if (result.failed()) {
                handler.handle(Future.failedFuture(result.cause()));
                return;
              }
              try {
                ResultSet resultSet = result.result();
                List<JsonArray> results = resultSet.getResults();
                int count = results.get(0).getInteger(0);
                handler.handle(Future.succeededFuture(count));
              } catch (Exception e) {
                handler.handle(Future.failedFuture(e));
              }
            });
  }

  private <ID, T extends Persistent<ID>> List<String> removeUndefinedColumn(Class<T> elementType,
                                                                            List<String> fields) {
    Persistent<ID> persistent = newDomain(elementType);
    List<String> domainColumns = persistent.fields();
    return fields.stream()
            .filter(f -> domainColumns.contains(f))
            .collect(Collectors.toList());
  }

  private <ID, T extends Persistent<ID>> Example removeUndefinedField(Class<T> elementType,
                                                                      Example example) {
    //对example做一次清洗，将表中不存在的条件删除，避免频繁出现500错误
    Persistent<ID> persistent = newDomain(elementType);
    example = example.removeUndefinedField(persistent.fields());
    return example;
  }

  private <ID> Persistent newDomain(Class<? extends Persistent<ID>> clazz) {
    try {
      return clazz.newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

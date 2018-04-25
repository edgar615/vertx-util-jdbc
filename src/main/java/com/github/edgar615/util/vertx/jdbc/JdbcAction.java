package com.github.edgar615.util.vertx.jdbc;

import com.github.edgar615.util.db.SQLBindings;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Edgar on 2018/4/25.
 *
 * @author Edgar  Date 2018/4/25
 */
public interface JdbcAction<T> {
  Logger LOGGER = LoggerFactory.getLogger(JdbcAction.class);

  default void log(SQLBindings sqlBindings) {
    LOGGER.info("sql:{}, args:{}", sqlBindings.sql(), sqlBindings.bindings());
  }

  default void updateOrDelete(SQLConnection connection,
                              SQLBindings sqlBindings,
                              Handler<AsyncResult<Integer>> handler) {
    log(sqlBindings);
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

  default void query(SQLConnection connection,
                     SQLBindings sqlBindings,
                     Handler<AsyncResult<List<JsonObject>>> handler) {
    log(sqlBindings);
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
                  handler.handle(Future.succeededFuture(results));
                  return;
                }
                List<JsonObject> newResults = results.stream()
                        .map(json -> JdbcUtils.removeNull(json))
                        .map(json -> JdbcUtils.lowCamelField(json))
                        .collect(Collectors.toList());
                handler.handle(Future.succeededFuture(newResults));
              } catch (Exception e) {
                e.printStackTrace();
                handler.handle(Future.failedFuture(e));
              }
            });
  }


  void execute(SQLConnection connection, Handler<AsyncResult<T>> handler);
}

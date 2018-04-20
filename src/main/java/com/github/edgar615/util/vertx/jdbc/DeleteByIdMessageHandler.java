package com.github.edgar615.util.vertx.jdbc;

import com.github.edgar615.util.db.SQLBindings;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;

/**
 * Created by Edgar on 2018/4/20.
 *
 * @author Edgar  Date 2018/4/20
 */
public class DeleteByIdMessageHandler implements JdbcMessageHandler {

  private static final String ADDRESS = "__com.github.edgar615.util.vertx.jdbc.delete_by_id";

  @Override
  public String address() {
    return ADDRESS;
  }

  @Override
  public void handle(AsyncSQLClient sqlClient, MultiMap headers, JsonObject body,
                     Handler<AsyncResult<JsonObject>> handler) {
    String table = null;
    Object id = null;
    SQLBindings sqlBindings = null;
    try {
      table = body.getString("table");
      id= body.getValue("id");
      sqlBindings = JdbcUtils.deleteById(table, id);
    } catch (Exception e) {
      e.printStackTrace();
      handler.handle(Future.failedFuture(e));
      return;
    }
    final SQLBindings finalSqlBindings = sqlBindings;
    sqlClient.getConnection(ar -> {
      if (ar.failed()) {
        handler.handle(Future.failedFuture(ar.cause()));
        return;
      }
      SQLConnection connection = ar.result();
      connection.updateWithParams(finalSqlBindings.sql(),
                                  new JsonArray(finalSqlBindings.bindings()), result -> {
                if (result.failed()) {
                  handler.handle(Future.failedFuture(result.cause()));
                  return;
                }
                try {
                  UpdateResult updateResult = result.result();
                  JsonObject jsonObject = new JsonObject()
                          .put("result", updateResult.getUpdated());
                  handler.handle(Future.succeededFuture(jsonObject));
                } catch (Exception e) {
                  handler.handle(Future.failedFuture(e));
                }
              });
    });
  }
}

package com.github.edgar615.util.vertx.jdbc.handler;

import com.github.edgar615.util.db.SQLBindings;
import com.github.edgar615.util.vertx.jdbc.JdbcUtils;
import com.github.edgar615.util.vertx.jdbc.dataobj.InsertData;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
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
public class InsertHandler implements JdbcHandler {

  public void handle(AsyncSQLClient sqlClient, InsertData insertData,
                     Handler<AsyncResult<JsonObject>> handler) {
    SQLBindings sqlBindings;
    try {
      sqlBindings = JdbcUtils.insert(insertData.getResource(), insertData.getData());
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
      return;
    }
    log(sqlBindings);
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
                  JsonArray jsonArray = updateResult.getKeys();
                  JsonObject jsonObject = new JsonObject()
                          .put("result", jsonArray.getValue(0));
                  handler.handle(Future.succeededFuture(jsonObject));
                } catch (Exception e) {
                  handler.handle(Future.failedFuture(e));
                }
              });
    });
  }
}

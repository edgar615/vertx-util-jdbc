package com.github.edgar615.util.vertx.jdbc;

import com.github.edgar615.util.db.Persistent;
import com.github.edgar615.util.db.SQLBindings;
import com.github.edgar615.util.db.SqlBuilder;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;

/**
 * Created by Edgar on 2018/4/18.
 *
 * @author Edgar  Date 2018/4/18
 */
public class Tx {

  private final AsyncSQLClient sqlClient;

  public Tx(AsyncSQLClient sqlClient) {this.sqlClient = sqlClient;}

  public void beginTx(Handler<AsyncResult<SQLConnection>> handler) {
    sqlClient.getConnection(ar -> {
        if (ar.failed()){
          handler.handle(Future.failedFuture(ar.cause()));
          return;
        }
      SQLConnection connection = ar.result();
      connection.setAutoCommit(false, commit -> {
        if (commit.failed()) {
          handler.handle(Future.failedFuture(commit.cause()));
          return;
        }
        handler.handle(Future.succeededFuture(connection));
      });
    });
  }
}

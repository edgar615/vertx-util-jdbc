package com.github.edgar615.util.vertx.jdbc.action;

import com.github.edgar615.util.exception.DefaultErrorCode;
import com.github.edgar615.util.exception.SystemException;
import com.github.edgar615.util.vertx.jdbc.JdbcAction;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLConnection;

import java.util.ArrayList;
import java.util.List;

public class BatchFindByIdAction implements JdbcAction<List<JsonObject>> {
  private final List<FindByIdAction> actions;

  public static BatchFindByIdAction create(List<FindByIdAction> actions) {
    return new BatchFindByIdAction(actions);
  }

  private BatchFindByIdAction(List<FindByIdAction> actions) {
    this.actions = actions;
  }

  @Override
  public void execute(SQLConnection connection, Handler<AsyncResult<List<JsonObject>>> handler) {
    if (actions == null) {
      SystemException exception = SystemException.create(DefaultErrorCode.MISSING_ARGS)
              .setDetails("actions");
      throw exception;
    }
    List<Future> futures = new ArrayList<Future>();
    for (FindByIdAction action : actions) {
      Future<JsonObject> deleteFuture = Future.future();
      futures.add(deleteFuture);
      action.execute(connection, ar -> {
        if (ar.failed()) {
          deleteFuture.fail(ar.cause());
          return;
        }
        JsonObject result = ar.result();
        deleteFuture.complete(result);
      });
    }
    CompositeFuture.all(futures)
            .setHandler(ar -> {
              if (ar.failed()) {
                handler.handle(Future.failedFuture(ar.cause()));
                return;
              }
              List<JsonObject> result = ar.result().list();
              handler.handle(Future.succeededFuture(result));
            });
  }
}

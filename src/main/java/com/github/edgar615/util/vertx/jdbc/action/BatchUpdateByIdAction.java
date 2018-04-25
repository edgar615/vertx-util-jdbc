package com.github.edgar615.util.vertx.jdbc.action;

import com.github.edgar615.util.exception.DefaultErrorCode;
import com.github.edgar615.util.exception.SystemException;
import com.github.edgar615.util.vertx.jdbc.JdbcAction;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.sql.SQLConnection;

import java.util.ArrayList;
import java.util.List;

public class BatchUpdateByIdAction implements JdbcAction<Integer> {
  private final List<UpdateByIdAction> actions;

  public static BatchUpdateByIdAction create(List<UpdateByIdAction> actions) {
    return new BatchUpdateByIdAction(actions);
  }

  private BatchUpdateByIdAction(List<UpdateByIdAction> actions) {
    this.actions = actions;
  }

  @Override
  public void execute(SQLConnection connection, Handler<AsyncResult<Integer>> handler) {
      if (actions == null) {
        SystemException exception = SystemException.create(DefaultErrorCode.MISSING_ARGS)
                .setDetails("actions");
        throw exception;
      }
    List<Future> futures = new ArrayList<Future>();
    for (UpdateByIdAction action : actions) {
      Future<Integer> deleteFuture = Future.future();
      futures.add(deleteFuture);
      action.execute(connection, ar -> {
        if (ar.failed()) {
          deleteFuture.fail(ar.cause());
          return;
        }
        Integer result = ar.result();
        deleteFuture.complete(result);
      });
    }
    CompositeFuture.all(futures)
            .setHandler(ar -> {
              if (ar.failed()) {
                handler.handle(Future.failedFuture(ar.cause()));
                return;
              }
              handler.handle(Future.succeededFuture(actions.size()));
            });
  }
}

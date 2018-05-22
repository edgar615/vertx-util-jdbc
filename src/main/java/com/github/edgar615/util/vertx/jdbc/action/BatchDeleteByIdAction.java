package com.github.edgar615.util.vertx.jdbc.action;

import com.google.common.base.Strings;

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

public class BatchDeleteByIdAction implements JdbcAction<Integer> {
  private final String table;

  private final List<Object> idList;

  private BatchDeleteByIdAction(String table, List<Object> idList) {
    this.table = table;
    this.idList = idList;
  }

  public static BatchDeleteByIdAction create(String table, List<Object> idList) {
    return new BatchDeleteByIdAction(table, idList);
  }

  @Override
  public void execute(SQLConnection connection, Handler<AsyncResult<Integer>> handler) {
    if (Strings.isNullOrEmpty(table)) {
      SystemException exception = SystemException.create(DefaultErrorCode.MISSING_ARGS)
              .setDetails("table");
      throw exception;
    }
    if (idList == null) {
      SystemException exception = SystemException.create(DefaultErrorCode.MISSING_ARGS)
              .setDetails("idList");
      throw exception;
    }

    List<Future> futures = new ArrayList<Future>();
    for (Object id : idList) {
      Future<Integer> deleteFuture = Future.future();
      futures.add(deleteFuture);
      DeleteByIdAction.create(table, id).execute(connection, ar -> {
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
              handler.handle(Future.succeededFuture(idList.size()));
            });
  }
}

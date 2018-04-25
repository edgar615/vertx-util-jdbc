package com.github.edgar615.util.vertx.jdbc.action;

import com.github.edgar615.util.exception.DefaultErrorCode;
import com.github.edgar615.util.exception.SystemException;
import com.github.edgar615.util.search.Example;
import com.github.edgar615.util.vertx.jdbc.JdbcAction;
import com.github.edgar615.util.vertx.jdbc.dataobj.VertxPagination;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLConnection;

import java.util.List;

public class PaginationAction implements JdbcAction<VertxPagination> {
  private final String table;
  private final Example example;

  private final Integer page;

  private final Integer pageSize;

  public static PaginationAction create(String table, Example example, Integer page, Integer pageSize) {
    return new PaginationAction(table, example, page, pageSize);
  }

  private PaginationAction(String table, Example example, Integer page, Integer pageSize) {
    this.table = table;
    if (page == null) {
      this.page = 1;
    } else {
      this.page = page;
    }
    if (pageSize == null) {
      this.pageSize = 10;
    } else {
      this.pageSize = pageSize;
    }
    this.example = example;
  }

  @Override
  public void execute(SQLConnection connection, Handler<AsyncResult<VertxPagination>> handler) {
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

    Future<Integer> countFuture = Future.future();
    CountByExampleAction.create(table, example).execute(connection, countFuture.completer());
    countFuture.compose(totalRecords -> {
      Future<VertxPagination> pageFuture = Future.future();
      if (totalRecords == 0) {
        VertxPagination pagination =
                VertxPagination.newInstance(1, pageSize, totalRecords, Lists.newArrayList());
        pageFuture.complete(pagination);
        return pageFuture;
      }
      int pageCount = totalRecords / pageSize;
      if (totalRecords > pageSize * pageCount) {
        pageCount++;
      }
      int offset = (page - 1) * pageSize;
      if (pageCount < page) {
        offset = (pageCount - 1) * pageSize;
      }
      FindByExampleAction action = FindByExampleAction.create(table, example, offset, pageSize);
      action.execute(connection, ar -> {
        if (ar.failed()) {
          pageFuture.fail(ar.cause());
          return;
        }
        List<JsonObject> records = ar.result();
        VertxPagination pagination =
                VertxPagination.newInstance(1, pageSize, totalRecords, records);
        pageFuture.complete(pagination);
      });
      return pageFuture;
    }).setHandler(handler);
  }
}

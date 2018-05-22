package com.github.edgar615.util.vertx.jdbc.action;

import com.google.common.base.Strings;

import com.github.edgar615.util.exception.DefaultErrorCode;
import com.github.edgar615.util.exception.SystemException;
import com.github.edgar615.util.search.Example;
import com.github.edgar615.util.vertx.jdbc.JdbcAction;
import com.github.edgar615.util.vertx.jdbc.dataobj.VertxPage;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLConnection;

import java.util.List;

public class PageAction implements JdbcAction<VertxPage> {
  private final String table;

  private final Example example;

  private final Integer start;

  private final Integer limit;

  private PageAction(String table, Example example, Integer start, Integer limit) {
    this.table = table;
    if (start == null) {
      this.start = 0;
    } else {
      this.start = start;
    }
    if (limit == null) {
      this.limit = 10;
    } else {
      this.limit = limit;
    }
    this.example = example;
  }

  public static PageAction create(String table, Example example, Integer start, Integer limit) {
    return new PageAction(table, example, start, limit);
  }

  @Override
  public void execute(SQLConnection connection, Handler<AsyncResult<VertxPage>> handler) {
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
    FindByExampleAction action = FindByExampleAction.create(table, example, start, limit);
    action.execute(connection, ar -> {
      if (ar.failed()) {
        handler.handle(Future.failedFuture(ar.cause()));
        return;
      }
      List<JsonObject> records = ar.result();
      //如果records的数量小于limit，说明已经没有记录，直接计算总数
      if (records.size() > 0 && records.size() < limit) {
        int total = start + records.size();
        handler.handle(Future.succeededFuture(VertxPage.newInstance(total, records)));
        return;
      }
      CountByExampleAction countByExampleAction = CountByExampleAction.create(table, example);
      countByExampleAction.execute(connection, car -> {
        if (car.failed()) {
          handler.handle(Future.failedFuture(car.cause()));
          return;
        }
        int totalRecords = car.result();
        handler.handle(Future.succeededFuture(VertxPage.newInstance(totalRecords, records)));
      });
    });
  }
}

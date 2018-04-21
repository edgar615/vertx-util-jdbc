package com.github.edgar615.util.vertx.jdbc.handler;

import com.github.edgar615.util.vertx.jdbc.dataobj.CountExample;
import com.github.edgar615.util.vertx.jdbc.dataobj.FindExample;
import com.github.edgar615.util.vertx.jdbc.dataobj.VertxPage;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;

import java.util.List;

/**
 * Created by Edgar on 2018/4/20.
 *
 * @author Edgar  Date 2018/4/20
 */
public class PageHandler implements JdbcHandler {

  public void handle(AsyncSQLClient sqlClient,
                     FindExample findExample,
                     Handler<AsyncResult<VertxPage>> handler) {
    Future<List<JsonObject>> future = Future.future();
    new FindByExampleHandler().handle(sqlClient, findExample, future.completer());
    Integer limit = findExample.getLimit();
    Integer start = findExample.getStart();
    if (start == null) {
      start = 0;
    }
    if (limit == null) {
      limit = 10;
    }
    final Integer finalLimit = limit;
    final Integer finalStart = start;
    future.compose(records -> {
      if (records.size() > 0 && records.size() < finalLimit) {
        int total = finalStart + records.size();
        Future<VertxPage> pageFuture = Future.future();
        pageFuture.complete(VertxPage.newInstance(total, records));
        return pageFuture;
      }
      CountExample countExample = new CountExample();
      countExample.setResource(findExample.getResource());
      countExample.setQuery(findExample.getQuery());
      Future<VertxPage> pageFuture = Future.future();
      new CountByExampleHandler().handle(sqlClient, countExample, ar -> {
        if (ar.failed()) {
          pageFuture.fail(ar.cause());
        }
        int totalRecords = ar.result();
        pageFuture.complete(VertxPage.newInstance(totalRecords, records));
      });
      return pageFuture;
    }).setHandler(handler);

  }

}

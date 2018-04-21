package com.github.edgar615.util.vertx.jdbc.handler;

import com.google.common.collect.Lists;

import com.github.edgar615.util.vertx.jdbc.dataobj.CountExample;
import com.github.edgar615.util.vertx.jdbc.dataobj.FindExample;
import com.github.edgar615.util.vertx.jdbc.dataobj.PaginationExample;
import com.github.edgar615.util.vertx.jdbc.dataobj.VertxPagination;
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
public class PaginationHandler implements JdbcHandler {

  public void handle(AsyncSQLClient sqlClient,
                     PaginationExample paginationExample,
                     Handler<AsyncResult<VertxPagination>> handler) {
    CountExample countExample = new CountExample();
    countExample.setResource(paginationExample.getResource());
    countExample.setQuery(paginationExample.getQuery());
    Future<Integer> countFuture = Future.future();
    int pageSize = paginationExample.getPageSize();
    int page = paginationExample.getPage();
    new CountByExampleHandler().handle(sqlClient, countExample, countFuture.completer());
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
      FindExample findExample = new FindExample();
      findExample.setQuery(paginationExample.getQuery());
      findExample.setResource(paginationExample.getResource());
      findExample.setFields(paginationExample.getFields());
      findExample.setLimit(pageSize);
      findExample.setStart(offset);
      findExample.setSorted(paginationExample.getSorted());
      new FindByExampleHandler().handle(sqlClient, findExample, ar -> {
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

package com.github.edgar615.util.vertx.jdbc;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import com.github.edgar615.util.exception.SystemException;
import com.github.edgar615.util.search.Example;
import com.github.edgar615.util.vertx.jdbc.action.*;
import com.github.edgar615.util.vertx.jdbc.dataobj.*;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Edgar on 2018/4/21.
 *
 * @author Edgar  Date 2018/4/21
 */
public class PersistentServiceImpl implements PersistentService {

  private final AsyncSQLClient sqlClient;

  public PersistentServiceImpl(AsyncSQLClient sqlClient) {
    this.sqlClient = sqlClient;
  }

  @Override
  public void insertAndGenerateKey(InsertData insertData, Handler<AsyncResult<Integer>> handler) {
    String table = insertData.getResource();
    JsonObject data = insertData.getData();
    JdbcTask.create(sqlClient).execute("result", InsertAndGenerateKeyAction.create(table, data))
            .done(ctx -> (Integer) ctx.get("result")).setHandler(wrapHandler(handler));
  }

  @Override
  public void insert(InsertData insertData, Handler<AsyncResult<Void>> handler) {
    String table = insertData.getResource();
    JsonObject data = insertData.getData();
    InsertAction insertAction = InsertAction.create(table, data);
    Future<Map<String, Object>> future = JdbcTask.create(sqlClient).execute(insertAction)
            .done(ctx -> ctx);
    future.setHandler(ar -> {
      if (ar.failed()) {
        if (ar.cause() instanceof SystemException) {
          SystemException exception = (SystemException) ar.cause();
          handler.handle(Future.failedFuture(new SystemExceptionAdapter(exception)));
        } else {
          handler.handle(Future.failedFuture(ar.cause()));
        }
      } else {
        handler.handle(Future.succeededFuture());
      }
    });
  }

  @Override
  public void deleteById(DeleteById deleteById,
                         Handler<AsyncResult<Integer>> handler) {
    String table = deleteById.getResource();
    Object id = deleteById.getId();
    JdbcTask.create(sqlClient).execute("result", DeleteByIdAction.create(table, id))
            .done(ctx -> (Integer) ctx.get("result")).setHandler(wrapHandler(handler));
  }

  @Override
  public void updateById(UpdateById updateById,
                         Handler<AsyncResult<Integer>> handler) {
    String table = updateById.getResource();
    Object id = updateById.getId();
    JsonObject data = updateById.getData();
    JdbcTask.create(sqlClient).execute("result", UpdateByIdAction.create(table, data, id))
            .done(ctx -> (Integer) ctx.get("result")).setHandler(wrapHandler(handler));
  }

  @Override
  public void findById(FindById findById,
                       Handler<AsyncResult<JsonObject>> handler) {
    String table = findById.getResource();
    Object id = findById.getId();
    List<String> fields;
    if (Strings.isNullOrEmpty(findById.getFields())) {
      fields = new ArrayList<>();
    } else {
      fields = Splitter.on(",").omitEmptyStrings()
              .trimResults().splitToList(findById.getFields());
    }
    JdbcTask.create(sqlClient).execute("result", FindByIdAction.create(table, id, fields))
            .done(ctx -> (JsonObject) ctx.get("result")).setHandler(wrapHandler(handler));
  }

  @Override
  public void findByExample(FindExample findexample,
                            Handler<AsyncResult<List<JsonObject>>> handler) {
    String table = findexample.getResource();
    Example example = Example.create()
            .addQuery(findexample.getQuery())
            .orderBy(findexample.getSorted());
    if (!Strings.isNullOrEmpty(findexample.getFields())) {
      List<String> fields = Splitter.on(",").omitEmptyStrings()
              .trimResults().splitToList(findexample.getFields());
      example.addFields(fields);
    }
    FindByExampleAction action = FindByExampleAction
            .create(table, example, findexample.getStart(), findexample.getLimit());
    JdbcTask.create(sqlClient).execute("result", action)
            .done(ctx -> (List<JsonObject>) ctx.get("result")).setHandler(wrapHandler(handler));
  }

  @Override
  public void deleteByExample(DeleteExample deleteExample, Handler<AsyncResult<Integer>> handler) {
    String table = deleteExample.getResource();
    Example example = Example.create()
            .addQuery(deleteExample.getQuery());
    DeleteByIdAction action = DeleteByIdAction.create(table, example);
    JdbcTask.create(sqlClient).execute("result", action)
            .done(ctx -> (Integer) ctx.get("result")).setHandler(wrapHandler(handler));
  }

  @Override
  public void updateByExample(UpdateExample updateExample, Handler<AsyncResult<Integer>> handler) {
    String table = updateExample.getResource();
    Example example = Example.create()
            .addQuery(updateExample.getQuery());
    JsonObject data = updateExample.getData();
    UpdateByExampleAction action = UpdateByExampleAction.create(table, data, example);
    JdbcTask.create(sqlClient).execute("result", action)
            .done(ctx -> (Integer) ctx.get("result")).setHandler(wrapHandler(handler));
  }

  @Override
  public void countByExample(CountExample countExample, Handler<AsyncResult<Integer>> handler) {
    String table = countExample.getResource();
    Example example = Example.create()
            .addQuery(countExample.getQuery());
    JdbcTask.create(sqlClient).execute("result", CountByExampleAction.create(table, example))
            .done(ctx -> (Integer) ctx.get("result")).setHandler(wrapHandler(handler));
  }

  @Override
  public void pagination(PaginationExample paginationExample,
                         Handler<AsyncResult<VertxPagination>> handler) {
    String table = paginationExample.getResource();
    Example example = Example.create()
            .addQuery(paginationExample.getQuery())
            .orderBy(paginationExample.getSorted());
    if (!Strings.isNullOrEmpty(paginationExample.getFields())) {
      List<String> fields = Splitter.on(",").omitEmptyStrings()
              .trimResults().splitToList(paginationExample.getFields());
      example.addFields(fields);
    }
    PaginationAction action = PaginationAction
            .create(table, example, paginationExample.getPage(), paginationExample.getPageSize());
    JdbcTask.create(sqlClient).execute("result", action)
            .done(ctx -> (VertxPagination) ctx.get("result")).setHandler(wrapHandler(handler));
  }

  @Override
  public void page(FindExample findExample, Handler<AsyncResult<VertxPage>> handler) {
    String table = findExample.getResource();
    Example example = Example.create()
            .addQuery(findExample.getQuery())
            .orderBy(findExample.getSorted());
    if (!Strings.isNullOrEmpty(findExample.getFields())) {
      List<String> fields = Splitter.on(",").omitEmptyStrings()
              .trimResults().splitToList(findExample.getFields());
      example.addFields(fields);
    }
    PageAction action =
            PageAction.create(table, example, findExample.getStart(), findExample.getLimit());
    JdbcTask.create(sqlClient).execute("result", action)
            .done(ctx -> (VertxPage) ctx.get("result")).setHandler(wrapHandler(handler));
  }

  @Override
  public void close() {

  }

  private <T> Handler<AsyncResult<T>> wrapHandler(Handler<AsyncResult<T>> handler) {
    return ar -> {
      if (ar.failed()) {
        if (ar.cause() instanceof SystemException) {
          SystemException exception = (SystemException) ar.cause();
          handler.handle(Future.failedFuture(new SystemExceptionAdapter(exception)));
        } else {
          handler.handle(Future.failedFuture(ar.cause()));
        }
      } else {
        handler.handle(Future.succeededFuture(ar.result()));
      }
    };
  }
}

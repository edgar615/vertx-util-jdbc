package com.github.edgar615.util.vertx.jdbc;

import com.github.edgar615.util.vertx.jdbc.dataobj.*;
import com.github.edgar615.util.vertx.jdbc.handler.*;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;

import java.util.List;

/**
 * Created by Edgar on 2018/4/21.
 *
 * @author Edgar  Date 2018/4/21
 */
public class PersistentServiceImpl implements PersistentService {

  private final AsyncSQLClient sqlClient;

  public PersistentServiceImpl(AsyncSQLClient sqlClient) {this.sqlClient = sqlClient;}

  @Override
  public void insert(InsertData insertData, Handler<AsyncResult<JsonObject>> handler) {
    new InsertHandler().handle(sqlClient, insertData, handler);
  }

  @Override
  public void deleteById(DeleteById deleteById,
                         Handler<AsyncResult<Integer>> handler) {
    new DeleteByIdHandler().handle(sqlClient, deleteById, handler);
  }

  @Override
  public void updateById(UpdateById updateById,
                         Handler<AsyncResult<Integer>> handler) {
    new UpdateByIdHandler().handle(sqlClient, updateById, handler);
  }

  @Override
  public void findById(FindById findById,
                       Handler<AsyncResult<JsonObject>> handler) {
    new FindByIdHandler().handle(sqlClient, findById, handler);
  }

  @Override
  public void findByExample(FindExample example,
                            Handler<AsyncResult<List<JsonObject>>> handler) {
    new FindByExampleHandler().handle(sqlClient, example, handler);
  }

  @Override
  public void deleteByExample(DeleteExample example, Handler<AsyncResult<Integer>> handler) {
    new DeleteByExampleHandler().handle(sqlClient, example, handler);
  }

  @Override
  public void updateByExample(UpdateExample example, Handler<AsyncResult<Integer>> handler) {
    new UpdateByExampleHandler().handle(sqlClient, example, handler);
  }

  @Override
  public void countByExample(CountExample example, Handler<AsyncResult<Integer>> handler) {
    new CountByExampleHandler().handle(sqlClient, example, handler);
  }

  @Override
  public void pagination(PaginationExample example, Handler<AsyncResult<VertxPagination>> handler) {
    new PaginationHandler().handle(sqlClient, example, handler);
  }

  @Override
  public void page(FindExample example, Handler<AsyncResult<VertxPage>> handler) {
    new PageHandler().handle(sqlClient, example, handler);
  }


  @Override
  public void close() {

  }
}

package com.github.edgar615.util.vertx.jdbc;

import com.github.edgar615.util.vertx.jdbc.dataobj.*;
import io.vertx.codegen.annotations.ProxyClose;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by Edgar on 2018/4/21.
 *
 * @author Edgar  Date 2018/4/21
 */
@ProxyGen
@VertxGen
public interface PersistentService {

  void insertAndGenerateKey(InsertData insertData, Handler<AsyncResult<Integer>> handler);

  void insert(InsertData insertData, Handler<AsyncResult<Void>> handler);

  void deleteById(DeleteById deleteById, Handler<AsyncResult<Integer>> handler);

  void updateById(UpdateById updateById, Handler<AsyncResult<Integer>> handler);

  void findById(FindById findById,
                Handler<AsyncResult<JsonObject>> handler);

  void findByExample(FindExample example,
                     Handler<AsyncResult<List<JsonObject>>> handler);

  void deleteByExample(DeleteExample example,
                       Handler<AsyncResult<Integer>> handler);

  void updateByExample(UpdateExample example,
                       Handler<AsyncResult<Integer>> handler);

  void countByExample(CountExample example,
                      Handler<AsyncResult<Integer>> handler);

  void pagination(PaginationExample example,
                  Handler<AsyncResult<VertxPagination>> handler);

  void page(FindExample example,
            Handler<AsyncResult<VertxPage>> handler);

  @ProxyClose
  void close();
}

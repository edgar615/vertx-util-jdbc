package com.github.edgar615.util.vertx.jdbc.handler;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import com.github.edgar615.util.db.SQLBindings;
import com.github.edgar615.util.exception.SystemException;
import com.github.edgar615.util.vertx.jdbc.JdbcUtils;
import com.github.edgar615.util.vertx.jdbc.SystemExceptionAdapter;
import com.github.edgar615.util.vertx.jdbc.dataobj.FindById;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edgar on 2018/4/20.
 *
 * @author Edgar  Date 2018/4/20
 */
public class FindByIdHandler implements JdbcHandler {

  public void handle(AsyncSQLClient sqlClient, FindById findById,
                     Handler<AsyncResult<JsonObject>> handler) {
    String fields = findById.getFields();
    List<String> fieldList = new ArrayList<>();
    if (!Strings.isNullOrEmpty(fields)) {
      fieldList = Splitter.on(",").omitEmptyStrings()
              .trimResults().splitToList(fields);
    }
    SQLBindings sqlBindings;
    try {
      sqlBindings = JdbcUtils.findById(findById.getResource(), findById.getId(), fieldList);
    } catch (Exception e) {
      if (e instanceof SystemException) {
        handler.handle(Future.failedFuture(new SystemExceptionAdapter((SystemException) e)));
        return;
      }
      handler.handle(Future.failedFuture(e));
      return;
    }
    query(sqlClient, sqlBindings, ar -> {
      if (ar.failed()) {
        handler.handle(Future.failedFuture(ar.cause()));
        return;
      }
      List<JsonObject> results = ar.result();
      if (results.isEmpty()) {
        handler.handle(Future.succeededFuture(null));
        return;
      }
      handler.handle(Future.succeededFuture(results.get(0)));
    });
  }
}

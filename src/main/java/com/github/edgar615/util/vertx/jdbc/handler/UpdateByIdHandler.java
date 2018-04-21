package com.github.edgar615.util.vertx.jdbc.handler;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import com.github.edgar615.util.db.SQLBindings;
import com.github.edgar615.util.exception.SystemException;
import com.github.edgar615.util.vertx.jdbc.JdbcUtils;
import com.github.edgar615.util.vertx.jdbc.SystemExceptionAdapter;
import com.github.edgar615.util.vertx.jdbc.dataobj.UpdateById;
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
public class UpdateByIdHandler implements JdbcHandler {

  public void handle(AsyncSQLClient sqlClient, UpdateById updateById,
                     Handler<AsyncResult<Integer>> handler) {
    SQLBindings sqlBindings;
    try {
      sqlBindings = JdbcUtils.updateById(updateById.getResource(), updateById.getData(),
                                         updateById.getId());
    } catch (Exception e) {
      if (e instanceof SystemException) {
        handler.handle(Future.failedFuture(new SystemExceptionAdapter((SystemException) e)));
        return;
      }
      handler.handle(Future.failedFuture(e));
      return;
    }
    updateOrDelete(sqlClient, sqlBindings, handler);
  }
}

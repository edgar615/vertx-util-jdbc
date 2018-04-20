package com.github.edgar615.util.vertx.jdbc;

import com.google.common.collect.Lists;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;

import java.util.List;
import java.util.ServiceLoader;

/**
 * Created by Edgar on 2018/4/20.
 *
 * @author Edgar  Date 2018/4/20
 */
public interface JdbcMessageHandler {

  List<JdbcMessageHandler> handlers
          = Lists.newArrayList(ServiceLoader.load(JdbcMessageHandler.class));

  /**
   * 地址
   *
   * @return
   */
  String address();

  /**
   * 处理类
   *
   * @param sqlClient
   * @param headers 消息头
   * @param message 消息内容
   * @param handler 回调
   */
  void handle(AsyncSQLClient sqlClient, MultiMap headers, JsonObject message,
              Handler<AsyncResult<JsonObject>> handler);
}

package com.github.edgar615.util.vertx.jdbc;

import com.github.edgar615.util.search.Example;
import com.github.edgar615.util.vertx.jdbc.action.FindByExampleAction;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.MySQLClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用来从数据库中加载全部数据.
 * 消息的格式：
 * publishAddress: 取到一次数据后的广播地址
 * data: {
 * resource: 表名
 * query: 默认查询条件
 * limit: 每次获取数量
 * }
 *
 * @author Edgar  Date 2018/4/26
 */
public class LoadAllMessageConsumer implements Handler<Message<JsonObject>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoadAllMessageConsumer.class);

  private final Vertx vertx;

  private final AsyncSQLClient sqlClient;

  public LoadAllMessageConsumer(Vertx vertx, JsonObject config) {
    this.vertx = vertx;
    this.sqlClient = MySQLClient.createShared(vertx, new JsonObject());
  }

  @Override
  public void handle(Message<JsonObject> msg) {
    JsonObject jsonObject = msg.body();
    JsonObject data = jsonObject.getJsonObject("data");
    String pubAddress = jsonObject.getString("publishAddress");
    loadAll(data, pubAddress, new AtomicInteger(0));
  }

  private void loadAll(JsonObject jsonObject, String publishAddress, AtomicInteger count) {
    String table = jsonObject.getString("resource");
    String query = jsonObject.getString("query");
//    Integer start = jsonObject.getInteger("start", 0);
    Integer limit = jsonObject.getInteger("limit", 5);
    Object startPk = jsonObject.getValue("startPk");
    String primaryKey = JdbcUtils.primaryKey(table);
    Example example = Example.create().addQuery(query).asc(primaryKey)
            .greaterThan(JdbcUtils.lowerCamelName(primaryKey), startPk);

    JdbcTask.create(sqlClient)
            .execute("result", FindByExampleAction.create(table, example, 0, limit))
            .done(map -> (List<JsonObject>) map.get("result"))
            .setHandler(ar -> {
              if (ar.failed()) {
                LOGGER.error("load all {} failed, total:{}", table, count.get(), ar.cause());
                return;
              }
              if (ar.result().isEmpty()) {
                LOGGER.info("load all {} completed, total:{}", table, count.get());
              } else {
                //广播数据
                count.getAndAdd(ar.result().size());
                vertx.eventBus().publish(publishAddress, new JsonArray(ar.result()));
                JsonObject lastResult = ar.result().get(ar.result().size() - 1);
                Object lastPk = lastResult.getValue(JdbcUtils.lowerCamelName(primaryKey));
                JsonObject nextJson = new JsonObject()
                        .put("startPk", lastPk)
                        .put("query", query)
//                        .put("start", start + ar.result().size())
                        .put("limit", limit)
                        .put("resource", table);
                loadAll(nextJson, publishAddress, count);
              }
            });
  }
}

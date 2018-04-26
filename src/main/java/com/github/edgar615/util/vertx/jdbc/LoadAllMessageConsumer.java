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

import java.util.List;

/**
 * Created by Edgar on 2018/4/26.
 *
 * @author Edgar  Date 2018/4/26
 */
public class LoadAllMessageConsumer implements Handler<Message<JsonObject>> {

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
    loadAll(data, pubAddress);
  }

  public void loadAll(JsonObject jsonObject, String publishAddress) {
    String table = jsonObject.getString("resource");
    String query = jsonObject.getString("query");
    Integer start = jsonObject.getInteger("start", 0);
    Integer limit = jsonObject.getInteger("limit", 5);
    Object startPk = jsonObject.getValue("startPk");
    String primaryKey = JdbcUtils.primaryKey(table);
    Example example = Example.create().addQuery(query).asc(primaryKey)
            .greaterThan(JdbcUtils.lowerCamelName(primaryKey), startPk);

    JdbcTask.create(sqlClient)
            .execute("result", FindByExampleAction.create(table, example, start, limit))
            .done(map -> (List<JsonObject>) map.get("result"))
            .setHandler(ar -> {
              if (ar.failed()) {
                //广播错误
                ar.cause().printStackTrace();
                return;
              }
              if (ar.result().isEmpty()) {
                //通知加载完成
                System.out.println("complete");
              } else {
                //广播数据
                vertx.eventBus().publish(publishAddress, new JsonArray(ar.result()));
                JsonObject lastResult = ar.result().get(ar.result().size() - 1);
                Object lastPk = lastResult.getValue(JdbcUtils.lowerCamelName(primaryKey));
                JsonObject nextJson = new JsonObject()
                        .put("startPk", lastPk)
                        .put("query", query)
                        .put("start", start + ar.result().size())
                        .put("resource", table);
                loadAll(nextJson, publishAddress);
              }
            });
  }
}

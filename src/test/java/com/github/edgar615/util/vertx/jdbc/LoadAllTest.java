package com.github.edgar615.util.vertx.jdbc;

import com.github.edgar615.util.search.Example;
import com.github.edgar615.util.vertx.jdbc.action.CountByExampleAction;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.MySQLClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Edgar on 2018/4/18.
 *
 * @author Edgar  Date 2018/4/18
 */
@RunWith(VertxUnitRunner.class)
public class LoadAllTest {

  private Vertx vertx;

  private PersistentService persistentService;

  @Before
  public void setUp() {
    vertx = Vertx.vertx();
    JsonObject
            mySQLConfig = new JsonObject().put("host", "test.ihorn.com.cn").put
            ("username", "admin").put("password", "csst").put("database", "device")
            .put("maxPoolSize", 15);
    JsonObject persistentConfig = new JsonObject()
            .put("address", "database-service-address")
            .put("tables", new JsonArray().add("device"));

    JsonObject loadAllConfig = new JsonObject()
            .put("address", "__com.github.edgar615.util.vertx.jdbc.loadAll")
            .put("class", "com.github.edgar615.util.vertx.jdbc.LoadAllMessageConsumer")
            .put("config", mySQLConfig);

    JsonArray jsonObjectConsumer = new JsonArray().add(loadAllConfig);
    AtomicBoolean check = new AtomicBoolean();
    DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("mysql",
                                                                                       mySQLConfig)
                                                                          .put("persistent",
                                                                               persistentConfig)
                                                                          .put("eventbusConsumer",
                                                                               jsonObjectConsumer));
    vertx.deployVerticle(JdbcVerticle.class, options, ar -> check.set(true));
    Awaitility.await().until(() -> check.get());
  }

  @Test
  public void testLoadAll(TestContext testContext) {
//    vertx.eventBus().consumer("")
    JsonObject
            mySQLConfig = new JsonObject().put("host", "test.ihorn.com.cn").put
            ("username", "admin").put("password", "csst").put("database", "device")
            .put("maxPoolSize", 15);
    AsyncSQLClient sqlClient = MySQLClient.createShared(vertx, mySQLConfig);
    AtomicInteger count = new AtomicInteger();
    JdbcTask.create(sqlClient).execute("count", CountByExampleAction.create("device", Example.create() ))
            .done(ctx -> (Integer)ctx.get("count"))
            .setHandler(ar -> {
              if (ar.failed()) {
                testContext.fail();
              } else {
                count.set(ar.result());
              }
            });
    Awaitility.await().until(() -> count.get() > 0);
    System.out.println(count);

    JsonObject data = new JsonObject()
            .put("resource", "device");
    JsonObject jsonObject = new JsonObject()
            .put("data", data)
            .put("publishAddress", "test");
    vertx.eventBus().send("__com.github.edgar615.util.vertx.jdbc.loadAll", jsonObject);
    Async async = testContext.async();

  }
}

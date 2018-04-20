package com.github.edgar615.util.vertx.jdbc;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Edgar on 2018/4/18.
 *
 * @author Edgar  Date 2018/4/18
 */
@RunWith(VertxUnitRunner.class)
public class JdbcVerticleTest {

  private Vertx vertx;

  @Before
  public void setUp() {
    vertx = Vertx.vertx();
    JsonObject
            mySQLClientConfig = new JsonObject().put("host", "test.ihorn.com.cn").put
            ("username", "admin").put("password", "csst").put("database", "user_new");
    DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject()
                                                                          .put("mysql",
                                                                               mySQLClientConfig));
    AtomicBoolean check = new AtomicBoolean();
    vertx.deployVerticle(JdbcVerticle.class, options, ar -> check.set(true));
    Awaitility.await().until(() -> check.get());

  }

  @Test
  public void testInsert(TestContext testContext) {
    AtomicBoolean check = new AtomicBoolean();
    JsonObject data = new JsonObject()
            .put("username", "username");
    JsonObject jsonObject = new JsonObject()
            .put("table", "user")
            .put("data", data);
    String address = "__com.github.edgar615.util.vertx.jdbc.insert";
    vertx.eventBus().send(address, jsonObject, ar -> {
      if (ar.failed()) {
        ar.cause().printStackTrace();
        check.set(true);
        testContext.fail();
      }
      System.out.println(ar.result().body());
      check.set(true);
    });
    Awaitility.await().until(() -> check.get());
  }

  @Test
  public void testDeleteById(TestContext testContext) {
    AtomicBoolean check = new AtomicBoolean();
    JsonObject jsonObject = new JsonObject()
            .put("table", "user")
            .put("id", 15);
    String address = "__com.github.edgar615.util.vertx.jdbc.delete_by_id";
    vertx.eventBus().send(address, jsonObject, ar -> {
      if (ar.failed()) {
        ar.cause().printStackTrace();
        check.set(true);
        testContext.fail();
      }
      System.out.println(ar.result().body());
      check.set(true);
    });
    Awaitility.await().until(() -> check.get());
  }

}

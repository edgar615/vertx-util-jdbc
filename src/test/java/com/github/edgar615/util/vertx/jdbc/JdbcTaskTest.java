package com.github.edgar615.util.vertx.jdbc;

import com.github.edgar615.util.vertx.jdbc.action.*;
import com.google.common.collect.Lists;

import com.github.edgar615.util.base.Randoms;
import com.github.edgar615.util.search.Example;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.MySQLClient;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.serviceproxy.ServiceProxyBuilder;
import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Edgar on 2018/4/18.
 *
 * @author Edgar  Date 2018/4/18
 */
@RunWith(VertxUnitRunner.class)
public class JdbcTaskTest {

  private Vertx vertx;

  private PersistentService persistentService;

  private JsonObject mySQLConfig;

  @Before
  public void setUp() {
    vertx = Vertx.vertx();
    this.mySQLConfig = new JsonObject().put("host", "test.ihorn.com.cn").put
            ("username", "admin").put("password", "csst").put("database", "user_new");
    JsonObject persistentConfig = new JsonObject()
            .put("address", "database-service-address")
            .put("tables", new JsonArray().add("user"));

    AtomicBoolean check = new AtomicBoolean();
    DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("mysql",
            mySQLConfig)
            .put("persistent",
                    persistentConfig));
    vertx.deployVerticle(JdbcVerticle.class, options, ar -> check.set(true));
    Awaitility.await().until(() -> check.get());

    ServiceProxyBuilder
            builder = new ServiceProxyBuilder(vertx).setAddress("database-service-address");
    persistentService = builder.build(PersistentService.class);
  }

  @Test
  public void testInsertAndFindById(TestContext testContext) {
    AsyncSQLClient sqlClient = MySQLClient.createShared(vertx, mySQLConfig);
    JdbcTask jdbcTask = new JdbcTaskImpl(sqlClient);
    JsonObject data = new JsonObject()
            .put("username", "username");
    jdbcTask = jdbcTask.execute("insert", InsertAndGenerateKeyAction.create("user", data));
    jdbcTask.execute("foo", ctx -> FindByIdAction.create("user", ctx.get("insert")));
    Future<Integer> future = jdbcTask.done(ctx -> (Integer) ctx.get("insert"));
    AtomicBoolean check = new AtomicBoolean();
    future.setHandler(ar -> {
      if (ar.failed()) {
        ar.cause().printStackTrace();
        testContext.fail();
      } else {
        check.set(true);
      }
    });
    Awaitility.await().until(() -> check.get());
  }

  @Test
  public void testInsertAndCount(TestContext testContext) {
    AsyncSQLClient sqlClient = MySQLClient.createShared(vertx, mySQLConfig);
    JdbcTask jdbcTask = new JdbcTaskImpl(sqlClient);

    jdbcTask.execute("count1", CountByExampleAction.create("user", Example.create()));
    JsonObject data = new JsonObject()
            .put("username", "username");
    jdbcTask = jdbcTask.execute("insert", InsertAndGenerateKeyAction.create("user", data));
    jdbcTask.execute("count2", CountByExampleAction.create("user", Example.create()));
    Future<List<Integer>> future = jdbcTask.done(ctx -> Lists.newArrayList((Integer) ctx.get
            ("count1"), (Integer) ctx.get("count2")));
    AtomicBoolean check = new AtomicBoolean();
    future.setHandler(ar -> {
      if (ar.failed()) {
        ar.cause().printStackTrace();
        testContext.fail();
      } else {
        int count1 = ar.result().get(0);
        int count2 = ar.result().get(1);
        testContext.assertEquals(count1 + 1, count2);
        check.set(true);
      }
    });
    Awaitility.await().until(() -> check.get());
  }

  @Test
  public void testDeleteByIdAndCount(TestContext testContext) {
    AsyncSQLClient sqlClient = MySQLClient.createShared(vertx, mySQLConfig);
    JdbcTask jdbcTask = new JdbcTaskImpl(sqlClient);

    jdbcTask.execute("first", FindFirstByExampleAction.create("user", Example.create()));
    jdbcTask.execute("count1", CountByExampleAction.create("user", Example.create()));
    jdbcTask.execute("delete", ctx -> {
      JsonObject jsonObject = (JsonObject) ctx.get("first");
      return DeleteByIdAction.create("user", jsonObject.getValue("userId"));
    });
    jdbcTask.execute("count2", CountByExampleAction.create("user", Example.create()));
    Future<List<Integer>> future = jdbcTask.done(ctx -> Lists.newArrayList((Integer) ctx.get
            ("count1"), (Integer) ctx.get("count2")));
    AtomicBoolean check = new AtomicBoolean();
    future.setHandler(ar -> {
      if (ar.failed()) {
        ar.cause().printStackTrace();
        testContext.fail();
      } else {
        int count1 = ar.result().get(0);
        int count2 = ar.result().get(1);
        testContext.assertEquals(count1 - 1, count2);
        check.set(true);
      }
    });
    Awaitility.await().until(() -> check.get());
  }

}

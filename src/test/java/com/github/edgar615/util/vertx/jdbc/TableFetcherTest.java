package com.github.edgar615.util.vertx.jdbc;

import com.google.common.collect.Lists;

import com.github.edgar615.util.db.SQLBindings;
import com.github.edgar615.util.vertx.jdbc.table.TableFetcher;
import com.github.edgar615.util.vertx.jdbc.table.TableFetcherOptions;
import com.github.edgar615.util.vertx.jdbc.table.TableRegistry;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.awaitility.Awaitility;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Edgar on 2018/4/18.
 *
 * @author Edgar  Date 2018/4/18
 */
@RunWith(VertxUnitRunner.class)
public class TableFetcherTest {

  private Vertx vertx;

  @Before
  public void setUp(TestContext testContext) {
    vertx = Vertx.vertx();
    TableFetcherOptions options = new TableFetcherOptions()
            .setHost("test.ihorn.com.cn")
            .setDatabase("user_new")
            .setUsername("admin")
            .setPassword("csst");
    AtomicBoolean check = new AtomicBoolean();
    TableFetcher fetcher = new TableFetcher(vertx, options);
    Future<Void> completeFuture = Future.future();
    fetcher.start(vertx, completeFuture);
    completeFuture.setHandler(ar -> {
      if (ar.failed()) {
        ar.cause().printStackTrace();
        testContext.asyncAssertFailure();
      }
      System.out.println(TableRegistry.instance().tables());
      check.set(true);
    });
    Awaitility.await().until(() -> check.get());
  }

  @After
  public void tearDown() {
    TableRegistry.instance().clear();
  }

  @Test
  public void testInsert(TestContext testContext) {
    JsonObject data = new JsonObject()
            .put(UUID.randomUUID().toString(), "foo")
            .put("username", "foo");
    SQLBindings sqlBindings = JdbcUtils.insert("user", data);
    System.out.println(sqlBindings.sql());
    System.out.println(sqlBindings.bindings());
    Assert.assertEquals("insert into user(username) values(?)", sqlBindings.sql());
  }

  @Test
  public void testDeleteById(TestContext testContext) {
    SQLBindings sqlBindings = JdbcUtils.deleteById("user", 1);
    System.out.println(sqlBindings.sql());
    System.out.println(sqlBindings.bindings());
    Assert.assertEquals("delete from user where user_id = ?", sqlBindings.sql());
  }

  @Test
  public void testUpdateById(TestContext testContext) {
    JsonObject data = new JsonObject()
            .put(UUID.randomUUID().toString(), "foo")
            .put("username", "foo");
    SQLBindings sqlBindings = JdbcUtils.updateById("user", data, 1);
    System.out.println(sqlBindings.sql());
    System.out.println(sqlBindings.bindings());
    Assert.assertEquals("update user set username = ? where user_id = ?", sqlBindings.sql());
  }

  @Test
  public void testFindById(TestContext testContext) {
    SQLBindings sqlBindings = JdbcUtils.findById("user", 1);
    System.out.println(sqlBindings.sql());
    System.out.println(sqlBindings.bindings());
    Assert.assertEquals("select * from user where user_id = ?", sqlBindings.sql());
  }

  @Test
  public void testFindById2(TestContext testContext) {
    SQLBindings sqlBindings = JdbcUtils.findById("user", 1, Lists.newArrayList("userId",
                                                                               "username",
                                                                               UUID.randomUUID()
                                                                                       .toString
                                                                                               ()));
    System.out.println(sqlBindings.sql());
    System.out.println(sqlBindings.bindings());
    Assert.assertEquals("select user_id,username from user where user_id = ?", sqlBindings.sql());
  }
}

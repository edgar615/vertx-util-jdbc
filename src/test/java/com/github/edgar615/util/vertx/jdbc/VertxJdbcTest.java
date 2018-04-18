package com.github.edgar615.util.vertx.jdbc;

import com.github.edgar615.util.search.Example;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.MySQLClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * Created by Edgar on 2018/4/18.
 *
 * @author Edgar  Date 2018/4/18
 */
@RunWith(VertxUnitRunner.class)
public class VertxJdbcTest {

  private Vertx vertx;

  private SQLClient sqlClient;

  private Function<JsonObject, DeviceScript> function;

  @Before
  public void setUp() {
    vertx = Vertx.vertx();
    JsonObject
            mySQLClientConfig = new JsonObject().put("host", "test.ihorn.com.cn").put
            ("username", "admin").put("password", "csst").put("database", "om_new");
    sqlClient = MySQLClient.createShared(vertx, mySQLClientConfig);
    function = new Function<JsonObject, DeviceScript>() {
      @Override
      public DeviceScript apply(JsonObject jsonObject) {
        return JdbcUtils.convertToPojo(jsonObject, DeviceScript.class);
      }
    };
  }

  @Test
  public void testPagination(TestContext testContext) {
    AtomicBoolean check = new AtomicBoolean();
    sqlClient.getConnection(ar -> {
      if (ar.failed()) {
        ar.cause().printStackTrace();
        testContext.fail();
        return;
      }
      VertxJdbc jdbc = VertxJdbc.create(ar.result());
      jdbc.pagination(DeviceScript.class, Example.create(), 1, 3, function, pr -> {
        if (pr.failed()) {
          pr.cause().printStackTrace();
          testContext.fail();
          return;
        }
        System.out.println(pr.result());
        check.set(true);
      });
    });
    Awaitility.await().until(() -> check.get());
  }

//  @Test
//  public void testInsert(TestContext testContext) {
//    AtomicBoolean check = new AtomicBoolean();
//    sqlClient.getConnection(ar -> {
//      if (ar.failed()) {
//        ar.cause().printStackTrace();
//        testContext.fail();
//        return;
//      }
//      VertxJdbc jdbc = VertxJdbc.create(ar.result());
//      DeviceScript deviceScript = new DeviceScript();
//      deviceScript.setMessageType("dfere");
//      jdbc.insert(deviceScript, pr -> {
//        if (pr.failed()) {
//          pr.cause().printStackTrace();
//          testContext.fail();
//          return;
//        }
//        System.out.println(pr.result());
//        check.set(true);
//      });
//    });
//    Awaitility.await().until(() -> check.get());
//  }

}

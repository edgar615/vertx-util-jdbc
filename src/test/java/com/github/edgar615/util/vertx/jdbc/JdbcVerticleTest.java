package com.github.edgar615.util.vertx.jdbc;

import com.github.edgar615.util.exception.SystemException;
import com.github.edgar615.util.search.Example;
import com.github.edgar615.util.vertx.jdbc.dataobj.CountExample;
import com.github.edgar615.util.vertx.jdbc.dataobj.DeleteExample;
import com.github.edgar615.util.vertx.jdbc.dataobj.DeleteById;
import com.github.edgar615.util.vertx.jdbc.dataobj.FindExample;
import com.github.edgar615.util.vertx.jdbc.dataobj.FindById;
import com.github.edgar615.util.vertx.jdbc.dataobj.InsertData;
import com.github.edgar615.util.vertx.jdbc.dataobj.PaginationExample;
import com.github.edgar615.util.vertx.jdbc.dataobj.UpdateExample;
import com.github.edgar615.util.vertx.jdbc.dataobj.UpdateById;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.MySQLClient;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.serviceproxy.ServiceProxyBuilder;
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

  private PersistentService persistentService;

  @Before
  public void setUp() {
    vertx = Vertx.vertx();
    JsonObject
            mySQLConfig = new JsonObject().put("host", "test.ihorn.com.cn").put
            ("username", "admin").put("password", "csst").put("database", "user_new");
    AsyncSQLClient sqlClient = MySQLClient.createShared(vertx, mySQLConfig);
    AtomicBoolean check = new AtomicBoolean();
    DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("mysql",
                                                                                       mySQLConfig));
    vertx.deployVerticle(JdbcVerticle.class, options, ar -> check.set(true));
    Awaitility.await().until(() -> check.get());

    PersistentService service = new PersistentServiceImpl(sqlClient);
// Register the handler
    new ServiceBinder(vertx)
            .setAddress("database-service-address")
            .register(PersistentService.class, service);
    vertx.eventBus().registerCodec(new SystemExceptionMessageCodec());

    ServiceProxyBuilder
            builder = new ServiceProxyBuilder(vertx).setAddress("database-service-address");
    persistentService = builder.build(PersistentService.class);
  }

  @Test
  public void testInsert(TestContext testContext) {
    AtomicBoolean check = new AtomicBoolean();
    JsonObject data = new JsonObject()
            .put("username", "username");
    InsertData insertData = new InsertData();
    insertData.setResource("user");
    insertData.setData(data);
    persistentService.insert(insertData, ar -> {
      if (ar.failed()) {
        System.out.println(ar.cause().getClass());
        if (ar.cause() instanceof SystemExceptionAdapter) {
          SystemExceptionAdapter adapter = (SystemExceptionAdapter) ar.cause();
          SystemException systemException = adapter.systemException();
          systemException.printStackTrace();
        }
        check.set(true);
        testContext.fail();
      }
      System.out.println(ar.result());
      check.set(true);
    });
    Awaitility.await().until(() -> check.get());
  }

  @Test
  public void testDeleteById(TestContext testContext) {
    AtomicBoolean check = new AtomicBoolean();
    DeleteById deleteById = new DeleteById();
    deleteById.setId("17");
    deleteById.setResource("user");

    persistentService.deleteById(deleteById, ar -> {
      if (ar.failed()) {
        ar.cause().printStackTrace();
        check.set(true);
        testContext.fail();
      }
      System.out.println(ar.result());
      check.set(true);
    });

    Awaitility.await().until(() -> check.get());
  }

  @Test
  public void testUpdateById(TestContext testContext) {
    AtomicBoolean check = new AtomicBoolean();
    JsonObject data = new JsonObject()
            .put("username", "xxxx")
            .put("fullname", "xoere");
    UpdateById updateById = new UpdateById();
    updateById.setResource("user");
    updateById.setId("17");
    updateById.setData(data);

    persistentService.updateById(updateById, ar -> {
      if (ar.failed()) {
        ar.cause().printStackTrace();
        check.set(true);
        testContext.fail();
      }
      System.out.println(ar.result());
      check.set(true);
    });

    Awaitility.await().until(() -> check.get());
  }

  @Test
  public void testFindById(TestContext testContext) {
    AtomicBoolean check = new AtomicBoolean();

    FindById findById = new FindById();
    findById.setResource("user");
    findById.setId(17 + "");
    persistentService.findById(findById, ar -> {
      if (ar.failed()) {
        ar.cause().printStackTrace();
        check.set(true);
        testContext.fail();
      }
      System.out.println(ar.result());
      System.out.println(JdbcUtils.lowCamelField(ar.result()));
      check.set(true);
    });

    Awaitility.await().until(() -> check.get());
  }

  @Test
  public void testFindByExample(TestContext testContext) {
    AtomicBoolean check = new AtomicBoolean();
    Example example = Example.create()
//            .equalsTo("username", "username")
            .equalsTo("xxx", "username")
            .desc("userId");
    example.addField("username").addField("xxx").addField("userId");
    FindExample findExample = new FindExample();
    findExample.setResource("user");
    findExample.fromExample(example);
//    findExample.setStart(5);
//    findExample.setLimit(1);
    persistentService.findByExample(findExample, ar -> {
      if (ar.failed()) {
        ar.cause().printStackTrace();
        check.set(true);
        testContext.fail();
      }
      System.out.println(ar.result());
      check.set(true);
    });

    Awaitility.await().until(() -> check.get());
  }

  @Test
  public void testDeleteByExample(TestContext testContext) {
    AtomicBoolean check = new AtomicBoolean();
    Example example = Example.create()
            .equalsTo("username", "username")
            .equalsTo("xxx", "username");
    DeleteExample deleteExample = new DeleteExample();
    deleteExample.setResource("user");
    deleteExample.fromExample(example);
    persistentService.deleteByExample(deleteExample, ar -> {
      if (ar.failed()) {
        ar.cause().printStackTrace();
        check.set(true);
        testContext.fail();
      }
      System.out.println(ar.result());
      check.set(true);
    });

    Awaitility.await().until(() -> check.get());
  }

  @Test
  public void testUpdateByExample(TestContext testContext) {
    AtomicBoolean check = new AtomicBoolean();
    JsonObject data = new JsonObject()
            .put("username", "username");
    Example example = Example.create()
            .equalsTo("username", "xxxx")
            .equalsTo("xxx", "username");
    UpdateExample updateExample = new UpdateExample();
    updateExample.setResource("user");
    updateExample.fromExample(example);
    updateExample.setData(data);
    persistentService.updateByExample(updateExample, ar -> {
      if (ar.failed()) {
        ar.cause().printStackTrace();
        check.set(true);
        testContext.fail();
      }
      System.out.println(ar.result());
      check.set(true);
    });

    Awaitility.await().until(() -> check.get());
  }

  @Test
  public void testCountByExample(TestContext testContext) {
    AtomicBoolean check = new AtomicBoolean();
    Example example = Example.create()
//            .equalsTo("username", "username")
            .equalsTo("xxx", "username")
            .desc("userId");
    example.addField("username").addField("xxx").addField("userId");
    CountExample countExample = new CountExample();
    countExample.setResource("user");
    countExample.fromExample(example);
    persistentService.countByExample(countExample, ar -> {
      if (ar.failed()) {
        ar.cause().printStackTrace();
        check.set(true);
        testContext.fail();
      }
      System.out.println(ar.result());
      check.set(true);
    });

    Awaitility.await().until(() -> check.get());
  }

  @Test
  public void testPaginationByExample(TestContext testContext) {
    AtomicBoolean check = new AtomicBoolean();
    Example example = Example.create()
//            .equalsTo("username", "username")
            .equalsTo("xxx", "username")
            .desc("userId");
    example.addField("username").addField("xxx");
    PaginationExample paginationExample = new PaginationExample();
    paginationExample.setResource("user");
    paginationExample.fromExample(example);
    paginationExample.setPageSize(1);
    persistentService.pagination(paginationExample, ar -> {
      if (ar.failed()) {
        ar.cause().printStackTrace();
        check.set(true);
        testContext.fail();
      }
      System.out.println(ar.result());
      check.set(true);
    });

    Awaitility.await().until(() -> check.get());
  }

  @Test
  public void testPage(TestContext testContext) {
    AtomicBoolean check = new AtomicBoolean();
    Example example = Example.create()
//            .equalsTo("username", "username")
            .equalsTo("xxx", "username")
            .desc("userId");
    example.addField("username").addField("xxx");
    FindExample findExample = new FindExample();
    findExample.setResource("user");
    findExample.fromExample(example);
    persistentService.page(findExample, ar -> {
      if (ar.failed()) {
        ar.cause().printStackTrace();
        check.set(true);
        testContext.fail();
      }
      System.out.println(ar.result());
      check.set(true);
    });

    Awaitility.await().until(() -> check.get());
  }
}

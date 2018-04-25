package com.github.edgar615.util.vertx.jdbc;

import com.github.edgar615.util.exception.SystemException;
import com.github.edgar615.util.search.Example;
import com.github.edgar615.util.vertx.jdbc.dataobj.CountExample;
import com.github.edgar615.util.vertx.jdbc.dataobj.DeleteById;
import com.github.edgar615.util.vertx.jdbc.dataobj.DeleteExample;
import com.github.edgar615.util.vertx.jdbc.dataobj.FindById;
import com.github.edgar615.util.vertx.jdbc.dataobj.FindExample;
import com.github.edgar615.util.vertx.jdbc.dataobj.InsertData;
import com.github.edgar615.util.vertx.jdbc.dataobj.PaginationExample;
import com.github.edgar615.util.vertx.jdbc.dataobj.UpdateById;
import com.github.edgar615.util.vertx.jdbc.dataobj.UpdateExample;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.serviceproxy.ServiceProxyBuilder;
import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

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

    AtomicBoolean check = new AtomicBoolean();
    DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("mysql",
                                                                                       mySQLConfig).put("persistent", persistentConfig));
    vertx.deployVerticle(JdbcVerticle.class, options, ar -> check.set(true));
    Awaitility.await().until(() -> check.get());
  }


  @Test
  public void testLoadAll(TestContext testContext) {
//    vertx.eventBus().consumer("")
    JsonObject jsonObject = new JsonObject()
            .put("resource", "device");
    vertx.eventBus().send("__com.github.edgar615.util.vertx.jdbc.loadAll", jsonObject);
    Async async = testContext.async();

  }
}

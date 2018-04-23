package com.github.edgar615.util.vertx.jdbc;

import com.github.edgar615.util.vertx.jdbc.table.TableFetcher;
import com.github.edgar615.util.vertx.jdbc.table.TableFetcherOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.MySQLClient;
import io.vertx.serviceproxy.ServiceBinder;

/**
 * Created by Edgar on 2018/4/20.
 *
 * @author Edgar  Date 2018/4/20
 */
public class JdbcVerticle extends AbstractVerticle {

  private AsyncSQLClient sqlClient;


  @Override
  public void start(Future<Void> startFuture) throws Exception {
//    host localhost
//    port 3306
//    username  vertx
//    password password
//    database testdb
//    charset UTF-8
//    connectTimeout 10000L
//    testTimeout 10000L
//    queryTimeout
    JsonObject mySQLConfig = config().getJsonObject("mysql");
    this.sqlClient = MySQLClient.createShared(vertx, mySQLConfig);
    //读取数据库元数据
    TableFetcherOptions options = new TableFetcherOptions().setUsername("admin")
            .setPassword("csst")
            .addGenTable("user")
            .setIgnoreColumnsStr("created_on,updated_on")
            .setHost("test.ihorn.com.cn")
            .setDatabase("user_new");
    TableFetcher fetcher = new TableFetcher(vertx, options);
    fetcher.start(vertx, startFuture);

    //除了通过service proxy外，还可以直接通过eventbus调用
//    PersistentService service = new PersistentServiceImpl(sqlClient);
//    new ServiceBinder(vertx)
//            .setAddress("database-service-address")
//            .register(PersistentService.class, service);
//    vertx.eventBus().registerCodec(new SystemExceptionMessageCodec());
  }
}

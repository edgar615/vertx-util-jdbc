package com.github.edgar615.util.vertx.jdbc;

import com.google.common.base.Joiner;

import com.github.edgar615.util.vertx.eventbus.EventbusUtils;
import com.github.edgar615.util.vertx.jdbc.table.TableFetcher;
import com.github.edgar615.util.vertx.jdbc.table.TableFetcherOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.MySQLClient;
import io.vertx.serviceproxy.ServiceBinder;

import java.util.ArrayList;
import java.util.List;

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
    fetchTable(startFuture);

    JsonObject persistentConfig = config().getJsonObject("persistent", new JsonObject());
    if (persistentConfig.getValue("address") instanceof String) {
      //除了通过service proxy外，还可以直接通过eventbus调用
      String address = persistentConfig.getString("address");
      PersistentService service = new PersistentServiceImpl(sqlClient);
      new ServiceBinder(vertx)
              .setAddress(address)
              .register(PersistentService.class, service);
      vertx.eventBus().registerCodec(new SystemExceptionMessageCodec());
    }
    //注册一个事件根据条件依次加载数据，一般用户系统刚启动时初始化数据
    try {
      JsonArray consumerArray = config().getJsonArray("eventbusConsumer", new JsonArray());
      EventbusUtils.registerConsumer(vertx, consumerArray);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private void fetchTable(Future<Void> startFuture) {
    JsonObject mySQLConfig = config().getJsonObject("mysql");
    String host = mySQLConfig.getString("host", "localhost");
    int port = mySQLConfig.getInteger("port", 3306);
    String username = mySQLConfig.getString("username", "vertx");
    String password = mySQLConfig.getString("password", "password");
    String database = mySQLConfig.getString("database", "testdb");
    TableFetcherOptions options = new TableFetcherOptions().setUsername(username)
            .setPassword(password)
            .setHost(host)
            .setPort(port)
            .setDatabase(database)
            .setJdbcArg("connectTimeout=" + mySQLConfig.getLong("connectTimeout", 10000l));
    JsonObject persistentConfig = config().getJsonObject("persistent", new JsonObject());
    if (persistentConfig.getValue("loginTimeout") instanceof Integer) {
      options.setLoginTimeout(persistentConfig.getInteger("loginTimeout"));
    }
    if (persistentConfig.getValue("tables") instanceof JsonArray) {
      options.addGenTables(convertToListString(persistentConfig.getJsonArray("tables")));
    }
    if (persistentConfig.getValue("ignoreColumns") instanceof JsonArray) {
      String ignoreColumnsStr = Joiner.on(",").join(convertToListString(persistentConfig
                                                                                .getJsonArray(
                                                                                        "ignoreColumns")));
      options.setIgnoreColumnsStr(ignoreColumnsStr);
    }
    if (persistentConfig.getValue("ignoreTables") instanceof JsonArray) {
      String ignoreTablesStr = Joiner.on(",").join(convertToListString(persistentConfig
                                                                               .getJsonArray(
                                                                                       "ignoreTables")));
      options.setIgnoreTablesStr(ignoreTablesStr);
    }
    TableFetcher fetcher = new TableFetcher(vertx, options);
    fetcher.start(vertx, startFuture);
  }

  private List<String> convertToListString(JsonArray arr) {
    List<String> list = new ArrayList<>();
    for (Object obj : arr) {
      if (obj instanceof String) {
        list.add((String) obj);
      }
    }
    return list;
  }

}

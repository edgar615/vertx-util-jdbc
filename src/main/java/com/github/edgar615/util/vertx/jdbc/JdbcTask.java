package com.github.edgar615.util.vertx.jdbc;

import io.vertx.core.Future;
import io.vertx.ext.asyncsql.AsyncSQLClient;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Edgar on 2018/4/25.
 *
 * @author Edgar  Date 2018/4/25
 */
public interface JdbcTask {

  static JdbcTask create(AsyncSQLClient sqlClient) {
    return new JdbcTaskImpl(sqlClient);
  }

  static JdbcTask create(AsyncSQLClient sqlClient, boolean startTx) {
    return new JdbcTaskImpl(sqlClient, startTx);
  }

  <T> Future<T> done(Function<Map<String, Object>, T> function);

  /**
   * 执行JDBC操作
   *
   * @param action Jdbc操作
   * @param <T>
   * @return
   */
  default <T> JdbcTask execute(JdbcAction<T> action) {
    return execute(null, action);
  }

  /**
   * 执行JDBC操作，将结果保存在上下文中
   *
   * @param taskName 上下文中的结果名
   * @param action   Jdbc操作
   * @param <T>
   * @return
   */
  default <T> JdbcTask execute(String taskName, JdbcAction<T> action) {
    return execute(taskName, ctx -> action);
  }

  /**
   * 执行JDBC操作，将结果保存在上下文中
   *
   * @param actionFunction 根据上下文转换出jdbc操作
   * @param <T>
   * @return
   */
  default <T> JdbcTask execute(Function<Map<String, Object>, JdbcAction<T>> actionFunction) {
    return execute(null, actionFunction);
  }

  /**
   * 执行JDBC操作，将结果保存在上下文中
   *
   * @param taskName       上下文中的结果名
   * @param actionFunction 根据上下文转换出jdbc操作
   * @param <T>
   * @return
   */
  <T> JdbcTask execute(String taskName, Function<Map<String, Object>, JdbcAction<T>> actionFunction);

  JdbcTask andThen(Consumer<Map<String, Object>> consumer);

  /**
   * 开启事务
   *
   * @return
   */
  JdbcTask startTx();

}

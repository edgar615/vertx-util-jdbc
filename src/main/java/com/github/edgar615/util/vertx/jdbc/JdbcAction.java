package com.github.edgar615.util.vertx.jdbc;

import io.vertx.core.json.JsonObject;

import java.util.function.Consumer;

/**
 * Created by Edgar on 2018/4/25.
 *
 * @author Edgar  Date 2018/4/25
 */
public interface JdbcAction {
  void execute(JsonObject context, Consumer<Boolean> onResult);
}

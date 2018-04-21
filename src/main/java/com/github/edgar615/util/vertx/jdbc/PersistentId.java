package com.github.edgar615.util.vertx.jdbc;

import com.google.common.collect.Multimap;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Edgar on 2018/4/21.
 *
 * @author Edgar  Date 2018/4/21
 */
@DataObject
public class PersistentId {
  private final Object id;

  public Object getId() {
    return id;
  }

  public PersistentId(Object id) {
    this.id = check(id);
  }

  public PersistentId(JsonObject json) {
    this(json.getValue("id"));
  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject()
            .put("id", id);
    return jsonObject;
  }

  private static Object check(Object val) {
    if (val == null) {
      // OK
    } else if (val instanceof Number && !(val instanceof BigDecimal)) {
      // OK
    } else if (val instanceof Boolean) {
      // OK
    } else if (val instanceof String) {
      // OK
    } else if (val instanceof Character) {
      // OK
    } else if (val instanceof CharSequence) {
      val = val.toString();
    } else if (val instanceof Map) {
      val = new HashMap<>((Map) val);
    } else if (val instanceof JsonObject) {
      val = ((JsonObject) val).copy();
    } else if (val instanceof List) {
      val = new ArrayList<>((List) val);
    } else if (val instanceof JsonArray) {
      val = ((JsonArray) val).copy();
    } else {
      return val.toString();
    }
    return val;
  }
}

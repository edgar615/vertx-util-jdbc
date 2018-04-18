package com.github.edgar615.util.vertx.jdbc;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Edgar on 2018/4/18.
 *
 * @author Edgar  Date 2018/4/18
 */
public class JdbcUtils {
  public static <T> T convertToPojo(JsonObject jsonObject, Class<T> tClass) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.setPropertyNamingStrategy(
            PropertyNamingStrategy.SNAKE_CASE);
    return mapper.convertValue(toMap(jsonObject), tClass);
  }

  static Map<String, Object> toMap(JsonObject jsonObject) {
    Map<String, Object> map = new HashMap<>();
    jsonObject.getMap().forEach((key, value) -> {
      map.put(key, check(value));
    });
    return map;
  }

  static List<Object> toList(JsonArray jsonArray) {
    List<Object> list = new ArrayList<>();
    jsonArray.getList().forEach(value -> {
      list.add(check(value));
    });
    return list;
  }

  static Object check(Object val) {
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
      val = toMap(((JsonObject) val));
    } else if (val instanceof List) {
      val = new ArrayList<>((List) val);
    } else if (val instanceof JsonArray) {
      val = toList((JsonArray) val);
    } else {
      throw new IllegalStateException("Illegal type in Event Content: " + val.getClass());
    }
    return val;
  }

}

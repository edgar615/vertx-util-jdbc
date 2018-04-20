package com.github.edgar615.util.vertx.jdbc;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.github.edgar615.util.base.MorePreconditions;
import com.github.edgar615.util.base.StringUtils;
import com.github.edgar615.util.db.SQLBindings;
import com.github.edgar615.util.vertx.jdbc.meta.Table;
import com.github.edgar615.util.vertx.jdbc.meta.TableRegistry;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

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

  /**
   * 根据主键查询.
   *
   * @param tableName 表名
   * @param id        主键
   * @return {@link SQLBindings}
   */
  public static SQLBindings findById(String tableName, Object id) {
    return findById(tableName, id, Lists.newArrayList());
  }

  private static List<String> tableFields(String tableName) {
    return getTable(tableName).getColumns()
            .stream()
            .map(c -> c.getName())
            .collect(Collectors.toList());
  }

  private static String primaryKey(String tableName) {
    return getTable(tableName).getPk();
  }

  private static Table getTable(String tableName) {
    Optional<Table> optional =
            TableRegistry.instance().tables().stream()
                    .filter(table -> underscoreName(table.getName()).equalsIgnoreCase(tableName))
                    .findFirst();
    if (!optional.isPresent()) {
      throw new NoSuchElementException("table:" + tableName);
    }
    return optional.get();
  }

  /**
   * 根据主键查询.
   *
   * @param tableName 表名
   * @param id        主键
   * @param fields    返回的属性列表
   * @param <ID>      主键类型
   * @return {@link SQLBindings}
   */
  public static <ID> SQLBindings findById(String tableName,
                                          ID id, List<String> fields) {
    String selectedField = "*";
    if (!fields.isEmpty()) {
      List<String> tableFields = tableFields(tableName);
      selectedField = Joiner.on(",")
              .join(fields.stream()
                            .map(f -> underscoreName(f))
                            .filter(f -> tableFields.contains(f))
                            .collect(Collectors.toList()));
    }
    StringBuilder s = new StringBuilder();
    s.append("select ")
            .append(selectedField)
            .append(" from ")
            .append(underscoreName(tableName))
            .append(" where ")
            .append(underscoreName(primaryKey(tableName)))
            .append(" = ?");
    return SQLBindings.create(s.toString(), Arrays.asList(id));
  }


  /**
   * 根据主键删除.
   *
   * @param tableName 表名
   * @param id        主键
   * @return {@link SQLBindings}
   */
  public static SQLBindings deleteById(String tableName, Object id) {
    StringBuilder s = new StringBuilder();
    s.append("delete from ")
            .append(underscoreName(tableName))
            .append(" where ")
            .append(underscoreName(primaryKey(tableName)))
            .append(" = ?");
    return SQLBindings.create(s.toString(), Arrays.asList(id));
  }

  /**
   * 根据主键更新,忽略实体中的null.
   *
   * @param tableName  表名
   * @param jsonObject 持久化对象
   * @param id         主键
   * @return {@link SQLBindings}
   */
  public static SQLBindings updateById(String tableName, JsonObject jsonObject, Object id) {
    List<String> columns = new ArrayList<>();
    List<Object> params = new ArrayList<>();
    List<String> virtualFields = getTable(tableName).getVirtualFields();
    List<String> fields = getTable(tableName).getFields();
    jsonObject.forEach(e -> {
      String columnName = underscoreName(e.getKey());
      if (e.getValue() != null && !virtualFields.contains(columnName)
          && fields.contains(columnName)) {
        columns.add(columnName + " = ?");
        params.add(e.getValue());
      }
    });
    MorePreconditions.checkNotEmpty(columns, "no update field");

    StringBuilder s = new StringBuilder();
    s.append("update ")
            .append(underscoreName(tableName))
            .append(" set ")
            .append(Joiner.on(",").join(columns))
            .append(" where ")
            .append(underscoreName(primaryKey(tableName)))
            .append(" = ?");
    params.add(id);
    return SQLBindings.create(s.toString(), params);
  }

  /**
   * insert.
   *
   * @param tableName 表名
   * @param jsonObject 持久化对象
   * @return {@link SQLBindings}
   */
  public static SQLBindings insert(String tableName, JsonObject jsonObject) {
    List<String> columns = new ArrayList<>();
    List<String> prepare = new ArrayList<>();
    List<Object> params = new ArrayList<>();
    List<String> virtualFields = getTable(tableName).getVirtualFields();
    List<String> fields = getTable(tableName).getFields();
    jsonObject.forEach(e -> {
      String columnName = underscoreName(e.getKey());
      if (e.getValue() != null && !virtualFields.contains(columnName)
          && fields.contains(columnName)) {
        columns.add(columnName);
        prepare.add("?");
        params.add(e.getValue());
      }
    });
    StringBuilder s = new StringBuilder();
    s.append("insert into ")
            .append(underscoreName(tableName))
            .append("(")
            .append(Joiner.on(",").join(columns))
            .append(") values(")
            .append(Joiner.on(",").join(prepare))
            .append(")");
    return SQLBindings.create(s.toString(), params);
  }

  /**
   * 根据主键更新,设置字段为null.
   *
   * @param tableName  表名
   * @param fields 需要更新的字段
   * @param id     主键
   * @return {@link SQLBindings}
   */
  public static SQLBindings setNullById(String tableName,
                                        List<String> fields, Object id) {
    List<String> tableFields = tableFields(tableName);
    List<String> columns = fields.stream()
            .map(f -> underscoreName(f))
            .filter(f -> tableFields.contains(f))
            .map(f -> f + " = null")
            .collect(Collectors.toList());
    MorePreconditions.checkNotEmpty(fields, "no update field");

    List<Object> params = new ArrayList<>();
    StringBuilder s = new StringBuilder();
    s.append("update ")
            .append(underscoreName(tableName))
            .append(" set ")
            .append(Joiner.on(",").join(columns))
            .append(" where ")
            .append(underscoreName(primaryKey(tableName)))
            .append(" = ?");
    params.add(id);
    return SQLBindings.create(s.toString(), params);
  }

  private static String underscoreName(String name) {
    return StringUtils.underscoreName(name);
  }


  private static Map<String, Object> toMap(JsonObject jsonObject) {
    Map<String, Object> map = new HashMap<>();
    jsonObject.getMap().forEach((key, value) -> {
      map.put(key, check(value));
    });
    return map;
  }

  private static List<Object> toList(JsonArray jsonArray) {
    List<Object> list = new ArrayList<>();
    jsonArray.getList().forEach(value -> {
      list.add(check(value));
    });
    return list;
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

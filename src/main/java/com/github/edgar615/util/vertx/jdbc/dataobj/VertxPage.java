package com.github.edgar615.util.vertx.jdbc.dataobj;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * 简单的分页类，该类是一个不可变类.
 * 只会显示总数和列表，没有页码之类的条件
 *
 * @author Edgar Zhang
 * @version 1.0
 */
@DataObject(generateConverter = true)
public final class VertxPage {

  private List<JsonObject> records;

  private int totalRecords;

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    VertxPageConverter.toJson(this, jsonObject);
    return jsonObject;
  }

  public VertxPage(JsonObject jsonObject) {
    VertxPageConverter.fromJson(jsonObject, this);
  }

  private VertxPage(int totalRecords, List<JsonObject> records) {
    this.totalRecords = totalRecords;
    this.records = ImmutableList.copyOf(records);
  }

  public void setRecords(List<JsonObject> records) {
    this.records = records;
  }

  public void setTotalRecords(int totalRecords) {
    this.totalRecords = totalRecords;
  }

  /**
   * 创建一个Pagination类
   *
   * @param totalRecords 总记录数
   * @param records      当前页显示的集合
   * @return VertxPage
   */
  public static VertxPage newInstance(int totalRecords,
                                      List<JsonObject> records) {
    return new VertxPage(totalRecords, records);
  }

  public List<JsonObject> getRecords() {
    return records;
  }

  public int getTotalRecords() {
    return totalRecords;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper("VertxPage")
            .add("totalRecords", totalRecords)
            .add("records", records)
            .toString();
  }
}
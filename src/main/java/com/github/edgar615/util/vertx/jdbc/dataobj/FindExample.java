package com.github.edgar615.util.vertx.jdbc.dataobj;

import com.github.edgar615.util.search.Example;
import com.github.edgar615.util.vertx.jdbc.JdbcUtils;
import com.google.common.base.Joiner;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Created by Edgar on 2018/4/21.
 *
 * @author Edgar  Date 2018/4/21
 */
@DataObject(generateConverter = true)
public class FindExample {

  private String resource;

  private String query;

  private String sorted;

  private String fields;

  private Integer start;

  private Integer limit;

  public FindExample() {
  }

  public FindExample(JsonObject jsonObject) {
    FindExampleConverter.fromJson(jsonObject, this);
  }

  public void fromExample(Example example) {
    this.fields = Joiner.on(",")
            .join(example.fields());
    this.sorted = Joiner.on(",")
            .join(example.orderBy());
    this.query = JdbcUtils.toQuery(example);
  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    FindExampleConverter.toJson(this, jsonObject);
    return JdbcUtils.removeNull(jsonObject);
  }

  public String getResource() {
    return resource;
  }

  public void setResource(String resource) {
    this.resource = resource;
  }

  public Integer getStart() {
    return start;
  }

  public void setStart(Integer start) {
    this.start = start;
  }

  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public String getSorted() {
    return sorted;
  }

  public void setSorted(String sorted) {
    this.sorted = sorted;
  }

  public String getFields() {
    return fields;
  }

  public void setFields(String fields) {
    this.fields = fields;
  }
}

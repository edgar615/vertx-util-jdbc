package com.github.edgar615.util.vertx.jdbc.dataobj;

import com.google.common.base.Joiner;

import com.github.edgar615.util.search.Example;
import com.github.edgar615.util.vertx.jdbc.JdbcUtils;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Created by Edgar on 2018/4/21.
 *
 * @author Edgar  Date 2018/4/21
 */
@DataObject(generateConverter = true)
public class PaginationExample {

  private String resource;

  private String query;

  private String sorted;

  private String fields;

  private Integer page = 1;

  private Integer pageSize = 10;

  public PaginationExample() {
  }

  public PaginationExample(JsonObject jsonObject) {
    PaginationExampleConverter.fromJson(jsonObject, this);
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
    PaginationExampleConverter.toJson(this, jsonObject);
    return JdbcUtils.removeNull(jsonObject);
  }

  public String getResource() {
    return resource;
  }

  public void setResource(String resource) {
    this.resource = resource;
  }

  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
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

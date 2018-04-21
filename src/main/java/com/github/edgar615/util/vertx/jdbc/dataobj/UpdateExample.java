package com.github.edgar615.util.vertx.jdbc.dataobj;

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
public class UpdateExample {

  private String resource;

  private String query;

  private JsonObject data;

  public UpdateExample() {
  }

  public UpdateExample(JsonObject jsonObject) {
    UpdateExampleConverter.fromJson(jsonObject, this);
  }

  public void fromExample(Example example) {
    this.query = JdbcUtils.toQuery(example);
  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    UpdateExampleConverter.toJson(this, jsonObject);
    return JdbcUtils.removeNull(jsonObject);
  }

  public JsonObject getData() {
    return data;
  }

  public void setData(JsonObject data) {
    this.data = data;
  }

  public String getResource() {
    return resource;
  }

  public void setResource(String resource) {
    this.resource = resource;
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

}

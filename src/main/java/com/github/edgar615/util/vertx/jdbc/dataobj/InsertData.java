package com.github.edgar615.util.vertx.jdbc.dataobj;

import com.github.edgar615.util.vertx.jdbc.JdbcUtils;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Created by Edgar on 2018/4/21.
 *
 * @author Edgar  Date 2018/4/21
 */
@DataObject(generateConverter = true)
public class InsertData {

  private String resource;

  private String id;

  private JsonObject data;


  public InsertData() {
  }

  public InsertData(JsonObject jsonObject) {
    InsertDataConverter.fromJson(jsonObject, this);
  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    InsertDataConverter.toJson(this, jsonObject);
    return JdbcUtils.removeNull(jsonObject);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getResource() {
    return resource;
  }

  public void setResource(String resource) {
    this.resource = resource;
  }

  public JsonObject getData() {
    return data;
  }

  public void setData(JsonObject data) {
    this.data = data;
  }
}

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
public class DeleteById {

  private String resource;

  private String id;


  public DeleteById() {
  }

  public DeleteById(JsonObject jsonObject) {
    DeleteByIdConverter.fromJson(jsonObject, this);
  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    DeleteByIdConverter.toJson(this, jsonObject);
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

}

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
public class FindById {

  private String resource;

  private String id;

  private String fields;


  public FindById() {
  }

  public FindById(JsonObject jsonObject) {
    FindByIdConverter.fromJson(jsonObject, this);
  }

  public void fromExample(Example example) {
    this.fields = Joiner.on(",")
            .join(example.fields());
  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    FindByIdConverter.toJson(this, jsonObject);
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

  public String getFields() {
    return fields;
  }

  public void setFields(String fields) {
    this.fields = fields;
  }
}

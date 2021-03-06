/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.github.edgar615.util.vertx.jdbc.dataobj;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter for {@link com.github.edgar615.util.vertx.jdbc.dataobj.InsertData}.
 *
 * NOTE: This class has been automatically generated from the {@link com.github.edgar615.util.vertx.jdbc.dataobj.InsertData} original class using Vert.x codegen.
 */
public class InsertDataConverter {

  public static void fromJson(JsonObject json, InsertData obj) {
    if (json.getValue("data") instanceof JsonObject) {
      obj.setData(((JsonObject)json.getValue("data")).copy());
    }
    if (json.getValue("id") instanceof String) {
      obj.setId((String)json.getValue("id"));
    }
    if (json.getValue("resource") instanceof String) {
      obj.setResource((String)json.getValue("resource"));
    }
  }

  public static void toJson(InsertData obj, JsonObject json) {
    if (obj.getData() != null) {
      json.put("data", obj.getData());
    }
    if (obj.getId() != null) {
      json.put("id", obj.getId());
    }
    if (obj.getResource() != null) {
      json.put("resource", obj.getResource());
    }
  }
}
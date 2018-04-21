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
 * Converter for {@link com.github.edgar615.util.vertx.jdbc.dataobj.VertxPage}.
 *
 * NOTE: This class has been automatically generated from the {@link com.github.edgar615.util.vertx.jdbc.dataobj.VertxPage} original class using Vert.x codegen.
 */
public class VertxPageConverter {

  public static void fromJson(JsonObject json, VertxPage obj) {
    if (json.getValue("records") instanceof JsonArray) {
      java.util.ArrayList<io.vertx.core.json.JsonObject> list = new java.util.ArrayList<>();
      json.getJsonArray("records").forEach( item -> {
        if (item instanceof JsonObject)
          list.add(((JsonObject)item).copy());
      });
      obj.setRecords(list);
    }
    if (json.getValue("totalRecords") instanceof Number) {
      obj.setTotalRecords(((Number)json.getValue("totalRecords")).intValue());
    }
  }

  public static void toJson(VertxPage obj, JsonObject json) {
    if (obj.getRecords() != null) {
      JsonArray array = new JsonArray();
      obj.getRecords().forEach(item -> array.add(item));
      json.put("records", array);
    }
    json.put("totalRecords", obj.getTotalRecords());
  }
}
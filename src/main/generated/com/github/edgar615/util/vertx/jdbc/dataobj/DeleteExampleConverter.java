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
 * Converter for {@link com.github.edgar615.util.vertx.jdbc.dataobj.DeleteExample}.
 *
 * NOTE: This class has been automatically generated from the {@link com.github.edgar615.util.vertx.jdbc.dataobj.DeleteExample} original class using Vert.x codegen.
 */
public class DeleteExampleConverter {

  public static void fromJson(JsonObject json, DeleteExample obj) {
    if (json.getValue("query") instanceof String) {
      obj.setQuery((String)json.getValue("query"));
    }
    if (json.getValue("resource") instanceof String) {
      obj.setResource((String)json.getValue("resource"));
    }
  }

  public static void toJson(DeleteExample obj, JsonObject json) {
    if (obj.getQuery() != null) {
      json.put("query", obj.getQuery());
    }
    if (obj.getResource() != null) {
      json.put("resource", obj.getResource());
    }
  }
}
package com.github.edgar615.util.vertx.jdbc.dataobj;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * 分页类，该类是一个不可变类.
 *
 * @author Edgar Zhang
 * @version 1.0
 */
@DataObject(generateConverter = true)
public final class VertxPagination {

  private List<JsonObject> records;

  private int page;

  private int pageSize;

  private int totalRecords;

  private int totalPages;

  public VertxPagination(JsonObject jsonObject) {
    VertxPaginationConverter.fromJson(jsonObject, this);
  }

  private VertxPagination(int page, int pageSize, int totalRecords, List<JsonObject> records) {
    this.page = page;
    this.pageSize = pageSize;
    this.totalRecords = totalRecords;
    int pages = totalRecords / pageSize;
    if (totalRecords > pageSize * pages) {
      pages++;
    }
    this.totalPages = pages;
    this.records = ImmutableList.copyOf(records);
  }

  /**
   * 创建一个Pagination类
   *
   * @param page         页码
   * @param pageSize     每页显示的记录数
   * @param totalRecords 总记录数
   * @param records      当前页显示的集合
   * @return VertxPagination
   */
  public static VertxPagination newInstance(int page, int pageSize, int totalRecords,
                                            List<JsonObject> records) {
    return new VertxPagination(page, pageSize, totalRecords, records);
  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    VertxPaginationConverter.toJson(this, jsonObject);
    return jsonObject;
  }

  public List<JsonObject> getRecords() {
    return records;
  }

  public void setRecords(List<JsonObject> records) {
    this.records = records;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public int getTotalRecords() {
    return totalRecords;
  }

  public void setTotalRecords(int totalRecords) {
    this.totalRecords = totalRecords;
  }

  public int getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(int totalPages) {
    this.totalPages = totalPages;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper("VertxPagination")
            .add("page", page)
            .add("pageSize", pageSize)
            .add("totalRecords", totalRecords)
            .add("totalPages", totalPages)
            .add("records", records)
            .toString();
  }
}
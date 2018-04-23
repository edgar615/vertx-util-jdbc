package com.github.edgar615.util.vertx.jdbc.table;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Edgar on 2018/4/20.
 *
 * @author Edgar  Date 2018/4/20
 */
public class TableRegistry {
  private final List<Table> tables = new CopyOnWriteArrayList<>();

  private static final TableRegistry INSTANCE = new TableRegistry();

  private TableRegistry() {
  }

  public static TableRegistry instance() {
    return INSTANCE;
  }

  public List<Table> tables() {
    return tables;
  }

  public TableRegistry clear() {
    this.tables.clear();
    return this;
  }

  public TableRegistry remove(String tableName) {
      this.tables.removeIf(t -> t.getName().equalsIgnoreCase(tableName));
    return this;
  }

  public TableRegistry add(Table table) {
    this.tables.add(table);
    return this;
  }

  public TableRegistry addAll(List<Table> tables) {
    this.tables.addAll(tables);
    return this;
  }
}

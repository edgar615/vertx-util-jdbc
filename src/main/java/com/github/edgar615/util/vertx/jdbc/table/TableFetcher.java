package com.github.edgar615.util.vertx.jdbc.table;

import com.github.edgar615.mysql.mapping.Table;
import com.github.edgar615.mysql.mapping.TableMapping;
import com.github.edgar615.mysql.mapping.TableMappingOptions;
import com.github.edgar615.mysql.mapping.TableRegistry;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Edgar on 2017/5/17.
 *
 * @author Edgar  Date 2017/5/17
 */
public class TableFetcher {
  private static final Logger LOGGER = LoggerFactory.getLogger(TableFetcher.class);

  private final Vertx vertx;

  private final TableMappingOptions options;

  public TableFetcher(Vertx vertx, TableMappingOptions options) {
    this.vertx = vertx;
    this.options = options;
  }

  public void start(Vertx vertx, Future<Void> completeFuture) {
    vertx.<List<Table>>executeBlocking(f -> {
      try {
        TableMapping mapping = new TableMapping(options);
        List<Table> tables = mapping.fetchTable();
        f.complete(tables);
      } catch (Exception e) {
        LOGGER.error("Error occcured during fetch table." + e);
        f.fail(e);
      }
    }, ar -> {
      if (ar.failed()) {
        completeFuture.fail(ar.cause());
        return;
      }
      TableRegistry.instance().clear().addAll(ar.result());
      completeFuture.complete();
    });

  }

}

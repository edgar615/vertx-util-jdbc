package com.github.edgar615.util.vertx.jdbc;

import com.google.common.base.Joiner;

import com.github.edgar615.util.search.Example;
import com.github.edgar615.util.vertx.jdbc.dataobj.*;
import com.github.edgar615.util.vertx.jdbc.handler.*;
import com.github.edgar615.util.vertx.task.Task;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.SQLConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Edgar on 2018/4/25.
 *
 * @author Edgar  Date 2018/4/25
 */
public class JdbcTaskImpl implements JdbcTask {
  private final AsyncSQLClient sqlClient;

  private final Map<String, Object> context = new HashMap<>();

  private boolean done;

  private Task<Map<String, Object>> task;

  private boolean inTx;

  private SQLConnection connection;

  public JdbcTaskImpl(AsyncSQLClient sqlClient) {
    this(sqlClient, false);
  }

  public JdbcTaskImpl(AsyncSQLClient sqlClient, boolean startTx) {
    this.sqlClient = sqlClient;
    Task<Map<String, Object>> task = Task.create();
    task.complete(context);
    this.task = task.flatMap(ctx -> {
      Future<Map<String, Object>> future = Future.future();
      sqlClient.getConnection(ar -> {
        if (ar.failed()) {
          future.fail(ar.cause());
          return;
        }
        connection = ar.result();
        if (!startTx) {
          future.complete(context);
          return;
        }
        connection.setAutoCommit(false, commitResult -> {
          if (commitResult.failed()) {
            future.fail(commitResult.cause());
          } else {
            inTx = true;
            future.complete(ctx);
          }
        });
      });
      return future;
    });
  }

  @Override
  public JdbcTask startTx() {
    if (inTx) {
      return this;
    }
    this.task = task.flatMap(ctx -> {
      Future<Map<String, Object>> future = Future.future();
      connection.setAutoCommit(false, ar -> {
        if (ar.failed()) {
          future.fail(ar.cause());
        } else {
          inTx = true;
          future.complete(ctx);
        }
      });
      return future;
    });
    return this;
  }

  @Override
  public JdbcTask findByExample(String taskName, String table,
                                Function<Map<String, Object>, Example> exampleFunction) {
    this.task = task.flatMap(ctx -> {
      if (connection == null) {
        return Future.failedFuture("no connection init in task");
      }
      Future<Map<String, Object>> future = Future.future();
      FindExample findExample = new FindExample();
      findExample.setResource(table);
      findExample.fromExample(exampleFunction.apply(ctx));
      new FindByExampleHandler().handle2(connection, findExample, ar -> {
        if (ar.failed()) {
          future.fail(ar.cause());
          return;
        }
        List<JsonObject> results = ar.result();
        if (taskName != null) {
          ctx.put(taskName, results);
        }
        future.complete(ctx);
      });
      return future;
    });
    return this;
  }

  @Override
  public JdbcTask findByExample(String taskName, String table,
                                Function<Map<String, Object>, Example> exampleFunction, int start,
                                int limit) {
    this.task = task.flatMap(ctx -> {
      if (connection == null) {
        return Future.failedFuture("no connection init in task");
      }
      Future<Map<String, Object>> future = Future.future();
      FindExample findExample = new FindExample();
      findExample.setResource(table);
      findExample.fromExample(exampleFunction.apply(ctx));
      findExample.setStart(start);
      findExample.setLimit(limit);
      new FindByExampleHandler().handle2(connection, findExample, ar -> {
        if (ar.failed()) {
          future.fail(ar.cause());
          return;
        }
        List<JsonObject> results = ar.result();
        if (taskName != null) {
          ctx.put(taskName, results);
        }
        future.complete(ctx);
      });
      return future;
    });
    return this;
  }

  @Override
  public JdbcTask countByExample(String taskName, String table,
                                 Function<Map<String, Object>, Example> exampleFunction) {
    this.task = task.flatMap(ctx -> {
      if (connection == null) {
        return Future.failedFuture("no connection init in task");
      }
      Future<Map<String, Object>> future = Future.future();
      CountExample countExample = new CountExample();
      countExample.setResource(table);
      countExample.fromExample(exampleFunction.apply(ctx));
      new CountByExampleHandler().handle2(connection, countExample, ar -> {
        if (ar.failed()) {
          future.fail(ar.cause());
          return;
        }
        Integer result = ar.result();
        if (taskName != null) {
          ctx.put(taskName, result);
        }
        future.complete(ctx);
      });
      return future;
    });
    return this;
  }

  @Override
  public JdbcTask pagination(String taskName, String table,
                             Function<Map<String, Object>, Example> exampleFunction, int page,
                             int pageSize) {
    this.task = task.flatMap(ctx -> {
      if (connection == null) {
        return Future.failedFuture("no connection init in task");
      }
      Future<Map<String, Object>> future = Future.future();
      PaginationExample paginationExample = new PaginationExample();
      paginationExample.setResource(table);
      paginationExample.fromExample(exampleFunction.apply(ctx));
      paginationExample.setPage(page);
      paginationExample.setPageSize(pageSize);
      new PaginationHandler().handle2(connection, paginationExample, ar -> {
        if (ar.failed()) {
          future.fail(ar.cause());
          return;
        }
        VertxPagination result = ar.result();
        if (taskName != null) {
          ctx.put(taskName, result);
        }
        future.complete(ctx);
      });
      return future;
    });
    return this;
  }

  @Override
  public JdbcTask findFirstByExample(String taskName, String table,
                                     Function<Map<String, Object>, Example> exampleFunction) {
    this.task = task.flatMap(ctx -> {
      if (connection == null) {
        return Future.failedFuture("no connection init in task");
      }
      Future<Map<String, Object>> future = Future.future();
      FindExample findExample = new FindExample();
      findExample.setResource(table);
      findExample.fromExample(exampleFunction.apply(ctx));
      new FindByExampleHandler().handle2(connection, findExample, ar -> {
        if (ar.failed()) {
          future.fail(ar.cause());
          return;
        }
        List<JsonObject> results = ar.result();
        if (taskName != null && !results.isEmpty()) {
          ctx.put(taskName, results.get(0));
        }
        future.complete(ctx);
      });
      return future;
    });
    return this;
  }

  @Override
  public JdbcTask page(String taskName, String table,
                       Function<Map<String, Object>, Example> exampleFunction, int start,
                       int limit) {
    this.task = task.flatMap(ctx -> {
      if (connection == null) {
        return Future.failedFuture("no connection init in task");
      }
      Future<Map<String, Object>> future = Future.future();
      FindExample findExample = new FindExample();
      findExample.setResource(table);
      findExample.fromExample(exampleFunction.apply(ctx));
      findExample.setStart(start);
      findExample.setLimit(limit);
      new PageHandler().handle2(connection, findExample, ar -> {
        if (ar.failed()) {
          future.fail(ar.cause());
          return;
        }
        VertxPage result = ar.result();
        if (taskName != null) {
          ctx.put(taskName, result);
        }
        future.complete(ctx);
      });
      return future;
    });
    return this;
  }

  @Override
  public JdbcTask insert(String taskName, String table,
                         Function<Map<String, Object>, JsonObject> dataFunction) {
    this.task = task.flatMap(ctx -> {
      if (connection == null) {
        return Future.failedFuture("no connection init in task");
      }
      Future<Map<String, Object>> future = Future.future();
      InsertData insertData = new InsertData();
      insertData.setData(dataFunction.apply(ctx));
      insertData.setResource(table);
      new InsertHandler().handle2(connection, insertData, ar -> {
        if (ar.failed()) {
          future.fail(ar.cause());
          return;
        }
        JsonObject result = ar.result();
        if (taskName != null) {
          ctx.put(taskName, result.getValue("result"));
        }

        future.complete(ctx);
      });
      return future;
    });
    return this;
  }

  @Override
  public JdbcTask deleteById(String taskName, String table,
                             Function<Map<String, Object>, Object> idExtractor) {
    this.task = task.flatMap(ctx -> {
      if (connection == null) {
        return Future.failedFuture("no connection init in task");
      }
      Future<Map<String, Object>> future = Future.future();
      DeleteById deleteById = new DeleteById();
      deleteById.setResource(table);
      Object id = idExtractor.apply(ctx);
      deleteById.setId(id.toString());
      new DeleteByIdHandler().handle2(connection, deleteById, ar -> {
        if (ar.failed()) {
          future.fail(ar.cause());
          return;
        }
        Integer result = ar.result();
        if (taskName != null) {
          ctx.put(taskName, result);
        }
        future.complete(ctx);
      });
      return future;
    });
    return this;
  }

  @Override
  public JdbcTask batchDeleteById(String taskName, String table,
                                  Function<Map<String, Object>, List<Object>> idExtractor) {
    this.task = task.flatMap(ctx -> {
      if (connection == null) {
        return Future.failedFuture("no connection init in task");
      }
      Future<Map<String, Object>> future = Future.future();
      List<Object> idList = idExtractor.apply(ctx);
      List<Future> futures = new ArrayList<Future>();
      for (Object id : idList) {
        Future<Integer> deleteFuture = Future.future();
        futures.add(deleteFuture);
        DeleteById deleteById = new DeleteById();
        deleteById.setResource(table);
        deleteById.setId(id.toString());
        new DeleteByIdHandler().handle2(connection, deleteById, ar -> {
          if (ar.failed()) {
            deleteFuture.fail(ar.cause());
            return;
          }
          Integer result = ar.result();
          deleteFuture.complete(result);
        });
      }
      CompositeFuture.all(futures)
              .setHandler(ar -> {
                if (ar.failed()) {
                  future.fail(ar.cause());
                  return;
                }
                if (taskName != null) {
                  ctx.put(taskName, futures.size());
                }
                future.complete(ctx);
              });
      return future;
    });
    return this;
  }

  @Override
  public JdbcTask batchDeleteById(String taskName, String table, List<Object> idList) {
    return null;
  }

  @Override
  public JdbcTask deleteByExample(String taskName, String table, Example example) {
    this.task = task.flatMap(ctx -> {
      if (connection == null) {
        return Future.failedFuture("no connection init in task");
      }
      Future<Map<String, Object>> future = Future.future();
      DeleteExample deleteExample = new DeleteExample();
      deleteExample.setResource(table);
      deleteExample.fromExample(example);
      new DeleteByExampleHandler().handle2(connection, deleteExample, ar -> {
        if (ar.failed()) {
          future.fail(ar.cause());
          return;
        }
        Integer result = ar.result();
        if (taskName != null) {
          ctx.put(taskName, result);
        }
        future.complete(ctx);
      });
      return future;
    });
    return this;
  }

  @Override
  public JdbcTask deleteByExample(String taskName, String table,
                                  Function<Map<String, Object>, Example> exampleFunction) {
    return null;
  }

  @Override
  public JdbcTask updateById(String taskName, String table, JsonObject data, Object id) {
    this.task = task.flatMap(ctx -> {
      if (connection == null) {
        return Future.failedFuture("no connection init in task");
      }
      Future<Map<String, Object>> future = Future.future();
      UpdateById updateById = new UpdateById();
      updateById.setResource(table);
      updateById.setData(data);
      updateById.setId(id.toString());
      new UpdateByIdHandler().handle2(connection, updateById, ar -> {
        if (ar.failed()) {
          future.fail(ar.cause());
          return;
        }
        Integer result = ar.result();
        if (taskName != null) {
          ctx.put(taskName, result);
        }
        future.complete(ctx);
      });
      return future;
    });
    return this;
  }

  @Override
  public JdbcTask updateById(String taskName, String table,
                             Function<Map<String, Object>, JsonObject> dataFunction,
                             Function<Map<String, Object>, Object> idFunction) {
    return null;
  }

  @Override
  public JdbcTask updateByExample(String taskName, String table, JsonObject data, Example example) {
    this.task = task.flatMap(ctx -> {
      if (connection == null) {
        return Future.failedFuture("no connection init in task");
      }
      Future<Map<String, Object>> future = Future.future();
      UpdateExample updateExample = new UpdateExample();
      updateExample.setResource(table);
      updateExample.setData(data);
      updateExample.fromExample(example);
      new UpdateByExampleHandler().handle2(connection, updateExample, ar -> {
        if (ar.failed()) {
          future.fail(ar.cause());
          return;
        }
        Integer result = ar.result();
        if (taskName != null) {
          ctx.put(taskName, result);
        }
        future.complete(ctx);
      });
      return future;
    });
    return this;
  }

  @Override
  public JdbcTask updateByExample(String taskName, String table,
                                  Function<Map<String, Object>, JsonObject> dataFunction,
                                  Function<Map<String, Object>, Example> exampleFunction) {
    return null;
  }

  @Override
  public JdbcTask findById(String taskName, String table,
                           Function<Map<String, Object>, Object> idExtractor,
                           List<String> fields) {
    this.task = task.flatMap(ctx -> {
      if (connection == null) {
        return Future.failedFuture("no connection init in task");
      }
      Future<Map<String, Object>> future = Future.future();
      FindById findById = new FindById();
      findById.setResource(table);
      Object id = idExtractor.apply(ctx);
      findById.setId(id.toString());
      if (fields != null) {
        findById.setFields(Joiner.on(",").join(fields));
      }
      new FindByIdHandler().handle2(connection, findById, ar -> {
        if (ar.failed()) {
          future.fail(ar.cause());
          return;
        }
        JsonObject results = ar.result();
        if (taskName != null && results != null) {
          ctx.put(taskName, results);
        }
        future.complete(ctx);
      });
      return future;
    });
    return this;
  }

  @Override
  public JdbcTask findById(String taskName, String table, Object id, List<String> fields) {
    this.task = task.flatMap(ctx -> {
      if (connection == null) {
        return Future.failedFuture("no connection init in task");
      }
      Future<Map<String, Object>> future = Future.future();
      FindById findById = new FindById();
      findById.setResource(table);
      findById.setId(id.toString());
      findById.setFields(Joiner.on(",").join(fields));
      new FindByIdHandler().handle2(connection, findById, ar -> {
        if (ar.failed()) {
          future.fail(ar.cause());
          return;
        }
        JsonObject results = ar.result();
        if (taskName != null && results != null) {
          ctx.put(taskName, results);
        }
        future.complete(ctx);
      });
      return future;
    });
    return this;
  }

  @Override
  public JdbcTask batchFindById(String taskName, String table, List<Object> idList,
                                List<String> fields) {
    return null;
  }

  @Override
  public JdbcTask batchFindById(String taskName, String table,
                                Function<Map<String, Object>, List<Object>> idFunction,
                                List<String> fields) {
    return null;
  }

  public <T> Future<T> done(Function<Map<String, Object>, T> function) {
    Future<T> future = Future.future();
    this.task.andThen(ctx -> {
      //提交事务，清理ctx
      if (!inTx) {
        try {
          T result = function.apply(ctx);
          ctx.clear();
          done = true;
          future.complete(result);
        } catch (Exception e) {
          future.fail(e);
        }
      }
      connection.commit(commitResult -> {
        if (commitResult.failed()) {
          //todo log
          future.fail(commitResult.cause());
        } else {
          try {
            T result = function.apply(ctx);
            done = true;
            future.complete(result);
          } catch (Exception e) {
            future.fail(e);
          }
        }
      });
    }).onFailure(e -> {
      System.out.println(context);
      done = true;
      //回滚事务
      if (!inTx) {
        //do nothing
        future.fail(e);
        return;
      }
      //与提交事务不同，这里不关心事务回滚结果
      connection.rollback(commitResult -> {
        done = true;
        future.fail(e);
      });
    });
    return future;
  }

}

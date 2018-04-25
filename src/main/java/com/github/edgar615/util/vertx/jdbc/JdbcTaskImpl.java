package com.github.edgar615.util.vertx.jdbc;

import com.github.edgar615.util.vertx.task.Task;
import io.vertx.core.Future;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.SQLConnection;

import java.util.HashMap;
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

  JdbcTaskImpl(AsyncSQLClient sqlClient) {
    this(sqlClient, false);
  }

  JdbcTaskImpl(AsyncSQLClient sqlClient, boolean startTx) {
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
        //回收connection
        connection.close(ar -> {
          //log
        });
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
        //回收connection
        connection.close(ar -> {
          //log
        });
      });
    }).onFailure(e -> {
      System.out.println(context);
      done = true;
      //回滚事务
      if (!inTx) {
        //do nothing
        future.fail(e);
        //回收connection
        connection.close(ar -> {
          //log
        });
        return;
      }
      //与提交事务不同，这里不关心事务回滚结果
      connection.rollback(commitResult -> {
        done = true;
        future.fail(e);
        //回收connection
        connection.close(ar -> {
          //log
        });
      });
    });
    return future;
  }

  @Override
  public <T> JdbcTask execute(String taskName, Function<Map<String, Object>, JdbcAction<T>> actionFunction) {
    this.task = task.flatMap(ctx -> {
      if (connection == null) {
        return Future.failedFuture("no connection init in task");
      }
      Future<Map<String, Object>> future = Future.future();
      JdbcAction<T> action = null;
      try {
        action = actionFunction.apply(ctx);
      } catch (Exception e) {
        future.fail(e);
        return future;
      }
      action.execute(connection, ar -> {
        if (ar.failed()) {
          future.fail(ar.cause());
          return;
        }
        if (taskName != null) {
          ctx.put(taskName, ar.result());
        }
        future.complete(ctx);
      });
      return future;
    });
    return this;
  }

}

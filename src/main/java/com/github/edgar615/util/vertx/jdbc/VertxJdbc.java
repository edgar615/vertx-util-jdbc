package com.github.edgar615.util.vertx.jdbc;

import com.github.edgar615.util.db.Page;
import com.github.edgar615.util.db.Pagination;
import com.github.edgar615.util.db.Persistent;
import com.github.edgar615.util.search.Example;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Edgar on 2018/4/18.
 *
 * @author Edgar  Date 2018/4/18
 */
public interface VertxJdbc {

  void close();

  void close(Handler<AsyncResult<Void>> handler);

  /**
   * 新增数据.
   *
   * @param persistent 持久化对象
   * @param handler    如果主键是自增，返回主键值，否则返回0
   * @param <ID>       主键类型
   */

  <ID> void insert(Persistent<ID> persistent, Handler<AsyncResult<Void>> handler);

  /**
   * 新增数据.
   *
   * @param persistent 持久化对象
   * @param handler    如果主键是自增，返回主键值，否则返回0
   */

  void insertAndGenerateKey(Persistent<Integer> persistent, Handler<AsyncResult<Integer>> handler);

  /**
   * 根据主键删除.
   *
   * @param elementType 持久化对象
   * @param id          主键
   * @param handler     删除的记录数
   * @param <ID>        主键类型
   * @param <T>         持久化对象
   */
  <ID, T extends Persistent<ID>> void deleteById(Class<T> elementType, ID id,
                                                 Handler<AsyncResult<Integer>> handler);

  /**
   * 根据条件删除.
   *
   * @param elementType 持久化对象
   * @param example     查询条件
   * @param handler     删除的记录数
   * @param <ID>        主键类型
   * @param <T>         持久化对象
   */
  <ID, T extends Persistent<ID>> void deleteByExample(Class<T> elementType,
                                                      Example example,
                                                      Handler<AsyncResult<Integer>>
                                                              handler);

  /**
   * 根据主键更新，忽略实体中的null.
   *
   * @param persistent 持久化对象
   * @param id         主键
   * @param handler    修改的记录数
   * @param <ID>       主键类型
   */
  default <ID> void updateById(Persistent<ID> persistent, ID id,
                               Handler<AsyncResult<Integer>> handler) {
    updateById(persistent, new HashMap<>(), new ArrayList<>(), id, handler);
  }

  /**
   * 根据主键更新，忽略实体中的null
   *
   * @param persistent 持久化对象
   * @param addOrSub   需要做增加或者减去的字段，value为正数表示增加，负数表示减少
   * @param nullFields 需要设为null的字段
   * @param id         主键ID
   * @param handler    修改的记录数
   * @param <ID>       主键类型
   * @return
   */
  <ID> void updateById(Persistent<ID> persistent,
                      Map<String, Integer> addOrSub,
                      List<String> nullFields,
                      ID id,
                      Handler<AsyncResult<Integer>> handler);


  /**
   * 根据条件更新，忽略实体中的null.
   *
   * @param persistent 持久化对象
   * @param example    查询条件
   * @param handler    修改的记录数
   * @param <ID>       条件集合
   */
  default <ID> void updateByExample(Persistent<ID> persistent,
                                    Example example,
                                    Handler<AsyncResult<Integer>> handler) {
    updateByExample(persistent, new HashMap<>(), new ArrayList<>(), example, handler);
  }

  /**
   * 根据条件更新，忽略实体中的null
   *
   * @param persistent 持久化对象
   * @param addOrSub   需要做增加或者减去的字段，value为正数表示增加，负数表示减少
   * @param nullFields 需要设为null的字段
   * @param example    查询条件
   * @param handler    修改的记录数
   * @param <ID>       主键类型
   * @return
   */
  <ID> void updateByExample(Persistent<ID> persistent,
                           Map<String, Integer> addOrSub,
                           List<String> nullFields, Example example,
                           Handler<AsyncResult<Integer>> handler);

  /**
   * 根据主键查找.
   *
   * @param elementType 持久化对象
   * @param id          主键
   * @param fields      返回的属性列表
   * @param function    将jsonobject转换为实体的转换类
   * @param handler     实体
   * @param <ID>        主键类型
   * @param <T>         持久化对象
   */
  <ID, T extends Persistent<ID>> void findById(Class<T> elementType, ID id,
                                               List<String> fields,
                                               Function<JsonObject, T> function,
                                               Handler<AsyncResult<T>> handler);

  /**
   * 根据条件查找.
   *
   * @param elementType 持久化对象,
   * @param example     查询参数的定义，包括查询条件、排序规则等
   * @param function    将jsonobject转换为实体的转换类
   * @param handler     实体
   * @param <ID>        主键类型
   * @param <T>         持久化对象
   */
  <ID, T extends Persistent<ID>> void findByExample(Class<T> elementType,
                                                    Example example,
                                                    Function<JsonObject, T> function,
                                                    Handler<AsyncResult<List<T>>> handler);

  /**
   * 根据条件查找.
   *
   * @param elementType 持久化对象
   * @param example     查询参数的定义，包括查询条件、排序规则等
   * @param start       开始索引
   * @param limit       查询数量
   * @param function    将jsonobject转换为实体的转换类
   * @param handler     实体
   * @param <ID>        主键类型
   * @param <T>         持久化对象
   */
  <ID, T extends Persistent<ID>> void findByExample(Class<T> elementType, Example example,
                                                    int start, int limit,
                                                    Function<JsonObject, T> function,
                                                    Handler<AsyncResult<List<T>>> handler);

  /**
   * 根据条件查找.
   *
   * @param elementType 持久化对象
   * @param example     查询条件
   * @param handler     总数
   * @param <ID>        主键类型
   * @param <T>         持久化对象
   * @return
   */
  <ID, T extends Persistent<ID>> void countByExample(Class<T> elementType,
                                                     Example example,
                                                     Handler<AsyncResult<Integer>> handler);

  static VertxJdbc create(SQLConnection connection) {
    return new VertxJdbcImpl(connection);
  }

  /**
   * 分页查找.
   *
   * @param elementType 持久化对象
   * @param example     查询参数的定义，包括查询条件、排序规则等
   * @param page        页码
   * @param pageSize    每页数量
   * @param <ID>        主键类型
   * @param <T>         持久化对象
   * @return
   */
  default <ID, T extends Persistent<ID>> void pagination(Class<T> elementType,
                                                         Example example,
                                                         final int page, final int pageSize,
                                                         Function<JsonObject, T> function,
                                                         Handler<AsyncResult<Pagination<T>>>
                                                                 handler) {
    try {
      Preconditions.checkArgument(page > 0, "page must greater than 0");
      Preconditions.checkArgument(pageSize > 0, "pageSize must greater than 0");
      Preconditions.checkNotNull(function);
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
      return;
    }
    //查询总数
    Future<Integer> countFuture = Future.future();
    countByExample(elementType, example, countFuture.completer());
    countFuture.compose(totalRecords -> {
      Future<Pagination<T>> pageFuture = Future.future();
      if (totalRecords == 0) {
        Pagination<T> pagination =
                Pagination.newInstance(1, pageSize, totalRecords, Lists.newArrayList());
        pageFuture.complete(pagination);
        return pageFuture;
      }
      int pageCount = totalRecords / pageSize;
      if (totalRecords > pageSize * pageCount) {
        pageCount++;
      }
      int offset = (page - 1) * pageSize;
      if (pageCount < page) {
        offset = (pageCount - 1) * pageSize;
      }
      findByExample(elementType, example, offset, pageSize, function, far -> {
        if (far.failed()) {
          pageFuture.fail(far.cause());
          return;
        }
        List<T> records = far.result();
        Pagination<T> pagination =
                Pagination.newInstance(1, pageSize, totalRecords, records);
        pageFuture.complete(pagination);
        ;
      });
      return pageFuture;
    }).setHandler(handler);
  }

  /**
   * 根据条件查找.
   *
   * @param elementType 持久化对象
   * @param example     查询参数的定义，包括查询条件、排序规则等
   * @param function    将jsonobject转换为实体的转换类
   * @param handler     实体
   * @param <ID>        主键类型
   * @param <T>         持久化对象
   * @return
   */
  default <ID, T extends Persistent<ID>> void findFirstByExample(Class<T> elementType,
                                                                 Example example,
                                                                 Function<JsonObject, T> function,
                                                                 Handler<AsyncResult<T>> handler) {
    findByExample(elementType, example, function, ar -> {
      if (ar.failed()) {
        handler.handle(Future.failedFuture(ar.cause()));
        return;
      }
      if (ar.result() == null || ar.result().isEmpty()) {
        handler.handle(Future.succeededFuture(null));
        return;
      }
      handler.handle(Future.succeededFuture(ar.result().get(0)));
    });
  }

  /**
   * 根据主键查找.
   *
   * @param elementType 持久化对象
   * @param id          主键
   * @param function    将jsonobject转换为实体的转换类
   * @param handler     实体
   * @param <ID>        主键类型
   * @param <T>         持久化对象
   * @return
   */
  default <ID, T extends Persistent<ID>> void findById(Class<T> elementType, ID id,
                                                       Function<JsonObject, T> function,
                                                       Handler<AsyncResult<T>> handler) {
    findById(elementType, id, Lists.newArrayList(), function, handler);
  }

  /**
   * 根据条件查找，并返回总数.
   *
   * @param elementType 持久化对象
   * @param example     查询参数的定义，包括查询条件、排序规则等
   * @param start       开始索引
   * @param limit       查询数量
   * @param <ID>        主键类型
   * @param <T>         持久化对象
   * @return
   */
  default <ID, T extends Persistent<ID>> void page(Class<T> elementType, Example example,
                                                   int start, int limit,
                                                   Function<JsonObject, T> function,
                                                   Handler<AsyncResult<Page<T>>> handler) {
    findByExample(elementType, example, start, limit, function, ar -> {
      if (ar.failed()) {
        handler.handle(Future.failedFuture(ar.cause()));
        return;
      }
      List<T> records = ar.result();
      //如果records的数量小于limit，说明已经没有记录，直接计算总数
      if (records.size() > 0 && records.size() < limit) {
        int total = start + records.size();
        handler.handle(Future.succeededFuture(Page.newInstance(total, records)));
        return;
      }
      countByExample(elementType, example, car -> {
        if (car.failed()) {
          handler.handle(Future.failedFuture(car.cause()));
          return;
        }
        int totalRecords = car.result();
        handler.handle(Future.succeededFuture(Page.newInstance(totalRecords, records)));
      });
    });

  }
}

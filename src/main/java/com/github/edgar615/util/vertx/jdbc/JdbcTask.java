package com.github.edgar615.util.vertx.jdbc;

import com.github.edgar615.util.search.Example;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Edgar on 2018/4/25.
 *
 * @author Edgar  Date 2018/4/25
 */
public interface JdbcTask {

  <T> Future<T> done(Function<Map<String, Object>, T> function);

  /**
   * 新增数据.将结果保存在上下文 name: 主键
   *
   * @param taskName     上下文中的结果名
   * @param table        表名
   * @param dataFunction 根据上下文生成持久化对象
   * @return
   */
  JdbcTask insert(String taskName, String table,
                  Function<Map<String, Object>, JsonObject> dataFunction);

  /**
   * 根据主键删除,在上下文中存储结果.
   *
   * @param taskName    上下文中的结果名
   * @param table       表名
   * @param idExtractor 从上下文生成主键
   * @return
   */
  JdbcTask deleteById(String taskName, String table,
                      Function<Map<String, Object>, Object> idExtractor);

  /**
   * 根据主键删除,在上下文中存储结果.
   *
   * @param taskName    上下文中的结果名
   * @param table       表名
   * @param idExtractor 从上下文生成主键的列表
   * @return
   */
  JdbcTask batchDeleteById(String taskName, String table,
                           Function<Map<String, Object>, List<Object>> idExtractor);

  /**
   * 根据条件删除.在上下文中存储结果
   *
   * @param taskName 上下文中的结果名
   * @param table    表名
   * @param example  查询条件
   * @return
   */
  JdbcTask deleteByExample(String taskName, String table, Example example);

  /**
   * 根据条件删除.在上下文中存储结果
   *
   * @param taskName        上下文中的结果名
   * @param table           表名
   * @param exampleFunction 从上下文生成查询条件
   * @return
   */
  JdbcTask deleteByExample(String taskName, String table,
                           Function<Map<String, Object>, Example> exampleFunction);

  /**
   * 根据主键更新，忽略实体中的null.在上下文中存储结果.
   *
   * @param taskName 上下文中的结果名
   * @param table    表名
   * @param data     持久化对象
   * @param id       主键
   * @return
   */
  JdbcTask updateById(String taskName, String table, JsonObject data, Object id);

  /**
   * 根据主键更新，忽略实体中的null.在上下文中存储结果.
   *
   * @param taskName     上下文中的结果名
   * @param table        表名
   * @param dataFunction 从上下文中提取持久化对象
   * @param idFunction   从上下文中提取主键
   * @return
   */
  JdbcTask updateById(String taskName, String table,
                      Function<Map<String, Object>, JsonObject> dataFunction,
                      Function<Map<String, Object>, Object> idFunction);

  /**
   * 根据条件更新，忽略实体中的null.在上下文中存储结果.
   *
   * @param taskName 上下文中的结果名
   * @param table    表名
   * @param data     持久化对象
   * @param example  查询条件
   * @return
   */
  JdbcTask updateByExample(String taskName, String table, JsonObject data, Example example);

  /**
   * 根据条件更新，忽略实体中的null.在上下文中存储结果.
   *
   * @param taskName        上下文中的结果名
   * @param table           表名
   * @param dataFunction    从上下文中提取持久化对象
   * @param exampleFunction 从上下文中提取查询条件
   * @return
   */
  JdbcTask updateByExample(String taskName, String table,
                           Function<Map<String, Object>, JsonObject> dataFunction,
                           Function<Map<String, Object>, Example> exampleFunction);

  /**
   * 根据主键查找.在上下文中存储结果.
   *
   * @param taskName    上下文中的结果名
   * @param table       表名
   * @param idExtractor 从上下文中获取主键的方法
   * @param fields      返回的属性列表
   * @return
   */
  JdbcTask findById(String taskName, String table,
                    Function<Map<String, Object>, Object> idExtractor,
                    List<String> fields);

  /**
   * 根据主键查找.在上下文中存储结果.
   *
   * @param taskName 上下文中的结果名
   * @param table    表名
   * @param id       主键
   * @param fields   返回的属性列表
   * @return
   */
  JdbcTask findById(String taskName, String table, Object id, List<String> fields);

  /**
   * 根据主键查找.在上下文中存储结果.
   *
   * @param taskName 上下文中的结果名
   * @param table    表名
   * @param idList   主键
   * @param fields   返回的属性列表
   * @return
   */
  JdbcTask batchFindById(String taskName, String table, List<Object> idList, List<String> fields);

  /**
   * 根据主键查找.在上下文中存储结果.
   *
   * @param taskName   上下文中的结果名
   * @param table      表名
   * @param idFunction 从上下文中提取主键列表
   * @param fields     返回的属性列表
   * @return
   */
  JdbcTask batchFindById(String taskName, String table,
                         Function<Map<String, Object>, List<Object>> idFunction,
                         List<String> fields);

  /**
   * 开启事务
   *
   * @return
   */
  JdbcTask startTx();

  /**
   * 根据条件查找.在上下文中存储结果.
   *
   * @param taskName        上下文中的结果名
   * @param table           表名,
   * @param exampleFunction 从上下文中读取查询条件
   * @return
   */
  JdbcTask findByExample(String taskName, String table,
                         Function<Map<String, Object>, Example> exampleFunction);

  /**
   * 根据条件查找.在上下文中存储结果.
   *
   * @param taskName        上下文中的结果名
   * @param table           表名,
   * @param start           开始索引
   * @param limit           查询数量
   * @param exampleFunction 从上下文中读取查询条件
   * @return
   */
  JdbcTask findByExample(String taskName, String table,
                         Function<Map<String, Object>, Example> exampleFunction,
                         int start, int limit);

  /**
   * 根据条件查找.在上下文中存储结果.
   *
   * @param taskName        上下文中的结果名
   * @param table           表名
   * @param exampleFunction 从上下文中提取查询条件
   * @return
   */
  JdbcTask countByExample(String taskName, String table,
                          Function<Map<String, Object>, Example> exampleFunction);

  /**
   * 分页查找.在上下文中存储结果.
   *
   * @param taskName        上下文中的结果名
   * @param table           表名
   * @param exampleFunction 从上下文中提取查询条件
   * @param page            页码
   * @param pageSize        每页数量
   * @return
   */
  JdbcTask pagination(String taskName, String table,
                      Function<Map<String, Object>, Example> exampleFunction,
                      final int page, final int pageSize);

  /**
   * 根据条件查找.在上下文中存储结果.
   *
   * @param taskName        上下文中的结果名
   * @param table           表名
   * @param exampleFunction 从上下文中提取查询条件
   * @return
   */
  JdbcTask findFirstByExample(String taskName, String table,
                              Function<Map<String, Object>, Example> exampleFunction);

  /**
   * 根据条件查找，并返回总数.在上下文中保存结果.
   *
   * @param taskName        上下文中的结果名
   * @param table           表名
   * @param exampleFunction 从上下文中提取查询条件
   * @param start           开始索引
   * @param limit           查询数量
   * @return
   */
  JdbcTask page(String taskName, String table,
                Function<Map<String, Object>, Example> exampleFunction,
                int start,
                int limit);

  /**
   * 根据主键删除,在上下文中存储结果.
   *
   * @param taskName 上下文中的结果名
   * @param table    表名
   * @param id       主键
   * @return
   */
  default JdbcTask deleteById(String taskName, String table, Object id) {
    return deleteById(taskName, table, ctx -> id);
  }

  /**
   * 根据主键删除,在上下文中存储结果.
   *
   * @param taskName 上下文中的结果名
   * @param table    表名
   * @param idList   主键的列表
   * @return
   */
  default JdbcTask batchDeleteById(String taskName, String table, List<Object> idList) {
    return batchDeleteById(taskName, table, ctx -> idList);
  }

  /**
   * 根据条件查找，并返回总数.在上下文中保存结果.
   *
   * @param taskName 上下文中的结果名
   * @param table    表名
   * @param example  查询参数的定义，包括查询条件、排序规则等
   * @param start    开始索引
   * @param limit    查询数量
   * @return
   */
  default JdbcTask page(String taskName, String table, Example example,
                        int start, int limit) {
    return page(taskName, table, ctx -> example, start, limit);
  }

  /**
   * 根据条件查找.在上下文中存储结果.
   *
   * @param taskName 上下文中的结果名
   * @param table    表名
   * @param example  查询参数的定义，包括查询条件、排序规则等
   * @return
   */
  default JdbcTask findFirstByExample(String taskName, String table, Example example) {
    return findFirstByExample(taskName, table, ctx -> example);
  }

  /**
   * 分页查找.在上下文中存储结果.
   *
   * @param taskName 上下文中的结果名
   * @param table    表名
   * @param example  查询参数的定义，包括查询条件、排序规则等
   * @param page     页码
   * @param pageSize 每页数量
   * @return
   */
  default JdbcTask pagination(String taskName, String table, Example example,
                              final int page, final int pageSize) {
    return pagination(table, ctx -> example, page, pageSize);
  }

  /**
   * 根据条件查找.在上下文中存储结果.
   *
   * @param taskName 上下文中的结果名
   * @param table    表名
   * @param example  查询条件
   * @return
   */
  default JdbcTask countByExample(String taskName, String table, Example example) {
    return countByExample(taskName, table, ctx -> example);
  }

  /**
   * 根据条件查找.在上下文中存储结果.
   *
   * @param taskName 上下文中的结果名
   * @param table    表名,
   * @param start    开始索引
   * @param limit    查询数量
   * @param example  查询参数的定义，包括查询条件、排序规则等
   * @return
   */
  default JdbcTask findByExample(String taskName, String table, Example example,
                                 int start, int limit) {
    return findByExample(taskName, table, ctx -> example, start, limit);
  }

  /**
   * 新增数据.将结果保存在上下文 name: 主键
   *
   * @param taskName 上下文中的结果名
   * @param table    表名
   * @param data     持久化对象
   * @return
   */
  default JdbcTask insert(String taskName, String table, JsonObject data) {
    return insert(taskName, table, ctx -> data);
  }

  /**
   * 根据条件查找.在上下文中存储结果.
   *
   * @param taskName 上下文中的结果名
   * @param table    表名,
   * @param example  查询参数的定义，包括查询条件、排序规则等
   * @return
   */
  default JdbcTask findByExample(String taskName, String table, Example example) {
    return findByExample(taskName, table, ctx -> example);
  }

  /**
   * 根据条件查找，并返回总数.不在上下文中保存结果.
   *
   * @param table           表名
   * @param exampleFunction 从上下文中提取查询条件
   * @param start           开始索引
   * @param limit           查询数量
   * @return
   */
  default JdbcTask page(String table,
                        Function<Map<String, Object>, Example> exampleFunction,
                        int start,
                        int limit) {
    return page(null, table, exampleFunction, start, limit);
  }

  /**
   * 根据条件查找.不在上下文中存储结果.
   *
   * @param table           表名
   * @param exampleFunction 从上下文中提取查询条件
   * @return
   */
  default JdbcTask findFirstByExample(String table,
                                      Function<Map<String, Object>, Example> exampleFunction) {
    return findFirstByExample(null, table, exampleFunction);
  }

  /**
   * 分页查找.不在上下文中存储结果.
   *
   * @param table           表名
   * @param exampleFunction 从上下文中提取查询条件
   * @param page            页码
   * @param pageSize        每页数量
   * @return
   */
  default JdbcTask pagination(String table,
                              Function<Map<String, Object>, Example> exampleFunction,
                              final int page, final int pageSize) {
    return pagination(null, table, exampleFunction, page, pageSize);
  }

  /**
   * 根据条件查找.不在上下文中存储结果.
   *
   * @param table           表名,
   * @param start           开始索引
   * @param limit           查询数量
   * @param exampleFunction 从上下文中读取查询条件
   * @return
   */
  default JdbcTask findByExample(String table,
                                 Function<Map<String, Object>, Example> exampleFunction,
                                 int start, int limit) {
    return findByExample(null, table, exampleFunction, start, limit);
  }

  /**
   * 根据条件查找.不在上下文中存储结果.
   *
   * @param table           表名
   * @param exampleFunction 从上下文中提取查询条件
   * @return
   */
  default JdbcTask countByExample(String table,
                                  Function<Map<String, Object>, Example> exampleFunction) {
    return countByExample(null, table, exampleFunction);
  }

  /**
   * 根据主键查找.不在上下文中存储结果.
   *
   * @param table  表名
   * @param idList 主键
   * @param fields 返回的属性列表
   * @return
   */
  default JdbcTask batchFindById(String table, List<Object> idList, List<String> fields) {
    return batchFindById(null, idList, fields);
  }

  /**
   * 根据主键查找.不在上下文中存储结果.
   *
   * @param table      表名
   * @param idFunction 从上下文中提取主键列表
   * @param fields     返回的属性列表
   * @return
   */
  default JdbcTask batchFindById(String table,
                                 Function<Map<String, Object>, List<Object>> idFunction,
                                 List<String> fields) {
    return batchFindById(null, idFunction, fields);
  }

  /**
   * 根据条件查找.不在上下文中存储结果.
   *
   * @param table           表名,
   * @param exampleFunction 从上下文中读取查询条件
   * @return
   */
  default JdbcTask findByExample(String table,
                                 Function<Map<String, Object>, Example> exampleFunction) {
    return findByExample(null, table, exampleFunction);
  }

  /**
   * 根据条件更新，忽略实体中的null.不在上下文中存储结果.
   *
   * @param table           表名
   * @param dataFunction    从上下文中提取持久化对象
   * @param exampleFunction 从上下文中提取查询条件
   * @return
   */
  default JdbcTask updateByExample(String table,
                                   Function<Map<String, Object>, JsonObject> dataFunction,
                                   Function<Map<String, Object>, Example> exampleFunction) {
    return updateByExample(null, table, dataFunction, exampleFunction);
  }

  /**
   * 根据主键更新，忽略实体中的null.不在上下文中存储结果.
   *
   * @param table        表名
   * @param dataFunction 从上下文中提取持久化对象
   * @param idFunction   从上下文中提取主键
   * @return
   */
  default JdbcTask updateById(String table,
                              Function<Map<String, Object>, JsonObject> dataFunction,
                              Function<Map<String, Object>, Object> idFunction) {
    return updateById(null, table, dataFunction, idFunction);
  }

  /**
   * 根据主键删除,不在上下文中存储结果.
   *
   * @param table       表名
   * @param idExtractor 从上下文生成主键的列表
   * @return
   */
  default JdbcTask batchDeleteById(String table,
                                   Function<Map<String, Object>, List<Object>> idExtractor) {
    return batchDeleteById(null, table, idExtractor);
  }

  /**
   * 根据主键删除,不在上下文中存储结果.
   *
   * @param table  表名
   * @param idList 主键的列表
   * @return
   */
  default JdbcTask batchDeleteById(String table, List<Object> idList) {
    return batchDeleteById(null, table, idList);
  }

  /**
   * 根据主键更新，忽略实体中的null.不在上下文中存储结果.
   *
   * @param table      表名
   * @param data       持久化对象
   * @param idFunction 从上下文生成主键
   */
  default JdbcTask updateById(String table, JsonObject data,
                              Function<Map<String, Object>, Object> idFunction) {
    return updateById(null, table, data, idFunction);
  }

  /**
   * 新增数据.不保存结果
   *
   * @param table        表名
   * @param dataFunction 根据上下文生成持久化对象
   */
  default JdbcTask insert(String table,
                          Function<Map<String, Object>, JsonObject> dataFunction) {
    return insert(null, table, dataFunction);
  }

  /**
   * 根据条件删除.不在上下文中存储结果
   *
   * @param table           表名
   * @param exampleFunction 从上下文生成查询条件
   */
  default JdbcTask deleteByExample(String table,
                                   Function<Map<String, Object>, Example> exampleFunction) {
    return deleteByExample(null, table, exampleFunction);
  }

  /**
   * 根据ID删除.不在上下文中存储结果
   *
   * @param table       表名
   * @param idExtractor 从上下文生成ID
   * @return
   */
  default JdbcTask deleteById(String table,
                              Function<Map<String, Object>, Object> idExtractor) {
    return deleteById(null, table, idExtractor);
  }

  /**
   * 新增数据.不在上下文中存储结果.
   *
   * @param table 表名
   * @param data  持久化对象
   * @return
   */
  default JdbcTask insert(String table, JsonObject data) {
    return insert(null, table, data);
  }

  /**
   * 根据主键删除,.不在上下文中存储结果.
   *
   * @param table 表名
   * @param id    主键
   * @return
   */
  default JdbcTask deleteById(String table, Object id) {
    return deleteById(null, table, id);
  }

  /**
   * 根据条件删除.不在上下文中存储结果
   *
   * @param table   表名
   * @param example 查询条件
   */
  default JdbcTask deleteByExample(String table, Example example) {
    return deleteByExample(null, table, example);
  }

  /**
   * 根据主键更新，忽略实体中的null.不在上下文中存储结果.
   *
   * @param table 表名
   * @param data  持久化对象
   * @param id    主键
   * @return
   */
  default JdbcTask updateById(String table, JsonObject data, Object id) {
    return updateById(null, table, data, id);
  }

  /**
   * 根据条件更新，忽略实体中的null.不在上下文中存储结果.
   *
   * @param table   表名
   * @param data    持久化对象
   * @param example 查询条件
   * @return
   */
  default JdbcTask updateByExample(String table, JsonObject data, Example example) {
    return updateByExample(null, table, data, example);
  }

  /**
   * 根据主键查找.不在上下文中存储结果.
   *
   * @param table 表名
   * @param id    主键
   * @return
   */
  default JdbcTask findById(String table, Object id) {
    return findById(null, table, id);
  }

  /**
   * 根据主键查找.不在上下文中存储结果.
   *
   * @param taskName 上下文中的结果名
   * @param table    表名
   * @param id       主键
   * @return
   */
  default JdbcTask findById(String taskName, String table, Object id) {
    return findById(taskName, table, id, null);
  }

  /**
   * 根据主键查找.不在上下文中存储结果.
   *
   * @param table  表名
   * @param id     主键
   * @param fields 返回的属性列表
   * @return
   */
  default JdbcTask findById(String table, Object id, List<String> fields) {
    return findById(null, table, id, fields);
  }

  /**
   * 根据主键查找.不在上下文中存储结果.
   *
   * @param table       表名
   * @param idExtractor 从上下文中获取主键的方法
   * @return
   */
  default JdbcTask findById(String table, Function<Map<String, Object>, Object> idExtractor) {
    return findById(null, table, idExtractor);
  }

  /**
   * 根据主键查找.不在上下文中存储结果.
   *
   * @param taskName    上下文中的结果名
   * @param table       表名
   * @param idExtractor 从上下文中获取主键的方法
   * @return
   */
  default JdbcTask findById(String taskName, String table,
                            Function<Map<String, Object>, Object> idExtractor) {
    return findById(taskName, table, idExtractor, null);
  }

  /**
   * 根据主键查找.不在上下文中存储结果.
   *
   * @param table       表名
   * @param idExtractor 从上下文中获取主键的方法
   * @param fields      返回的属性列表
   * @return
   */
  default JdbcTask findById(String table, Function<Map<String, Object>, Object> idExtractor,
                            List<String> fields) {
    return findById(null, table, idExtractor, fields);
  }

  /**
   * 根据条件查找.不在上下文中存储结果.
   *
   * @param table   表名,
   * @param example 查询参数的定义，包括查询条件、排序规则等
   * @return
   */
  default JdbcTask findByExample(String table, Example example) {
    return findByExample(table, example);
  }

  /**
   * 根据条件查找.不在上下文中存储结果.
   *
   * @param table   表名,
   * @param start   开始索引
   * @param limit   查询数量
   * @param example 查询参数的定义，包括查询条件、排序规则等
   * @return
   */
  default JdbcTask findByExample(String table, Example example, int start, int limit) {
    return findByExample(null, table, example, start, limit);
  }

  /**
   * 根据条件查找.不在上下文中存储结果.
   *
   * @param table   表名
   * @param example 查询条件
   * @return
   */
  default JdbcTask countByExample(String table, Example example) {
    return countByExample(null, table, example);
  }

  /**
   * 分页查找.不在上下文中存储结果.
   *
   * @param table    表名
   * @param example  查询参数的定义，包括查询条件、排序规则等
   * @param page     页码
   * @param pageSize 每页数量
   * @return
   */
  default JdbcTask pagination(String table, Example example,
                              final int page, final int pageSize) {
    return pagination(null, table, example, page, pageSize);
  }

  /**
   * 根据条件查找.不在上下文中存储结果
   *
   * @param table   表名
   * @param example 查询参数的定义，包括查询条件、排序规则等
   * @return
   */
  default JdbcTask findFirstByExample(String table, Example example) {
    return findFirstByExample(null, table, example);
  }

  /**
   * 根据条件查找，并返回总数.不在上下文中保存结果.
   *
   * @param table   表名
   * @param example 查询参数的定义，包括查询条件、排序规则等
   * @param start   开始索引
   * @param limit   查询数量
   * @return
   */
  default JdbcTask page(String table, Example example, int start,
                        int limit) {
    return page(null, table, example, start, limit);
  }

}

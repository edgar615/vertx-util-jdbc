package com.github.edgar615.util.vertx.jdbc;

import com.github.edgar615.jdbc.codegen.gen.CodegenOptions;
import com.github.edgar615.jdbc.codegen.gen.Generator;

/**
 * Created by Administrator on 2015/6/9.
 */
public class DatabaseCodeGen {

  public static void main(String[] args) throws Exception {

    CodegenOptions options = new CodegenOptions().setUsername("admin")
            .setPassword("csst")
            .addGenTable("user")
            .setIgnoreColumnsStr("created_on,updated_on")
            .setGenRule(true)
            .setJdbcUrl(
                    "jdbc:mysql://test.ihorn.com.cn:3306/user_new")
            .setSrcFolderPath("src/test/java")
            .setDomainPackage("com.github.edgar615.util.vertx.jdbc");
    new Generator(options).generate();

  }

}
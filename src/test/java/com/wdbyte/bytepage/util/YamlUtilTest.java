package com.wdbyte.bytepage.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.wdbyte.bytepage.module.PostInfo;
import org.yaml.snakeyaml.Yaml;

/**
 * @author niulang
 * @date 2023/04/12
 */
class YamlUtilTest {

    void parseYaml() {
        Yaml yaml = new Yaml();
        String ctx = "title: Java String 字符串\n"
            + "date: 2023-03-23 20:20:04\n"
            + "permalink: /java/java-string/\n"
            + "categories:\n"
            + "  - Java 基础教程\n"
            + "tags:\n"
            + "  - String\n"
            + "meta:\n"
            + "  - name: description\n"
            + "    content: Java 中的 String 是 Java 中最常用的类之一，String 是一个不可变的字符序列，用于表示文本。\n"
            + "  - name: keywords\n"
            + "    content: Java String,Java 字符串\n"
            + "feed:\n"
            + "  enable: true"
            + "";
        InputStream inputStream = new ByteArrayInputStream(ctx.getBytes());
        ;
        //Map<String, Object> obj = yaml.load(inputStream);
        PostInfo customer = yaml.loadAs(inputStream, PostInfo.class);
        System.out.println(customer);
    }
}
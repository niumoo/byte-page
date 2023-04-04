package com.wdbyte.bytepage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import com.wdbyte.bytepage.module.PostInfo;
import com.wdbyte.bytepage.util.MarkdownUtil;
import com.wdbyte.bytepage.util.PostTemplateUtil;
import com.wdbyte.bytepage.util.ThymeleafHtmlUtil;
import org.thymeleaf.context.Context;

/**
 * @author niulang
 * @date 2023/03/31
 */
public class BytePage {

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("/Users/darcy/develop/voding/docs/01.Java 基础/10.Java 基础/05.java-array.md");
        PostInfo postInfo = PostTemplateUtil.convert2PostInfo(path);

        String postContent = MarkdownUtil.markdownToHtmlExtensions(postInfo.getMarkdownContent());
        // 定义数据模型
        Context context = new Context();
        context.setVariable("post_content", postContent);
        context.setVariable("post_title", postInfo.getTitle());
        context.setVariable("twoList", Arrays.asList("JDK、JRE、JVM 的区别","Java 数据类型","Java 流程控制","Java String 字符串","Java Array 数组","Java 多维数组","Java StringBuilder","Java 继承"));

        // 渲染模板 (输出到变量（控制台）)
        String processRes = ThymeleafHtmlUtil.processHtml("post2", context);
        System.out.println(processRes);

        // 输出到流（文件）
        ThymeleafHtmlUtil.processHtmlWriteFile("dist/post2.html", "post2", context);
    }

}

package com.wdbyte.bytepage.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * @author niulang
 * @date 2023/03/31
 */
public class ThymeleafHtmlUtil {

    private static TemplateEngine templateEngine;

    static {
        // 定义、设置模板解析器
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        // 设置模板类型 # https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#what-is-thymeleaf
        // HTML、XML、TEXT、JAVASCRIPT、CSS、RAW
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        // 定义模板引擎
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }

    public static String processHtml(String templateName, Context context) {
        return templateEngine.process(templateName, context);
    }

    public static String processHtmlWriteFile(String path, String templateName, Context context) throws IOException {
        File file = new File(path);
        Writer write = new FileWriter(file);
        templateEngine.process(templateName, context, write);
        return file.getAbsolutePath();
    }

}

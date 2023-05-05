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
public class ThymeleafUtil {

    private static TemplateEngine templateEngine;
    private static TemplateEngine xmlTemplateEngine;

    static {
        // 定义、设置模板解析器
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        // 设置模板类型 # https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#what-is-thymeleaf
        // HTML、XML、TEXT、JAVASCRIPT、CSS、RAW
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");
        // 定义模板引擎
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        // 定义、设置模板解析器
        ClassLoaderTemplateResolver xmlTemplateResolver = new ClassLoaderTemplateResolver();
        // 设置模板类型 # https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#what-is-thymeleaf
        // HTML、XML、TEXT、JAVASCRIPT、CSS、RAW
        xmlTemplateResolver.setTemplateMode(TemplateMode.XML);
        xmlTemplateResolver.setPrefix("/templates/");
        xmlTemplateResolver.setSuffix(".xml");
        xmlTemplateResolver.setCharacterEncoding("UTF-8");
        // 定义模板引擎
        xmlTemplateEngine = new TemplateEngine();
        xmlTemplateEngine.setTemplateResolver(xmlTemplateResolver);
    }

    public static String processHtml(String templateName, Context context) {
        return templateEngine.process(templateName, context);
    }

    /**
     * 生成 HTML 写入指定位置
     *
     * @param path          文件生成保存位置
     * @param templateName  模板名称
     * @param context       模版中用到的变量
     * @return
     * @throws IOException
     */
    public static String processHtmlWriteFile(String path, String templateName, Context context) throws IOException {
        File file = new File(path);
        Writer write = new FileWriter(file);
        templateEngine.process(templateName, context, write);
        return file.getAbsolutePath();
    }

    /**
     * 生成 XML 写入指定位置
     *
     * @param path          文件生成保存位置
     * @param templateName  模板名称
     * @param context       模版中用到的变量
     * @return
     * @throws IOException
     */
    public static String processXmlWriteFile(String path, String templateName, Context context) throws IOException {
        File file = new File(path);
        Writer write = new FileWriter(file);
        xmlTemplateEngine.process(templateName, context, write);
        return file.getAbsolutePath();
    }

}

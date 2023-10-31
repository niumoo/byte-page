package com.wdbyte.bytepage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.wdbyte.bytepage.module.PostInfo;
import com.wdbyte.bytepage.module.TreeNode;
import com.wdbyte.bytepage.util.FileUtil;
import com.wdbyte.bytepage.util.HtmlParser;
import com.wdbyte.bytepage.util.MarkdownUtil;
import com.wdbyte.bytepage.util.PostTemplateUtil;
import com.wdbyte.bytepage.util.ThymeleafUtil;
import org.apache.commons.lang3.StringUtils;
import org.thymeleaf.context.Context;

/**
 * @author niulang
 * @date 2023/04/04
 */
public class DocPage {
    static String ROOT_PATH = null;

    private static String WEBSITE = "https://www.wdbyte.com";

    static Map<String, TreeNode<PostInfo>> postInfoMap = new HashMap<>();

    static TreeNode<PostInfo> rootNode;

    public static void main(String[] args) throws IOException {
        if (args == null || args.length == 0) {
            System.out.println("请传入文件夹路径");
            return;
        }
        ROOT_PATH = args[0];
        initRootNode();
        generatorPostHtmlForEach();
        generatorIndexHtml();
        generatorArchivesHtml();
        generatorSitemapXml();
        generatorFeedXml();
        generatorLimit5Url();
        copyStaticFile();
    }

    private static void initRootNode() throws IOException {
        rootNode = new TreeNode<>("root", null, null);
        toFileTree(rootNode, Paths.get(ROOT_PATH));
        rootNode = rootNode.getChildren().get(0);
    }

    public static void toFileTree(TreeNode<PostInfo> treeNode, Path path) throws IOException {
        String pathName = FileUtil.getPathNameByIndex(path, 1);
        pathName = StringUtils.substringAfter(pathName, ".");
        if (path.toString().endsWith(".md") && !Files.isDirectory(path)) {
            try {
                TreeNode<PostInfo> subNode = new TreeNode<>(pathName, PostTemplateUtil.convert2PostInfo(path), treeNode);
                treeNode.addChild(subNode);
                postInfoMap.put(path.toString(), subNode);
                return;
            } catch (Exception e) {
                System.out.println("error path:" + path.toString());
                e.printStackTrace();
                throw e;
            }
        }
        TreeNode<PostInfo> subNode = new TreeNode<>(pathName, null, treeNode);
        treeNode.addChild(subNode);
        List<Path> pathList = FileUtil.listDirAndMdFile(path,".md");
        for (Path pathTemp : pathList) {
            toFileTree(subNode, pathTemp);
        }
    }

    private static void generatorPostHtmlForEach() throws IOException {
        List<Path> pathList = FileUtil.listFiles(ROOT_PATH, ".md");
        for (Path path : pathList) {
            try {
                generatorPostHtml(path.toString(), generatorSavePath(path.toString()));
            } catch (Exception e) {
                System.out.printf(String.format("文章生成失败,path:%s,msg:%s", path.toString(), e.getMessage()));
                e.printStackTrace();
            }
        }
    }

    private static void generatorPostHtml(String currentFilePath, String saveFilePath)
        throws IOException {
        // 给定一个文件路径，向上取两级分类
        TreeNode<PostInfo> treeNode = postInfoMap.get(currentFilePath);
        TreeNode<PostInfo> menuNode = treeNode.getParent().getParent();
        String postContent = MarkdownUtil.markdownToHtmlExtensions(treeNode.getData().getMarkdownContent());
        treeNode.getData().setHtmlContent(postContent);
        // 定义数据模型
        Context context = new Context();
        // 文章内容
        context.setVariable("postInfo", treeNode.getData());
        // 顶部菜单
        context.setVariable("rootNode", rootNode);
        // 侧边菜单
        context.setVariable("menuNode", menuNode);
        // 文章目录
        context.setVariable("tocInfoList", HtmlParser.getHeadList(postContent));
        // 输出到流（文件）
        ThymeleafUtil.processHtmlWriteFile(saveFilePath, "blog", context);
        //System.out.println("生成文章详情：" + treeNode.getData().getTitle());
    }

    private static void generatorIndexHtml() throws IOException {
        // 定义数据模型
        Context context = new Context();
        // 用于生成顶部菜单
        context.setVariable("rootNode", rootNode);
        // 用于文章列表
        List<PostInfo> postInfoList = postInfoMap.values().stream()
            .map(TreeNode::getData)
            .sorted(Comparator.comparing(PostInfo::getDate).reversed())
            .limit(10)
            .collect(Collectors.toList());
        context.setVariable("postInfoList", postInfoList);
        // 输出到流（文件）
        ThymeleafUtil.processHtmlWriteFile("dist/index.html", "blog_index", context);
    }

    private static void generatorSitemapXml() throws IOException {
        // 定义数据模型
        Context context = new Context();
        context.setVariable("rootNode", rootNode);
        // 用于文章列表
        List<PostInfo> postInfoList = postInfoMap.values().stream()
            .map(TreeNode::getData)
            .sorted(Comparator.comparing(PostInfo::getDate).reversed())
            .collect(Collectors.toList());
        context.setVariable("postInfoList", postInfoList);
        context.setVariable("currentDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        // 输出到流（文件）
        ThymeleafUtil.processXmlWriteFile("dist/sitemap.xml", "sitemap", context);
    }

    private static void generatorFeedXml() throws IOException {
        // 用于文章列表
        List<PostInfo> postInfoList = postInfoMap.values().stream()
            .map(TreeNode::getData)
            .sorted(Comparator.comparing(PostInfo::getDateUtc).reversed())
            .map(postInfo -> {
                String htmlContent = postInfo.getHtmlContent();
                String top2Content = HtmlParser.getTop2Content(htmlContent,postInfo.getPermalink());
                top2Content = "为了更好的阅读体验，<a href=\"https://www.wdbyte.com"+postInfo.getPermalink()+"\">可以点击跳转到网页继续阅读.....</a></b>";
                postInfo.setTop2HtmlContent(top2Content);
                return postInfo;
            })
            .limit(3)
            .collect(Collectors.toList());
        // 定义数据模型
        Context context = new Context();
        context.setVariable("postInfoList", postInfoList);
        context.setVariable("currentDate", postInfoList.get(0).getDateUtc());
        // 输出到流（文件）
        ThymeleafUtil.processXmlWriteFile("dist/feed.xml", "feed", context);
    }

    private static void generatorLimit5Url() throws IOException {
        // 抽取最近5条url
        List<String> postUrlList = postInfoMap.values().stream()
            .map(TreeNode::getData)
            .sorted(Comparator.comparing(PostInfo::getUpdated).reversed())
            .map(postInfo -> "https://www.wdbyte.com" + postInfo.getPermalink())
            .limit(5).collect(Collectors.toList());
        // 百度提交 URL 格式
        Files.write(Paths.get("urls.txt"), postUrlList);
        // Bing 提交 URL 格式
        String urls = postUrlList.stream().collect(Collectors.joining("\",\""));
        urls = "\"" + urls + "\"";
        urls = "{\"siteUrl\":\"" + WEBSITE + "\", \"urlList\":[" + urls + "]}";
        Files.write(Paths.get("urls_bing.txt"), urls.getBytes());
    }

    private static void generatorArchivesHtml() throws IOException {
        // 定义数据模型
        Context context = new Context();
        List<PostInfo> postInfoList = postInfoMap.values().stream()
            .map(TreeNode::getData)
            .sorted(Comparator.comparing(PostInfo::getDate).reversed())
            .collect(Collectors.toList());
        // 用于文章列表
        context.setVariable("postInfoList", postInfoList);
        // 用于一级菜单
        context.setVariable("rootNode", rootNode);
        File file = new File("dist/archives/");
        if (!file.exists()) {
            file.mkdirs();
        }
        // 输出到流（文件）
        ThymeleafUtil.processHtmlWriteFile("dist/archives/index.html", "blog_archives", context);
    }

    public static String generatorSavePath(String currentFilePath) {
        TreeNode<PostInfo> treeNode = postInfoMap.get(currentFilePath);
        String saveFilePath = String.format("dist%s", treeNode.getData().getPermalink());
        File file = new File(saveFilePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return saveFilePath + "index.html";
    }

    public static void copyStaticFile() throws IOException {
        File file = new File("dist/static");
        if (!file.exists()) {
            file.mkdirs();
        }
        ClassLoader classLoader = DocPage.class.getClassLoader();
        URL url = classLoader.getResource("static");
        File sourceFile = new File(url.getFile());
        for (File source : sourceFile.listFiles()) {
            File targetFolder = new File("dist/static/");
            if (!targetFolder.exists()) {
                targetFolder.mkdirs();
            }
            File targetFile = new File(targetFolder, source.getName());
            Files.copy(source.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println(String.format("copy static file,src:%s,target:%s", source, targetFile));
        }
    }
}

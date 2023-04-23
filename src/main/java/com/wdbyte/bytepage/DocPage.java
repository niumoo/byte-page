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
import com.wdbyte.bytepage.util.MarkdownUtil;
import com.wdbyte.bytepage.util.PostTemplateUtil;
import com.wdbyte.bytepage.util.ThymeleafHtmlUtil;
import org.apache.commons.lang3.StringUtils;
import org.thymeleaf.context.Context;

/**
 * @author niulang
 * @date 2023/04/04
 */
public class DocPage {
    static String ROOT_PATH = null;

    static Map<String, TreeNode<PostInfo>> postInfoMap = new HashMap<>();

    static TreeNode<PostInfo> rootNode;

    public static void main(String[] args) throws IOException {
        if (args == null || args.length == 0) {
            System.out.println("请传入文件夹路径");
            return;
        }
        ROOT_PATH = args[0];
        rootNode = new TreeNode<>("root", null, null);
        toFileTree(rootNode, Paths.get(ROOT_PATH));
        rootNode = rootNode.getChildren().get(0);
        List<Path> pathList = FileUtil.listFiles(ROOT_PATH, ".md");
        for (Path path : pathList) {
            try {
                generatorPostHtml(path.toString(), generatorSavePath(path.toString()));
            } catch (Exception e) {
                System.out.printf(String.format("文章生成失败,path:%s,msg:%s", path.toString(), e.getMessage()));
                e.printStackTrace();
            }
        }
        generatorIndexHtml();
        generatorArchivesHtml();
        generatorSitemapXml();
        generatorLimit20Url();
        //copyStaticFile();
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
        context.setVariable("postInfo", treeNode.getData());
        context.setVariable("rootNode", rootNode);
        context.setVariable("menuNode", menuNode);
        // 输出到流（文件）
        ThymeleafHtmlUtil.processHtmlWriteFile(saveFilePath, "post", context);
        //System.out.println("生成文章详情：" + treeNode.getData().getTitle());
    }

    private static void generatorIndexHtml() throws IOException {
        // 定义数据模型
        Context context = new Context();
        context.setVariable("rootNode", rootNode);
        // 用于文章列表
        List<PostInfo> postInfoList = postInfoMap.values().stream()
            .map(TreeNode::getData)
            .sorted(Comparator.comparing(PostInfo::getDate).reversed())
            .limit(10)
            .collect(Collectors.toList());
        context.setVariable("postInfoList", postInfoList);
        // 输出到流（文件）
        ThymeleafHtmlUtil.processHtmlWriteFile("dist/index.html", "index", context);
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
        ThymeleafHtmlUtil.processXmlWriteFile("dist/sitemap.xml", "sitemap", context);
    }

    private static void generatorLimit20Url() throws IOException {
        // 抽取最近20条url
        List<String> postUrlList = postInfoMap.values().stream()
            .map(TreeNode::getData)
            .sorted(Comparator.comparing(PostInfo::getDate).reversed())
            .map(postInfo -> "https://www.wdbyte.com" + postInfo.getPermalink())
            .limit(20).collect(Collectors.toList());
        Files.write(Paths.get("urls.txt"), postUrlList);
    }

    private static void generatorArchivesHtml() throws IOException {
        // 定义数据模型
        Context context = new Context();
        // 用于一级菜单
        context.setVariable("rootNode", rootNode);
        // 用于文章列表
        List<PostInfo> postInfoList = postInfoMap.values().stream()
            .map(TreeNode::getData)
            .sorted(Comparator.comparing(PostInfo::getDate).reversed())
            .collect(Collectors.toList());
        context.setVariable("postInfoList", postInfoList);
        File file = new File("dist/archives/");
        if (!file.exists()) {
            file.mkdirs();
        }
        // 输出到流（文件）
        ThymeleafHtmlUtil.processHtmlWriteFile("dist/archives/index.html", "archives", context);
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

    public static void toFileTree(TreeNode<PostInfo> treeNode, Path path) throws IOException {
        String pathName = FileUtil.getPathNameByIndex(path, 1);
        pathName = StringUtils.substringAfter(pathName, ".");
        if (path.toString().endsWith(".md") && !Files.isDirectory(path)) {
            TreeNode<PostInfo> subNode = new TreeNode<>(pathName, PostTemplateUtil.convert2PostInfo(path), treeNode);
            treeNode.addChild(subNode);
            postInfoMap.put(path.toString(), subNode);
            return;
        }
        TreeNode<PostInfo> subNode = new TreeNode<>(pathName, null, treeNode);
        treeNode.addChild(subNode);
        List<Path> pathList = FileUtil.listDirAndMdFile(path);
        for (Path pathTemp : pathList) {
            toFileTree(subNode, pathTemp);
        }
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

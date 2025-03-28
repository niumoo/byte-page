package com.wdbyte.bytepage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Base64;
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
        //args = new String[] {"/Users/niulang/git/byte-notes/md"};
        //if (args == null || args.length == 0) {
        //    System.out.println("请传入文件夹路径");
        //    return;
        //}
        ROOT_PATH = args[0];
        initRootNode();
        generatorPostHtmlForEach();
        //generatorIndexHtml();
        //generatorIndexMd();
        generatorIndexPost();
        generatorCatPost();
        //generatorArchivesHtml();
        generatorSitemapXml();
        generatorFeedXml();
        generatorLimit5Url();
        //copyStaticFile();
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
        System.out.println("生成文章详情完成");
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
        ThymeleafUtil.processHtmlWriteFile(saveFilePath, "post", context);
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
            .limit(1000)
            .collect(Collectors.toList());
        context.setVariable("postInfoList", postInfoList);
        // 输出到流（文件）
        ThymeleafUtil.processHtmlWriteFile("dist/index.html", "index", context);
    }

    private static void generatorIndexMd() throws IOException {
        // 定义数据模型
        Context context = new Context();
        // 用于生成顶部菜单
        context.setVariable("rootNode", rootNode);
        // 用于文章列表
        List<PostInfo> postInfoList = postInfoMap.values().stream()
            .map(TreeNode::getData)
            .sorted(Comparator.comparing(PostInfo::getDate).reversed())
            .limit(1000)
            .collect(Collectors.toList());
        PostInfo postInfo = postInfoList.stream().filter(post -> post.getPermalink().equals("/index/")).findFirst().get();
        context.setVariable("postInfo", postInfo);
        context.setVariable("postInfoList", postInfoList.stream().limit(10).collect(Collectors.toList()));
        // 输出到流（文件）
        ThymeleafUtil.processHtmlWriteFile("dist/index.html", "index", context);
        System.out.println("生成首页完成");
    }

    private static void generatorIndexPost() throws IOException {
        // 定义数据模型
        Context context = new Context();
        context.setVariable("catNodeList", rootNode.getChildren());
        // 用于文章列表
        List<PostInfo> postInfoList = postInfoMap.values().stream()
            .map(TreeNode::getData)
            .sorted(Comparator.comparing(PostInfo::getDate).reversed())
            .limit(10)
            .collect(Collectors.toList());
        context.setVariable("postInfoList", postInfoList);
        // 输出到流（文件）
        ThymeleafUtil.processHtmlWriteFile("dist/index.html", "index", context);
        System.out.println("生成首页完成");
    }

    private static void generatorCatPost() throws IOException {
        for (TreeNode<PostInfo> catNode : rootNode.getChildren()) {
            // 定义数据模型
            Context context = new Context();
            context.setVariable("catNode", catNode);
            // 输出到流（文件）
            String path = "dist/" + Base64.getEncoder().encodeToString(catNode.getName().getBytes());
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            ThymeleafUtil.processHtmlWriteFile(path+"/index.html", "cat", context);
            System.out.println("生成" + catNode.getName() + "完成");
        }
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
        System.out.println("生成feed完成");
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
        ThymeleafUtil.processHtmlWriteFile("dist/archives/index.html", "archives", context);
        System.out.println("生成归档完成");
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
        File sourceDir = new File(url.getFile());
        File targetDir = new File("dist/static");
        try {
            clearDirectory(targetDir);
            copyDirectory(sourceDir, targetDir);
            System.out.println("静态资源拷贝完成！");
        } catch (IOException e) {
            System.err.println("文件夹拷贝失败: " + e.getMessage());
        }
    }


    /**
     * 递归拷贝文件夹及其内容
     * @param sourceDir 源文件夹
     * @param targetDir 目标文件夹
     * @throws IOException 如果发生I/O错误
     */
    public static void copyDirectory(File sourceDir, File targetDir) throws IOException {
        // 如果目标目录不存在，则创建
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        // 获取源目录下的所有文件和子目录
        File[] files = sourceDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 如果是子目录，递归调用
                    copyDirectory(file, new File(targetDir, file.getName()));
                } else {
                    // 如果是文件，直接拷贝并替换
                    File targetFile = new File(targetDir, file.getName());
                    Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    /**
     * 清空目标文件夹中的所有内容
     * @param directory 要清空的文件夹
     * @throws IOException 如果发生I/O错误
     */
    public static void clearDirectory(File directory) throws IOException {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // 如果是子目录，递归删除
                        clearDirectory(file);
                    }
                    // 删除文件或空目录
                    Files.delete(file.toPath());
                }
            }
        } else {
            // 如果目标目录不存在，则创建
            directory.mkdirs();
        }
    }
}

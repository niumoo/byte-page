package com.wdbyte.bytepage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    static String ROOT_PATH = "/Users/darcy/Downloads/doc";

    static Map<String, TreeNode<PostInfo>> postInfoMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        TreeNode<PostInfo> rootNode = new TreeNode<>("root", null, null);
        toFileTree(rootNode, Paths.get(ROOT_PATH));

        String currentFilePath = "/Users/darcy/Downloads/doc/01.Java 基础/10.Java 基础/05.java-array.md";
        List<Path> pathList = FileUtil.listFiles("/Users/darcy/Downloads/doc/", ".md");
        for (Path path : pathList) {
            generatorPostHtml(path.toString());
        }
    }

    private static void generatorPostHtml(String currentFilePath) throws IOException {
        // 给定一个文件路径，向上取两级分类
        TreeNode<PostInfo> treeNode = postInfoMap.get(currentFilePath);
        TreeNode<PostInfo> menuNode = treeNode.getParent().getParent();
        String postContent = MarkdownUtil.markdownToHtmlExtensions(treeNode.getData().getMarkdownContent());
        treeNode.getData().setHtmlContent(postContent);
        // 定义数据模型
        Context context = new Context();
        context.setVariable("postInfo", treeNode.getData());
        context.setVariable("menuNode", menuNode);
        // 输出到流（文件）
        String saveFilePath = String.format("dist%s", treeNode.getData().getPermalink());
        File file = new File(saveFilePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        saveFilePath = saveFilePath + "index.html";
        ThymeleafHtmlUtil.processHtmlWriteFile(saveFilePath, "post2", context);
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
}
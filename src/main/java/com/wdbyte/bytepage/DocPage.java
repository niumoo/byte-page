package com.wdbyte.bytepage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sun.source.doctree.DocTree;
import com.wdbyte.bytepage.module.PostInfo;
import com.wdbyte.bytepage.module.TreeNode;
import com.wdbyte.bytepage.util.FileUtil;
import com.wdbyte.bytepage.util.ListUtils;
import com.wdbyte.bytepage.util.MarkdownUtil;
import com.wdbyte.bytepage.util.PostTemplateUtil;
import com.wdbyte.bytepage.util.ThymeleafHtmlUtil;
import lombok.Data;
import org.apache.commons.collections4.list.TreeList;
import org.thymeleaf.context.Context;

/**
 * @author niulang
 * @date 2023/04/04
 */
public class DocPage {
    static String ROOT_PATH = "/Users/darcy/Downloads/doc";

    static Map<String,TreeNode<PostInfo>> postInfoMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        TreeNode<PostInfo> rootNode = new TreeNode<>("root", null, null);
        toFileTree(rootNode, Paths.get(ROOT_PATH));
        rootNode = rootNode.getChildren().get(0);

        // 给定一个文件路径，向上取两级分类
        TreeNode<PostInfo> menuNode = postInfoMap.get("/Users/darcy/Downloads/doc/10.Java 新特性/10.Java 新特性/110.java-07-files-paths.md").getParent().getParent();


        Path path = Paths.get("/Users/darcy/Downloads/doc/10.Java 新特性/10.Java 新特性/110.java-07-files-paths.md");
        PostInfo postInfo = PostTemplateUtil.convert2PostInfo(path);

        String postContent = MarkdownUtil.markdownToHtmlExtensions(postInfo.getMarkdownContent());
        // 定义数据模型
        Context context = new Context();
        context.setVariable("post_content", postContent);
        context.setVariable("post_title", postInfo.getTitle());
        context.setVariable("twoList", Arrays.asList("JDK、JRE、JVM 的区别","Java 数据类型","Java 流程控制","Java String 字符串","Java Array 数组","Java 多维数组","Java StringBuilder","Java 继承"));
        context.setVariable("menuNode", menuNode);

        // 渲染模板 (输出到变量（控制台）)
        String processRes = ThymeleafHtmlUtil.processHtml("post2", context);
        System.out.println(processRes);

        // 输出到流（文件）
        ThymeleafHtmlUtil.processHtmlWriteFile("dist/post2.html", "post2", context);

    }

    public static void toFileTree(TreeNode<PostInfo> treeNode, Path path) throws IOException {
        String pathName = FileUtil.getPathNameByIndex(path, 1);
        if (path.toString().endsWith(".md") && !Files.isDirectory(path)) {
            TreeNode<PostInfo> subNode = new TreeNode<>(pathName, PostTemplateUtil.convert2PostInfo(path), treeNode);
            treeNode.addChild(subNode);
            postInfoMap.put(path.toString(),subNode);
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
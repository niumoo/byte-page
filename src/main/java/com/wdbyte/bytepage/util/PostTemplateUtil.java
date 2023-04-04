package com.wdbyte.bytepage.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.wdbyte.bytepage.module.PostInfo;

/**
 * @author niulang
 * @date 2023/03/31
 */
public class PostTemplateUtil {

    public static PostInfo convert2PostInfo(Path path) throws IOException {
        String post = new String(Files.readAllBytes(path));
        post = post.trim();
        int endOf = post.indexOf("---", 3);
        String yaml = post.substring(4, endOf);
        PostInfo postInfo;
        try {
            postInfo = YamlUtil.parseYaml(yaml);
        } catch (Exception e) {
            System.out.println("path:" + path.toString());
            throw new RuntimeException(e);
        }
        String md = post.substring(endOf + 3).trim();
        postInfo.setMarkdownContent(md);
        postInfo.setFilePath(path);
        postInfo.setFilePathString(path.toString());
        // URL 归一化
        String link = postInfo.getPermalink().trim();
        if (!link.startsWith("/")) {
            link = "/" + link;
        }
        if (!link.endsWith("/")) {
            link = link + "/";
        }
        postInfo.setPermalink(link);
        return postInfo;
    }

}

package com.wdbyte.bytepage.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;

import com.wdbyte.bytepage.module.PostInfo;
import org.apache.commons.collections4.CollectionUtils;

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
        List<LinkedHashMap<String, String>> metaList = postInfo.getMeta();
        if (CollectionUtils.isNotEmpty(metaList)) {
            for (LinkedHashMap<String, String> metaMap : metaList) {
                if (metaMap.get("name").equals("description")) {
                    postInfo.setDescription(metaMap.get("content"));
                }
                if (metaMap.get("name").equals("keywords")) {
                    postInfo.setKeywords(metaMap.get("content"));
                }
            }
        }
        if (postInfo.getUpdated() == null) {
            postInfo.setUpdated(postInfo.getDate());
        }
        return postInfo;
    }

}

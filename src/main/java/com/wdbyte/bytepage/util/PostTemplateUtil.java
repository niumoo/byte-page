package com.wdbyte.bytepage.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;

import com.wdbyte.bytepage.module.PostInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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
        String githubPath = StringUtils.substringBefore(path.toString(),"md/");
        githubPath = StringUtils.replace(path.toString(),githubPath,"https://github.com/niumoo/byte-notes/blob/main/");
        postInfo.setGithubPath(githubPath);
        // URL 归一化
        String link = postInfo.getPermalink().trim();
        if (!link.startsWith("/")) {
            link = "/" + link;
        }
        if (!link.endsWith("/")) {
            link = link + "/";
        }
        postInfo.setPermalink(link);
        // keyword 和 description
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
        // 添加 UTC 时间
        DateTimeFormatter patternFrom = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter patternTo = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        if (postInfo.getDate() != null){
            LocalDateTime time = LocalDateTime.parse(postInfo.getDate(), patternFrom);
            time = time.minusHours(8);
            // 格式化
            postInfo.setDateUtc(patternTo.format(time));
        }
        if (postInfo.getUpdated() != null) {
            LocalDateTime time = LocalDateTime.parse(postInfo.getUpdated(), patternFrom);
            time = time.minusHours(8);
            // 格式化
            postInfo.setUpdatedUtc(patternTo.format(time));
        }
        return postInfo;
    }
}

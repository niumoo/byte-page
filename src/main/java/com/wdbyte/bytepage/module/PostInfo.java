package com.wdbyte.bytepage.module;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author niulang
 * @date 2023/03/31
 */
public class PostInfo {
    private String title;
    private String date;
    private String updated;
    private String permalink;
    private List<String> categories;
    private List<String> tags;
    private List<LinkedHashMap<String, String>> meta;
    private LinkedHashMap<String, String> feed;
    private Path filePath;
    private String filePathString;
    private String githubPath;
    private String keywords;
    private String description;
    private String htmlContent;
    private String markdownContent;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<LinkedHashMap<String, String>> getMeta() {
        return meta;
    }

    public void setMeta(List<LinkedHashMap<String, String>> meta) {
        this.meta = meta;
    }

    public String getMarkdownContent() {
        return markdownContent;
    }

    public void setMarkdownContent(String markdownContent) {
        this.markdownContent = markdownContent;
    }

    public LinkedHashMap<String, String> getFeed() {
        return feed;
    }

    public void setFeed(LinkedHashMap<String, String> feed) {
        this.feed = feed;
    }

    public Path getFilePath() {
        return filePath;
    }

    public String getFilePathString() {
        return filePathString;
    }

    public void setFilePathString(String filePathString) {
        this.filePathString = filePathString;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public String getGithubPath() {
        return githubPath;
    }

    public void setGithubPath(String githubPath) {
        this.githubPath = githubPath;
    }

    @Override
    public String toString() {
        return "PostInfo{" +
            "title='" + title + '\'' +
            ", date='" + date + '\'' +
            ", permalink='" + permalink + '\'' +
            ", categories=" + categories +
            ", tags=" + tags +
            ", meta=" + meta +
            ", feed=" + feed +
            ", filePath=" + filePath +
            ", filePathString='" + filePathString + '\'' +
            ", updated='" + updated + '\'' +
            ", keywords='" + keywords + '\'' +
            ", description='" + description + '\'' +
            ", htmlContent='" + htmlContent + '\'' +
            ", markdownContent='" + markdownContent + '\'' +
            '}';
    }
}

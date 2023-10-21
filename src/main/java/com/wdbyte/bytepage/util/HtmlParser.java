package com.wdbyte.bytepage.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlParser {
    /**
     * 获取HTML内容中的指定标签
     *
     * @param html List Triple (margin,id,titleValue)
     * @return
     */
    public static List<Triple<Integer, String, String>> getHeadList(String html) {
        Document doc = Jsoup.parse(html);
        Elements headings = doc.select("h1, h2, h3, h4, h5, h6");
        List<Triple<Integer, String, String>> tocList = new ArrayList<>();
        int max = 5;
        for (Element heading : headings) {
            Integer level = Integer.valueOf(heading.tag().getName().replace("h", ""));
            if (level < max) {
                max = level;
            }
        }
        for (Element heading : headings) {
            Integer level = Integer.valueOf(heading.tag().getName().replace("h", "")) - max;
            Integer margin = level * 20;
            String value = heading.text();
            String id = heading.attributes().get("id");
            tocList.add(Triple.of(margin, id, value));
        }
        return tocList;
    }

    public static String getTop2Content(String html) {
        int indexOf = html.indexOf("<h2");
        return StringUtils.substring(html, 0, StringUtils.indexOf(html, "<h2", indexOf + 1))+
            "<b>完整内容请到网页阅读.....</b>";
    }
}

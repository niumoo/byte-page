package com.wdbyte.bytepage.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.AttributeProviderContext;
import org.commonmark.renderer.html.AttributeProviderFactory;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * @author niulang
 * @date 2023/03/31
 */
public class MarkdownUtil {

    private static final List<Extension> EXTENSIONS =
        Arrays.asList(TablesExtension.create(), HeadingAnchorExtension.create());

    private static final Parser MARKDOWN_PARSER = Parser.builder()
        .extensions(EXTENSIONS)
        .build();

    private static final HtmlRenderer MARKDOWN_RENDERER = HtmlRenderer.builder()
        .extensions(EXTENSIONS)
        .attributeProviderFactory(new AttributeProviderFactory() {
            @Override
            public AttributeProvider create(AttributeProviderContext context) {
                return new MarkdownUtil.CustomAttributeProvider();
            }
        })
        .build();

    /**
     * markdown格式转换成HTML格式
     *
     * @param markdown
     * @return
     */
    public static String markdownToHtml(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    /**
     * 增加扩展[标题锚点，表格生成]
     * Markdown转换成HTML
     *
     * @param markdown
     * @return
     */
    public static String markdownToHtmlExtensions(String markdown) {
        Node document = MARKDOWN_PARSER.parse(markdown);
        return MARKDOWN_RENDERER.render(document);
    }

    /**
     * 处理标签的属性
     */
    static class CustomAttributeProvider implements AttributeProvider {
        @Override
        public void setAttributes(Node node, String tagName, Map<String, String> attributes) {
            //改变a标签的target属性为_blank
            if (node instanceof Link) {
                attributes.put("target", "_blank");
            }
            if (node instanceof TableBlock) {
                attributes.put("class", "ui celled table");
            }
        }
    }
}

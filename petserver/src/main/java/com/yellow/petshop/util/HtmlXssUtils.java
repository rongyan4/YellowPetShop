package com.yellow.petshop.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;

import java.util.regex.Pattern;

/**
 * 富文本 XSS 过滤工具类
 * <p>
 * 用于清洗商家在后台输入的商品详情富文本，去除所有危险标签和属性，
 * 防止通过 v-html 渲染时产生 XSS 攻击。
 * </p>
 *
 * <p>会自动过滤：</p>
 * <ul>
 *   <li>{@code <script>} 标签及内联脚本</li>
 *   <li>{@code onerror / onclick / onload} 等事件属性</li>
 *   <li>{@code javascript:} 、{@code data:} 等危险协议</li>
 *   <li>除白名单外的所有标签和属性</li>
 *   <li>style 属性中的 {@code expression(...)}, {@code url(javascript:...)},
 *       {@code @import}, {@code behavior:} 等危险 CSS 值</li>
 * </ul>
 */
public class HtmlXssUtils {

    /**
     * 匹配 style 属性中的危险 CSS 模式（大小写不敏感，忽略空白与注释）：
     * <ul>
     *   <li>expression(...)      —— IE 特有动态表达式</li>
     *   <li>url(javascript:...)  —— CSS 中内联 JS 协议</li>
     *   <li>url(vbscript:...)    —— VBScript 协议</li>
     *   <li>url(data:...)        —— data URI（可内嵌恶意脚本）</li>
     *   <li>@import             —— 远程样式表注入</li>
     *   <li>behavior:           —— IE HTC 行为绑定</li>
     *   <li>-moz-binding        —— Firefox XBL 绑定</li>
     * </ul>
     */
    private static final Pattern DANGEROUS_STYLE_PATTERN = Pattern.compile(
            "expression\\s*\\("                      // expression(...)
            + "|url\\s*\\(\\s*['\"]?\\s*"
            +   "(?:javascript|vbscript|data)\\s*:"
            +   ""                                    // url(javascript:/vbscript:/data:
            + "|@import"
            + "|behavior\\s*:"
            + "|-moz-binding",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * 富文本白名单（保留常见排版标签，禁止所有脚本）
     */
    private static final Safelist RICH_TEXT_SAFELIST = Safelist.relaxed()
            // 允许额外的块级/行内标签
            .addTags("span", "section", "article", "figure", "figcaption", "style")
            // p / div / span 允许 style（用于字体颜色、对齐等排版）
            .addAttributes("p",    "style", "class")
            .addAttributes("div",  "style", "class")
            .addAttributes("span", "style", "class")
            .addAttributes("h1",   "style", "class")
            .addAttributes("h2",   "style", "class")
            .addAttributes("h3",   "style", "class")
            .addAttributes("h4",   "style", "class")
            .addAttributes("h5",   "style", "class")
            .addAttributes("h6",   "style", "class")
            .addAttributes("ul",   "style", "class")
            .addAttributes("ol",   "style", "class")
            .addAttributes("li",   "style", "class")
            // img 允许 src / alt / title / width / height / style
            .addAttributes("img",  "src", "alt", "title", "width", "height", "style", "class")
            // a 限制只允许 http/https 协议，禁止 javascript:
            .addAttributes("a",    "href", "title", "target")
            .addProtocols("a",     "href", "http", "https")
            // img src 允许 http/https 和相对路径（不允许 data:）
            .addProtocols("img",   "src",  "http", "https");

    /**
     * 清洗富文本，保留安全的排版标签和样式。
     * <br>推荐用于商品详情、商家介绍等富文本字段存库前调用。
     *
     * <p>处理流程：</p>
     * <ol>
     *   <li>先用 Jsoup Safelist 过滤危险标签与属性；</li>
     *   <li>再遍历所有保留了 style 属性的元素，用正则二次清洗
     *       {@code expression / url(javascript:) / @import} 等危险 CSS 值。</li>
     * </ol>
     *
     * @param html 原始富文本 HTML
     * @return 清洗后的安全 HTML；若入参为 null 则返回 null
     */
    public static String clean(String html) {
        if (html == null) {
            return null;
        }
        // 第一步：Jsoup 白名单过滤
        String cleaned = Jsoup.clean(html, RICH_TEXT_SAFELIST);

        // 第二步：对 style 属性值做正则二次过滤
        cleaned = sanitizeStyleAttributes(cleaned);

        return cleaned;
    }

    /**
     * 遍历文档中所有带 style 属性的元素，
     * 若属性值匹配危险 CSS 模式则整体移除该 style 属性。
     *
     * @param html Jsoup 已初步清洗过的 HTML
     * @return 进一步去除危险 style 后的 HTML
     */
    private static String sanitizeStyleAttributes(String html) {
        // 使用 RELAXED 解析器，保留 body 内的片段结构
        Document doc = Jsoup.parseBodyFragment(html);
        for (Element el : doc.body().getAllElements()) {
            if (el.hasAttr("style")) {
                String styleValue = el.attr("style");
                if (DANGEROUS_STYLE_PATTERN.matcher(styleValue).find()) {
                    // 发现危险值，整体移除该 style 属性
                    el.removeAttr("style");
                }
            }
        }
        // 只返回 body 内部的片段，不带 <html>/<body> 外壳
        return doc.body().html();
    }

    /**
     * 严格模式：去掉全部 HTML 标签，只保留纯文本。
     * <br>适用于评论内容、搜索关键词等纯文本场景。
     *
     * @param html 原始内容
     * @return 纯文本内容；若入参为 null 则返回 null
     */
    public static String strip(String html) {
        if (html == null) {
            return null;
        }
        return Jsoup.clean(html, Safelist.none());
    }
}

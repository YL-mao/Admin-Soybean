package com.ylmao.admin.common;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 文本安全工具：纯文本规范化、HTML 转义，以及公告正文的白名单清洗。
 * 标题和说明仍是纯文本；公告正文只保留少量排版标签，不做全站请求体清洗。
 */
public final class TextSafeUtils {

    /** 公告正文允许的标签。style 只在清洗后再按属性白名单收紧。 */
    private static final Safelist NOTICE_SAFELIST = Safelist.none()
            .addTags("p", "br", "b", "strong", "i", "em", "u", "ul", "ol", "li", "a", "span")
            .addAttributes("a", "href")
            .addProtocols("a", "href", "http", "https")
            .addEnforcedAttribute("a", "rel", "nofollow noopener noreferrer")
            .addAttributes("p", "style")
            .addAttributes("span", "style")
            .addAttributes("li", "style")
            .addAttributes("b", "style")
            .addAttributes("strong", "style")
            .addAttributes("i", "style")
            .addAttributes("em", "style")
            .addAttributes("u", "style");

    private static final Document.OutputSettings NOTICE_OUTPUT = new Document.OutputSettings().prettyPrint(false);

    private static final Pattern BLOCK_TAG = Pattern.compile("(?i)</?(?:p|br|ul|ol|li|div|h[1-6])\\b");

    private static final Set<String> STYLE_PROPS = Set.of("color", "font-weight", "text-align", "text-decoration");

    private static final Pattern HEX_COLOR = Pattern.compile("#(?:[0-9a-fA-F]{3}|[0-9a-fA-F]{6}|[0-9a-fA-F]{8})");

    private static final Pattern RGB_COLOR = Pattern.compile(
            "rgba?\\(\\s*(?:\\d{1,3}%?\\s*,\\s*){2}\\d{1,3}%?(?:\\s*,\\s*(?:0|1|0?\\.\\d+)\\s*)?\\)");

    private static final Set<String> NAMED_COLORS = Set.of(
            "black", "white", "red", "green", "blue", "gray", "grey", "orange", "yellow", "purple");

    private static final Pattern FONT_WEIGHT = Pattern.compile("normal|bold|bolder|lighter|[1-9]00");

    private static final Pattern TEXT_ALIGN = Pattern.compile("left|center|right|justify");

    private static final Pattern TEXT_DECORATION = Pattern.compile("none|underline|line-through");

    private TextSafeUtils() {
    }

    /**
     * 纯文本规范化：trim，去掉 NUL 与其它控制字符（保留 tab / 换行）。
     */
    public static String normalizePlainText(String value) {
        if (value == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\t' || c == '\n' || c == '\r') {
                sb.append(c);
                continue;
            }
            // 跳过 C0 控制符与 DEL，避免异常字符进入库与页面。
            if (c < 0x20 || c == 0x7F) {
                continue;
            }
            sb.append(c);
        }
        return sb.toString().trim();
    }

    /**
     * 公告正文：去掉脚本、事件和危险样式，保留加粗、颜色、对齐、列表和 http(s) 链接。
     * 没有块级标签时，换行改成 br，纯文本公告仍按行显示。
     */
    public static String sanitizeNoticeHtml(String value) {
        if (value == null) {
            return null;
        }
        String normalized = normalizePlainText(value);
        if (normalized.isEmpty()) {
            return "";
        }
        if (!BLOCK_TAG.matcher(normalized).find()) {
            normalized = normalized.replace("\r\n", "\n").replace("\r", "\n").replace("\n", "<br>");
        }
        String cleaned = Jsoup.clean(normalized, "", NOTICE_SAFELIST, NOTICE_OUTPUT);
        Document document = Jsoup.parseBodyFragment(cleaned);
        for (Element element : document.select("[style]")) {
            String safeStyle = filterStyle(element.attr("style"));
            if (safeStyle.isEmpty()) {
                element.removeAttr("style");
            } else {
                element.attr("style", safeStyle);
            }
        }
        return document.body().html();
    }

    /** 公告正文转纯文本，给头部摘要和“洗完是否还有内容”用。 */
    public static String noticeHtmlToPlain(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }
        return Jsoup.parse(html).text();
    }

    /** HTML 转义，供拼进页面或属性的出口使用。 */
    public static String escapeHtml(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '&' -> sb.append("&amp;");
                case '<' -> sb.append("&lt;");
                case '>' -> sb.append("&gt;");
                case '"' -> sb.append("&quot;");
                case '\'' -> sb.append("&#39;");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }

    /** 只保留颜色、字重、对齐、下划线；其余样式声明丢掉。 */
    private static String filterStyle(String style) {
        StringBuilder sb = new StringBuilder();
        for (String decl : style.split(";")) {
            int colon = decl.indexOf(':');
            if (colon <= 0) {
                continue;
            }
            String prop = decl.substring(0, colon).trim().toLowerCase(Locale.ROOT);
            if (!STYLE_PROPS.contains(prop)) {
                continue;
            }
            String value = matchStyleValue(prop, decl.substring(colon + 1).trim());
            if (value == null) {
                continue;
            }
            if (!sb.isEmpty()) {
                sb.append("; ");
            }
            sb.append(prop).append(": ").append(value);
        }
        return sb.toString();
    }

    private static String matchStyleValue(String prop, String rawValue) {
        if (rawValue.isEmpty() || rawValue.indexOf('!') >= 0 || rawValue.indexOf('<') >= 0 || rawValue.indexOf('>') >= 0) {
            return null;
        }
        String lower = rawValue.toLowerCase(Locale.ROOT);
        if (lower.contains("url") || lower.contains("expression") || lower.contains("javascript") || lower.contains("@")
                || lower.contains("\\") || lower.contains("/*")) {
            return null;
        }
        return switch (prop) {
            case "color" -> matchColor(rawValue);
            case "font-weight" -> FONT_WEIGHT.matcher(lower).matches() ? lower : null;
            case "text-align" -> TEXT_ALIGN.matcher(lower).matches() ? lower : null;
            case "text-decoration" -> TEXT_DECORATION.matcher(lower).matches() ? lower : null;
            default -> null;
        };
    }

    private static String matchColor(String rawValue) {
        String lower = rawValue.toLowerCase(Locale.ROOT);
        if (NAMED_COLORS.contains(lower)) {
            return lower;
        }
        if (HEX_COLOR.matcher(rawValue).matches()) {
            return lower;
        }
        if (RGB_COLOR.matcher(lower).matches()) {
            return lower;
        }
        return null;
    }
}

package com.ylmao.admin.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextSafeUtilsTest {

    @Test
    void stripsScriptIncludingItsText() {
        String cleaned = TextSafeUtils.sanitizeNoticeHtml("测试xss <script>alert(1)</script>");
        assertEquals("测试xss", TextSafeUtils.noticeHtmlToPlain(cleaned));
        assertFalse(cleaned.toLowerCase().contains("script"));
        assertFalse(cleaned.contains("alert"));
    }

    @Test
    void keepsSafeStyleAndLink() {
        String cleaned = TextSafeUtils.sanitizeNoticeHtml(
                "<p style=\"color:red;font-weight:bold;text-align:center;position:fixed\">"
                        + "<b>加粗</b> <a href=\"javascript:alert(1)\">坏链</a> "
                        + "<a href=\"https://example.com\">好链</a></p>");
        assertTrue(cleaned.contains("color: red"));
        assertTrue(cleaned.contains("font-weight: bold"));
        assertTrue(cleaned.contains("text-align: center"));
        assertFalse(cleaned.contains("position"));
        assertFalse(cleaned.toLowerCase().contains("javascript"));
        assertTrue(cleaned.contains("https://example.com"));
        assertTrue(cleaned.contains("nofollow"));
    }

    @Test
    void keepsToolbarInlineExtras() {
        String cleaned = TextSafeUtils.sanitizeNoticeHtml(
                "<h2>标题</h2><blockquote>引用</blockquote><p><sub>下</sub><sup>上</sup><code>码</code></p>");
        assertTrue(cleaned.contains("<h2>"));
        assertTrue(cleaned.contains("<blockquote>"));
        assertTrue(cleaned.contains("<sub>"));
        assertTrue(cleaned.contains("<sup>"));
        assertTrue(cleaned.contains("<code>"));
    }

    @Test
    void dropsToolbarExcludedTags() {
        String cleaned = TextSafeUtils.sanitizeNoticeHtml(
                "<ul><li>列表</li></ul><table><tr><td>格</td></tr></table>"
                        + "<img src=\"https://example.com/a.png\" onerror=alert(1)><b>留下</b>");
        assertFalse(cleaned.toLowerCase().contains("<ul"));
        assertFalse(cleaned.toLowerCase().contains("<table"));
        assertFalse(cleaned.toLowerCase().contains("<img"));
        assertFalse(cleaned.toLowerCase().contains("onerror"));
        assertTrue(cleaned.contains("留下"));
        // 被拆掉的标签文字仍可能作为纯文本留下
        assertTrue(cleaned.contains("列表"));
        assertTrue(cleaned.contains("格"));
    }

    @Test
    void turnsPlainNewlineIntoBreak() {
        String cleaned = TextSafeUtils.sanitizeNoticeHtml("第一行\n第二行");
        assertTrue(cleaned.contains("<br>"));
        assertEquals("第一行 第二行", TextSafeUtils.noticeHtmlToPlain(cleaned));
    }

    @Test
    void nullStaysNull() {
        assertNull(TextSafeUtils.sanitizeNoticeHtml(null));
        assertEquals("", TextSafeUtils.noticeHtmlToPlain(null));
    }
}

package com.juliawalker.personalblog.infrastructure.markdown;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;

import com.vladsch.flexmark.ext.anchorlink.AnchorLinkExtension;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;

/**
 * Renders article Markdown to HTML. The output is sanitized with an allowlist, so it is
 * the only HTML that templates may print with {@code th:utext}.
 */
@Service
public class MarkdownService {

    // only used so jsoup keeps relative links (/articles/..., #heading); never appears in the output
    private static final String BASE_URI = "https://blog.invalid/";

    private final Parser parser;
    private final HtmlRenderer renderer;
    private final Safelist safelist;
    private final Document.OutputSettings outputSettings;

    public MarkdownService() {
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, List.of(
                TablesExtension.create(),
                StrikethroughExtension.create(),
                TaskListExtension.create(),
                AutolinkExtension.create(),
                AnchorLinkExtension.create()));
        options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");
        this.parser = Parser.builder(options).build();
        this.renderer = HtmlRenderer.builder(options).build();
        this.safelist = Safelist.relaxed()
                .addTags("del", "hr", "input")
                .addAttributes("a", "id")
                .addAttributes("h1", "id")
                .addAttributes("h2", "id")
                .addAttributes("h3", "id")
                .addAttributes("h4", "id")
                .addAttributes("h5", "id")
                .addAttributes("h6", "id")
                .addAttributes("code", "class")
                .addAttributes("li", "class")
                .addAttributes("input", "type", "class", "checked", "disabled", "readonly")
                .addEnforcedAttribute("input", "disabled", "disabled")
                .addProtocols("a", "href", "#")
                .preserveRelativeLinks(true);
        this.outputSettings = new Document.OutputSettings().prettyPrint(false);
    }

    public String toHtml(String markdown) {
        if (markdown == null || markdown.isBlank())
            return "";
        String html = renderer.render(parser.parse(markdown));
        return Jsoup.clean(html, BASE_URI, safelist, outputSettings);
    }

}

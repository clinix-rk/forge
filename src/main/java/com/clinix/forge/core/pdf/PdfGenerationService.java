package com.clinix.forge.core.pdf;

import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.FontStyle;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import jakarta.annotation.PostConstruct;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
public class PdfGenerationService {

    private final SpringTemplateEngine templateEngine;
    private byte[] cachedFontBytes;

    public PdfGenerationService(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @PostConstruct
    public void init() throws Exception {
        // Loads the font into memory exactly once during application startup
        ClassPathResource fontResource = new ClassPathResource("static/fonts/NotoSerif-Regular.ttf");
        try (InputStream inputStream = fontResource.getInputStream()) {
            this.cachedFontBytes = inputStream.readAllBytes();
        }
    }

    public byte[] generatePdf(String templateName, Context context) throws Exception {
        String htmlContent = templateEngine.process(templateName, context);

        Document jsoupDoc = Jsoup.parse(htmlContent, "UTF-8");
        org.w3c.dom.Document w3cDoc = new W3CDom().fromJsoup(jsoupDoc);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();

            builder.useFastMode();
            builder.withW3cDocument(w3cDoc, "/");

            // Supplies the cached font bytes via an in-memory stream, bypassing disk I/O
            builder.useFont(
                    () -> new ByteArrayInputStream(cachedFontBytes),
                    "NotoSerif",
                    400,
                    FontStyle.NORMAL,
                    true
            );

            builder.toStream(outputStream);
            builder.run();

            return outputStream.toByteArray();
        }
    }
}

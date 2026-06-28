package com.clinix.forge.core.pdf;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.net.URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfGenerationService {

    private final SpringTemplateEngine templateEngine;

    public byte[] generatePdf(String templateName, Context context) {
        log.info("Generating PDF from template: {}", templateName);
        try {
            String html = templateEngine.process(templateName, context);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();

            URL staticFolder = getClass().getResource("/static/");
            String baseUri = staticFolder != null ? staticFolder.toExternalForm() : "";

            builder.withHtmlContent(html, baseUri);
            builder.toStream(bos);
            builder.run();
            return bos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate PDF from template: {}", templateName, e);
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        }
    }
}

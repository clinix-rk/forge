package com.clinix.forge.prescription;

import com.clinix.forge.core.pdf.dto.PrescriptionPdfData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class PrescriptionPdfTemplateTest {

    private SpringTemplateEngine templateEngine;

    @BeforeEach
    void setUp() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");

        templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }

    @Test
    void testPrescriptionTemplateMalePronoun() {
        PrescriptionPdfData data = new PrescriptionPdfData(
                "C123",
                "John Doe",
                "15/08/2026",
                "35 yrs",
                "Male",
                "Details test",
                Collections.emptyList(),
                "Dr. Smith",
                "Root Canal"
        );

        Context context = new Context();
        context.setVariable("rx", data);
        context.setVariable("referralType", "standard");

        String html = templateEngine.process("pdf/prescription", context);

        assertThat(html).contains("<span>35 yrs</span> / <span>Male</span>");
        assertThat(html).contains("<span>He</span>");
        assertThat(html).contains("is under your medical treatment");
        assertThat(html).doesNotContain("<span>She</span>");
        assertThat(html).doesNotContain("<span>He/She</span>");
    }

    @Test
    void testPrescriptionTemplateFemalePronoun() {
        PrescriptionPdfData data = new PrescriptionPdfData(
                "C124",
                "Jane Doe",
                "15/08/2026",
                "28 yrs",
                "Female",
                "Details test",
                Collections.emptyList(),
                "Dr. Smith",
                "Root Canal"
        );

        Context context = new Context();
        context.setVariable("rx", data);
        context.setVariable("referralType", "extended");

        String html = templateEngine.process("pdf/prescription", context);

        assertThat(html).contains("<span>28 yrs</span> / <span>Female</span>");
        assertThat(html).contains("<span>She</span>");
        assertThat(html).contains("is on blood thinner medicines");
        assertThat(html).doesNotContain("<span>He</span>");
        assertThat(html).doesNotContain("<span>He/She</span>");
    }

    @Test
    void testPrescriptionTemplateOtherPronoun() {
        PrescriptionPdfData data = new PrescriptionPdfData(
                "C125",
                "Alex Doe",
                "15/08/2026",
                "40 yrs",
                "Other",
                "Details test",
                Collections.emptyList(),
                "Dr. Smith",
                "Root Canal"
        );

        Context context = new Context();
        context.setVariable("rx", data);
        context.setVariable("referralType", "standard");

        String html = templateEngine.process("pdf/prescription", context);

        assertThat(html).contains("<span>40 yrs</span> / <span>Other</span>");
        assertThat(html).contains("<span>He/She</span>");
        assertThat(html).contains("is under your medical treatment");
    }
}

package com.clinix.forge.core.logging;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * The silent watcher at the gates.
 * <p>
 * Intercepts every incoming envoy (HTTP request), branding them with a unique sigil (Trace ID)
 * so their journey through the citadel's halls can be tracked in the archives (logs).
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // Ensures this intercepts before authentication or routing
public class LoggingFilter implements Filter {

    private static final String TRACE_ID = "traceId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Extract a fast, 8-character sigil from a UUID
        String id = UUID.randomUUID().toString().substring(0, 8);

        if (request instanceof HttpServletRequest req) {
            if (log.isDebugEnabled()) {
                log.debug("Incoming request: {} {} from {}", req.getMethod(), req.getRequestURI(), req.getRemoteAddr());
            } else {
                log.trace("Incoming request: {} {}", req.getMethod(), req.getRequestURI());
            }
        }

        try {
            // Etch the mark into the ThreadLocal ledger
            MDC.put(TRACE_ID, id);

            // Allow the request to pass into the inner keep
            chain.doFilter(request, response);
        } finally {
            // Ensure the mark is wiped clean as the envoy departs to prevent contamination
            MDC.remove(TRACE_ID);
        }
    }
}
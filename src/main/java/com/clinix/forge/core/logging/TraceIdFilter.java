package com.clinix.forge.core.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class TraceIdFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TraceIdFilter.class);
    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String MDC_TRACE_ID_KEY = "traceId";
    private static final String MDC_USER_ID_KEY = "userId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Extract traceId from request header or generate new one
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }

        // 2. Store traceId in MDC (thread-local storage)
        MDC.put(MDC_TRACE_ID_KEY, traceId);

        // 3. Extract userId if available (optional)
        String userId = extractUserIdFromRequest(request);
        if (userId != null) {
            MDC.put(MDC_USER_ID_KEY, userId);
        }

        // 4. Add traceId to response header
        response.setHeader(TRACE_ID_HEADER, traceId);

        log.info("Incoming request: {} {} | Method: {} | IP: {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getMethod(),
                request.getRemoteAddr()
        ); // traceId automatically included via MDC

        try {
            // 5. Continue filter chain (all downstream logs will include traceId)
            filterChain.doFilter(request, response);

            log.info("Request completed successfully");
        } catch (Exception e) {
            log.error("Request processing failed", e);
            throw e;
        } finally {
            // 6. CRITICAL: Clear MDC to prevent memory leaks
            MDC.clear();
        }
    }

    private String extractUserIdFromRequest(HttpServletRequest request) {
        // Example: Extract from JWT token or session
        // This is placeholder logic
        Object userId = request.getAttribute("userId");
        return userId != null ? userId.toString() : null;
    }
}

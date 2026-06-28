package com.clinix.forge.core.payload;

import org.springframework.data.domain.Page;
import java.util.List;

public record PaginatedPayload<T>(
        List<T> items,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isLast
) {
    /**
     * Factory method to seamlessly convert a Spring Data Page into our typed payload.
     */
    public static <T> PaginatedPayload<T> of(Page<T> page) {
        return new PaginatedPayload<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    /**
     * Overloaded factory for when entities must be mapped to DTOs before wrapping.
     */
    public static <T> PaginatedPayload<T> of(List<T> mappedItems, Page<?> originalPage) {
        return new PaginatedPayload<>(
                mappedItems,
                originalPage.getNumber(),
                originalPage.getSize(),
                originalPage.getTotalElements(),
                originalPage.getTotalPages(),
                originalPage.isLast()
        );
    }
}
package com.tennisclub.reservations.model.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Represents a simplified paginated API response.
 *
 * @param content page content.
 * @param page page metadata.
 * @param <T> content item type.
 */
public record PaginatedResponse<T>(List<T> content, PageMetadata page) {

    /**
     * Creates a simplified response from a Spring page.
     *
     * @param page source page.
     * @return simplified paginated response.
     * @param <T> content item type.
     */
    public static <T> PaginatedResponse<T> from(Page<T> page) {
        return new PaginatedResponse<>(
                page.getContent(),
                new PageMetadata(
                        page.getNumber(),
                        page.getSize(),
                        page.getNumberOfElements(),
                        page.getTotalElements(),
                        page.getTotalPages()
                )
        );
    }

    /**
     * Represents pagination metadata.
     *
     * @param number current page number.
     * @param size requested page size.
     * @param numberOfElements number of elements in the current page.
     * @param totalElements total number of elements.
     * @param totalPages total number of pages.
     */
    public record PageMetadata(
            int number,
            int size,
            int numberOfElements,
            long totalElements,
            int totalPages
    ) {
    }
}

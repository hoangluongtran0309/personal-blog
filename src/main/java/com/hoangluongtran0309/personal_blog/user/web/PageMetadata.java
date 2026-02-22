package com.hoangluongtran0309.personal_blog.user.web;

import org.springframework.data.domain.Page;

public record PageMetadata(
        int currentPage,
        int totalPages,
        long totalElements,
        int numberOfElements,
        boolean hasPrevious,
        boolean hasNext,
        int startPage,
        int endPage) {
            
    public static PageMetadata of(Page<?> page) {
        int current = page.getNumber();
        int total = page.getTotalPages();
        int start = Math.max(0, current - 2);
        int end = Math.min(total - 1, current + 2);

        return new PageMetadata(
                current,
                total,
                page.getTotalElements(),
                page.getNumberOfElements(),
                page.hasPrevious(),
                page.hasNext(),
                start,
                end);
    }

}
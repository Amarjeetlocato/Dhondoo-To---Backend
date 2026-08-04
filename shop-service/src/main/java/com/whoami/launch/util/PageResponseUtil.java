package com.whoami.launch.util;


import org.springframework.data.domain.Page;

import com.whoami.launch.dto.PageResponse;

public final class PageResponseUtil {

    private PageResponseUtil() {
    }

    public static <T> PageResponse<T> from(Page<T> page) {

        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
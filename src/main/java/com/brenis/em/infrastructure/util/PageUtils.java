package com.brenis.em.infrastructure.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public final class PageUtils {

    private PageUtils() {}

    public static <T> Page<T> toPage(List<T> list, Pageable pageable) {
        if (list.isEmpty()) return new PageImpl<>(List.of(), pageable, 0);
        int start = Math.min((int) pageable.getOffset(), list.size());
        int end = Math.min(start + pageable.getPageSize(), list.size());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }
}

package io.github.lucasmbc.ecommerceapi.controller.mapper;

import io.github.lucasmbc.ecommerceapi.controller.dto.response.PageResponseDTO;
import org.springframework.data.domain.Page;

public final class PageMapper {
    private PageMapper() {
    }

    public static <T> PageResponseDTO<T> toResponse(Page<T> page) {
        return new PageResponseDTO<>(
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

package com.whoami.launch.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {

    private List<T> content;

    private int number;
    private int size;

    private long totalElements;
    private int totalPages;

    private boolean first;
    private boolean last;

    public PageResponse(List<T> content, int number, int size, long totalElements, int totalPages, boolean last) {
        this.content = content;
        this.number = number;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = (number == 0);
        this.last = last;
    }

    public int getPageNumber() {
        return number;
    }

    public int getPageSize() {
        return size;
    }
}

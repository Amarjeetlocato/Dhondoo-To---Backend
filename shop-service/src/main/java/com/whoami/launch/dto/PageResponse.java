package com.whoami.launch.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
}
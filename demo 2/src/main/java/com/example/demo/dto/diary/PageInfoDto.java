package com.example.demo.dto.diary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PageInfoDto {
    private int totalPage;
    private int currentPage;
    private int numberOfElements;
    private boolean isNext;

    public PageInfoDto(Page result) {
        this.totalPage = result.getTotalPages();
        this.currentPage = result.getNumber();
        this.numberOfElements = result.getNumberOfElements();
        this.isNext = result.hasNext();
    }
}

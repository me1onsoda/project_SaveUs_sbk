package com.ysb.library.dto;

import java.util.List;

public class ResultDto {
    private List<TestDto> items;

    public ResultDto(List<TestDto> items) {
        this.items = items;
    }

    public List<TestDto> getItems() {
        return items;
    }

    public void setItems(List<TestDto> items) {
        this.items = items;
    }

    public void printItems() {
        for (TestDto item : this.items) {
            System.out.println(item);
        }
    }
}

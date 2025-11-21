package com.example.Ex02.dto;

import java.util.List;

// 응답으로 돌아온 JSON 객체를 받아오는 역할만 수행
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

    package com.example.Ex02.dto;

    import java.util.List;

    public class AiResponseWrapper {
        private List<AiDto> items;

        public List<AiDto> getItems() { return items; }
        public void setItems(List<AiDto> items) { this.items = items; }
    }
package com.backend.immilog.jobboard.domain.model;

public enum Industry {
    ALL("all"),
    IT("IT"),
    MARKETING("마케팅"),
    DESIGN("디자인"),
    SALES("영업"),
    FINANCE("금융"),
    HR("인사"),
    SERVICE("서비스"),
    ARCHITECTURE("건축"),
    ETC("기타");

    private final String industry;

    Industry(String industry) {
        this.industry = industry;
    }
}
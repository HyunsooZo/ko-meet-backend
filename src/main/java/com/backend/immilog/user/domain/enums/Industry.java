package com.backend.immilog.user.domain.enums;

public enum Industry {
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

    public com.backend.immilog.post.domain.enums.Industry toPostIndustry() {
        return com.backend.immilog.post.domain.enums.Industry.valueOf(this.name());
    }
}

package com.backend.immilog.user.infrastructure.jpa.entity.company;

import com.backend.immilog.user.domain.enums.Industry;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PROTECTED)
@Embeddable
public class CompanyDataEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "industry")
    private Industry industry;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "company_email")
    private String companyEmail;

    @Column(name = "company_phone")
    private String companyPhone;

    @Column(name = "company_address")
    private String companyAddress;

    @Column(name = "company_homepage")
    private String companyHomepage;

    @Column(name = "company_logo")
    private String companyLogo;

    protected CompanyDataEntity() {}

    protected CompanyDataEntity(
            Industry industry,
            String companyName,
            String companyEmail,
            String companyPhone,
            String companyAddress,
            String companyHomepage,
            String companyLogo
    ) {
        this.industry = industry;
        this.companyName = companyName;
        this.companyEmail = companyEmail;
        this.companyPhone = companyPhone;
        this.companyAddress = companyAddress;
        this.companyHomepage = companyHomepage;
        this.companyLogo = companyLogo;
    }

    public static CompanyDataEntity of(
            Industry industry,
            String companyName,
            String companyEmail,
            String companyPhone,
            String companyAddress,
            String companyHomepage,
            String companyLogo
    ) {
        return new CompanyDataEntity(
                industry,
                companyName,
                companyEmail,
                companyPhone,
                companyAddress,
                companyHomepage,
                companyLogo
        );
    }
}

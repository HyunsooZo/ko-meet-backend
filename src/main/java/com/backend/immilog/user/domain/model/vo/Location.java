package com.backend.immilog.user.domain.model.vo;

import com.backend.immilog.user.domain.model.enums.UserCountry;
import lombok.*;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class Location {
    @Enumerated(EnumType.STRING)
    private UserCountry country;
    private String region;

    public static Location of(
            UserCountry country,
            String region
    ) {
        return Location.builder()
                .country(country)
                .region(region)
                .build();
    }
}

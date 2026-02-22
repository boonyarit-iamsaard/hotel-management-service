package me.boonyarit.hotel.management.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

@Getter
@ConfigurationProperties(prefix = "spring.security.oauth2.resourceserver.jwt")
@Validated
public class SecurityProperties {

    private final String issuerUri;

    private final String audiences;

    public SecurityProperties(
            @NotBlank(message = "JWT issuer URI must be configured") String issuerUri,
            @NotBlank(message = "JWT audience must be configured") String audiences) {
        this.issuerUri = issuerUri;
        this.audiences = audiences;
    }

    public String[] getAudiencesArray() {
        return StringUtils.commaDelimitedListToStringArray(audiences);
    }
}

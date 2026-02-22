package me.boonyarit.hotel.management.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.StringUtils;

import lombok.Getter;

@Getter
@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {

    private final String allowedOrigins;
    private final String allowedMethods;
    private final String allowedHeaders;
    private final String exposedHeaders;
    private final boolean allowCredentials;
    private final long maxAge;

    public CorsProperties(
            @DefaultValue("http://localhost:3000,http://localhost:4200,http://localhost:8080") String allowedOrigins,
            @DefaultValue("GET,POST,PUT,DELETE,OPTIONS,PATCH") String allowedMethods,
            @DefaultValue("Authorization,Content-Type,Accept,X-Requested-With,Origin") String allowedHeaders,
            @DefaultValue("Authorization,Content-Type") String exposedHeaders,
            @DefaultValue("true") boolean allowCredentials,
            @DefaultValue("3600") long maxAge) {
        this.allowedOrigins = allowedOrigins;
        this.allowedMethods = allowedMethods;
        this.allowedHeaders = allowedHeaders;
        this.exposedHeaders = exposedHeaders;
        this.allowCredentials = allowCredentials;
        this.maxAge = maxAge;
    }

    public String[] getAllowedOriginsArray() {
        return StringUtils.commaDelimitedListToStringArray(allowedOrigins);
    }

    public String[] getAllowedMethodsArray() {
        return StringUtils.commaDelimitedListToStringArray(allowedMethods);
    }

    public String[] getAllowedHeadersArray() {
        return StringUtils.commaDelimitedListToStringArray(allowedHeaders);
    }

    public String[] getExposedHeadersArray() {
        return StringUtils.commaDelimitedListToStringArray(exposedHeaders);
    }
}

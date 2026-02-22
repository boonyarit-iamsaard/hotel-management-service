package me.boonyarit.hotel.management.service;

import java.util.Collections;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.boonyarit.hotel.management.config.SecurityProperties;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserInfoService {

    private static final ParameterizedTypeReference<Map<String, Object>> MAP_TYPE = new ParameterizedTypeReference<>() {
    };

    private final SecurityProperties securityProperties;
    private final RestClient restClient;

    private String getUserInfoUrl() {
        String issuerUri = securityProperties.getIssuerUri();
        return issuerUri.endsWith("/")
                ? issuerUri + "userinfo"
                : issuerUri + "/userinfo";
    }

    public Map<String, Object> fetchUserInfo(String accessToken) {
        String userInfoUrl = getUserInfoUrl();
        try {
            Map<String, Object> result = restClient.get()
                    .uri(userInfoUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .body(MAP_TYPE);
            return result != null ? result : Collections.emptyMap();
        } catch (RestClientException e) {
            log.debug("Failed to fetch user info from {}", userInfoUrl, e);
            log.error("Failed to fetch user info: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }
}

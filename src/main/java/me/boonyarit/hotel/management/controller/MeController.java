package me.boonyarit.hotel.management.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import me.boonyarit.hotel.management.dto.response.UserInfoResponse;
import me.boonyarit.hotel.management.service.UserInfoService;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    private final UserInfoService userInfoService;

    @GetMapping
    public ResponseEntity<UserInfoResponse> getCurrentUser(Authentication authentication) {
        // Note: This endpoint requires authentication per SecurityConfig.
        // The authentication parameter will never be null when this method is invoked.
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Map<String, Object> claims = new HashMap<>();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            claims.putAll(jwtAuth.getTokenAttributes());
            // Enrich with profile claims from the /userinfo endpoint
            Map<String, Object> userInfo = userInfoService.fetchUserInfo(jwtAuth.getToken().getTokenValue());
            userInfo.forEach(claims::putIfAbsent);
        }

        return ResponseEntity.ok(UserInfoResponse.builder()
                .principalName(authentication.getName())
                .email(getStringClaim(claims, "email"))
                .name(getStringClaim(claims, "name"))
                .nickname(getStringClaim(claims, "nickname"))
                .picture(getStringClaim(claims, "picture"))
                .subject(getStringClaim(claims, "sub"))
                .authorities(authorities)
                .authenticated(authentication.isAuthenticated())
                .build());
    }

    private String getStringClaim(Map<String, Object> claims, String key) {
        Object value = claims.get(key);
        return value != null ? value.toString() : null;
    }
}

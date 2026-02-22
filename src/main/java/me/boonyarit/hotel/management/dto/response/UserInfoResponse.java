package me.boonyarit.hotel.management.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record UserInfoResponse(
        String principalName,
        String email,
        String name,
        String nickname,
        String picture,
        String subject,
        List<String> authorities,
        boolean authenticated) {
}

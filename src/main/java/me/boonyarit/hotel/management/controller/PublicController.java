package me.boonyarit.hotel.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    @GetMapping
    public ResponseEntity<Map<String, String>> getPublicMessage() {
        return ResponseEntity.ok(Map.of(
                "message", "This is a public endpoint accessible without authentication",
                "status", "success"));
    }
}

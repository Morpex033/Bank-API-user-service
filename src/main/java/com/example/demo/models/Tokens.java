package com.example.demo.models;

import java.time.Duration;

public record Tokens(String accessToken, String accessTokenExpiry, String refreshToken, String refreshTokenExpiry) {
}

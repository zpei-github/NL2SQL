package com.web.data;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class TokenResponse {
    private String accessToken;
    private String refreshToken;

    public TokenResponse() {}
    public TokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}

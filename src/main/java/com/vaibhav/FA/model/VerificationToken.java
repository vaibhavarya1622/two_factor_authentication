package com.vaibhav.FA.model;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash("token")
public record VerificationToken(@Id @NotNull String user, @TimeToLive Long expiry, String value) {
}

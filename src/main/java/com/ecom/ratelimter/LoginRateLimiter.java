package com.ecom.ratelimter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimiter {
    private final Map<String, Bucket> cache=new ConcurrentHashMap<>();

    public boolean allowRequest(String key){
        Bucket bucket=cache.computeIfAbsent(key,k->
                Bucket.builder()
                        .addLimit(Bandwidth.simple(5, Duration.ofMinutes(1)))
                        .build());
        return bucket.tryConsume(1);

    }
}

package com.example.limitation.configs;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Supplier;

@Service
public class RateLimiterConfig {

    @Autowired
    ProxyManager<String> proxyManager;

    public Bucket resolveBucket(String auth) {
        Supplier<BucketConfiguration> configSupplier = getConfigSupplierForAuthority(auth);

        return proxyManager.builder().build(auth, configSupplier);
    }

    private Supplier<BucketConfiguration> getConfigSupplierForAuthority(String auth) {

        if (Objects.equals(auth, "ADMIN")) {
            Refill refill = Refill.intervally(10, Duration.ofMinutes(1));
            Bandwidth limit = Bandwidth.classic(10, refill);
            return () -> (BucketConfiguration.builder().addLimit(limit).build());
        } else if (Objects.equals(auth, "USER")) {
            Refill refill = Refill.intervally(5, Duration.ofMinutes(1));
            Bandwidth limit = Bandwidth.classic(5, refill);
            return () -> (BucketConfiguration.builder().addLimit(limit).build());
        } else {
            throw new RuntimeException("user not found.");
        }
    }
}

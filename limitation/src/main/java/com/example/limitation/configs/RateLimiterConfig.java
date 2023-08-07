package com.example.limitation.configs;

import com.example.limitation.domain.user.UserModel;
import com.example.limitation.domain.user.UserService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class RateLimiterConfig {

    @Autowired
    UserService userService;

    @Autowired
    ProxyManager<String> proxyManager;

    public Bucket resolveBucket(UUID key) {
        Supplier<BucketConfiguration> configSupplier = getConfigSupplierForUserKey(key);

        return proxyManager.builder().build(String.valueOf(key), configSupplier);
    }

    private Supplier<BucketConfiguration> getConfigSupplierForUserKey(UUID userId) {
        Optional<UserModel> user = userService.getUser(userId);

        if (user.isPresent()) {
            Refill refill = Refill.intervally(user.get().getLimit(), Duration.ofMinutes(1));
            Bandwidth limit = Bandwidth.classic(user.get().getLimit(), refill);
            return () -> (BucketConfiguration.builder().addLimit(limit).build());
        } else {
            throw new RuntimeException("user not found.");
        }
    }
}

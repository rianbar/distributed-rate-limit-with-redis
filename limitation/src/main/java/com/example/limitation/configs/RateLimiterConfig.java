package com.example.limitation.configs;

import com.example.limitation.domain.services.UserService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Supplier;

@Service
public class RateLimiterConfig {

    @Autowired
    UserService userService;

    @Autowired
    ProxyManager<String> proxyManager;

    public Bucket resolveBucket(String name) {
        Supplier<BucketConfiguration> configSupplier = getConfigSupplierForUsername(name);

        return proxyManager.builder().build(name, configSupplier);
    }

    private Supplier<BucketConfiguration> getConfigSupplierForUsername(String name) {
        UserDetails user = userService.loadUserByUsername(name);
        //get currently authority
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String first = String.valueOf(auth.getAuthorities().iterator().next());

        if (Objects.equals(first, "ADMIN")) {
            Refill refill = Refill.intervally(10, Duration.ofMinutes(1));
            Bandwidth limit = Bandwidth.classic(10, refill);
            return () -> (BucketConfiguration.builder().addLimit(limit).build());
        } else if (Objects.equals(first, "USER")) {
            Refill refill = Refill.intervally(5, Duration.ofMinutes(1));
            Bandwidth limit = Bandwidth.classic(5, refill);
            return () -> (BucketConfiguration.builder().addLimit(limit).build());
        } else {
            throw new RuntimeException("user not found.");
        }
    }
}

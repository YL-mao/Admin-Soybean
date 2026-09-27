package com.ylmao.admin.config;

import com.ylmao.admin.utils.ServletUtils;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** 可信反代地址列表，供 ServletUtils 采信 X-Real-IP（由 AdminApp @EnableConfigurationProperties 注册）。 */
@Getter
@ConfigurationProperties(prefix = "app.client-ip")
public class ClientIpProperties {

    private static final Logger log = LoggerFactory.getLogger(ClientIpProperties.class);

    private static final Set<String> LOOPBACK = Set.of(
            "127.0.0.1",
            "::1",
            "0:0:0:0:0:0:0:1");

    /** 对这些对端 IP 采信 X-Real-IP；默认含本机回环。 */
    private List<String> trustedProxies = new ArrayList<>(List.of(
            "127.0.0.1",
            "::1",
            "0:0:0:0:0:0:0:1"));

    public void setTrustedProxies(List<String> trustedProxies) {
        this.trustedProxies = trustedProxies != null ? trustedProxies : List.of();
    }

    @PostConstruct
    void apply() {
        ServletUtils.setTrustedProxies(trustedProxies);
        log.info("客户端 IP 可信反代: {}", trustedProxies);
        boolean onlyLoopback = trustedProxies.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .allMatch(LOOPBACK::contains);
        if (onlyLoopback) {
            // 异机/Docker Nginx 对端非回环时必须追加，否则不采信 X-Real-IP。
            log.warn("app.client-ip.trusted-proxies 仅含本机回环；Docker/异机反代请把对端地址加入该列表");
        }
    }
}

package com.ylmao.admin.config;

import com.ylmao.admin.utils.ServletUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/** 可信反代地址列表，供 ServletUtils 采信 X-Real-IP（由 AdminApp @EnableConfigurationProperties 注册）。 */
@ConfigurationProperties(prefix = "app.client-ip")
public class ClientIpProperties {

    /** 对这些对端 IP 采信 X-Real-IP；默认含本机回环。 */
    private List<String> trustedProxies = new ArrayList<>(List.of(
            "127.0.0.1",
            "::1",
            "0:0:0:0:0:0:0:1"));

    public List<String> getTrustedProxies() {
        return trustedProxies;
    }

    public void setTrustedProxies(List<String> trustedProxies) {
        this.trustedProxies = trustedProxies != null ? trustedProxies : List.of();
    }

    @PostConstruct
    void apply() {
        ServletUtils.setTrustedProxies(trustedProxies);
    }
}

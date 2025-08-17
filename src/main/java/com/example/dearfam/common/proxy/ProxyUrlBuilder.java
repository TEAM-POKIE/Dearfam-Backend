package com.example.dearfam.common.proxy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Component
public class ProxyUrlBuilder {
    @Value("${app.proxy.enable:true}")
    private boolean proxyEnable;

    @Value("#{'${app.proxy.allow-hosts}'.split(',')}")
    private List<String> allowHosts;

    public String toProxied(String externalUrl) {
        if (!proxyEnable || externalUrl == null || externalUrl.isBlank()) {
            return externalUrl;
        }

        try {
            URI uri = URI.create(externalUrl);
            String host = Optional.ofNullable(uri.getHost()).orElse("").toLowerCase();

            // 허용된 호스트 목록에 있는 경우에만 프록시 URL로 변환
            boolean isAllowed = allowHosts.stream().anyMatch(h -> h.equalsIgnoreCase(host));
            if (isAllowed) {
                return "/proxy/fetch?url=" + URLEncoder.encode(externalUrl, StandardCharsets.UTF_8);
            }
        } catch (IllegalArgumentException e) {
            // URL 형식이 잘못된 경우 원본을 그대로 반환
            return externalUrl;
        }

        // 허용되지 않은 호스트는 원본 URL을 그대로 반환
        return externalUrl;
    }
}

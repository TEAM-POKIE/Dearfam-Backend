package com.example.dearfam.common.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class ProxyUrlBuilder {
    @Value("${app.proxy.enable:true}") private boolean proxyEnable;
    @Value("${app.proxy.base-url}") private String apiBaseUrl;        // ★ 추가
    @Value("${cdn.domain}") private String cdnDomain;
    @Value("${cloud.aws.s3.bucket}") private String bucket;
    @Value("${app.proxy.allow-hosts}") private List<String> allowHosts;

    public String toProxied(String externalUrl) {
        if (!proxyEnable || externalUrl == null || externalUrl.isBlank()) return externalUrl;

        // S3 주소면 CloudFront로 정규화
        externalUrl = toCdnIfS3(externalUrl);

        try {
            String host = Optional.ofNullable(URI.create(externalUrl).getHost()).orElse("").toLowerCase();
            boolean allowed = allowHosts.stream().anyMatch(h -> h.equalsIgnoreCase(host));
            if (!allowed) return externalUrl;
        } catch (Exception e) {
            return externalUrl;
        }

        // ★ 반드시 절대 URL로 반환
        String q = URLEncoder.encode(externalUrl, StandardCharsets.UTF_8);
        return apiBaseUrl + "/proxy/fetch?url=" + q;
    }

    private String toCdnIfS3(String url) {
        try {
            URI u = URI.create(url);
            String host = Optional.ofNullable(u.getHost()).orElse("").toLowerCase();
            if (host.startsWith((bucket + ".s3").toLowerCase())) {
                String path = Optional.ofNullable(u.getRawPath()).orElse("/");
                String cdnUrl = "https://" + cdnDomain + path;
                log.info("CDN 변환 URL: {}", cdnUrl);
                return cdnUrl;
            }
        } catch (Exception ignore) {}
        return url;
    }
}
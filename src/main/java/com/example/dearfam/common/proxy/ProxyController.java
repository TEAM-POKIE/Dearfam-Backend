package com.example.dearfam.common.proxy;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/proxy")
public class ProxyController {

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    @Value("${app.proxy.enable:true}")
    boolean proxyEnable;

    @Value("${app.proxy.allow-hosts}")
    List<String> allowHosts;

    @Value("${app.proxy.cache-max-age-seconds}")
    int cacheMaxAge;

    @GetMapping("/fetch")
    public ResponseEntity<Resource> fetch(@RequestParam String url,
                                          @RequestHeader(value = "Range", required = false) String range) throws Exception {
        if (!proxyEnable) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
        }

        URI uri;
        try {
            uri = URI.create(url);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        // 허용된 호스트가 아니면 요청을 거부하여 오픈 프록시 방지
        String host = Optional.ofNullable(uri.getHost()).orElse("").toLowerCase();
        if (allowHosts.stream().noneMatch(h -> h.equalsIgnoreCase(host))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(20))
                .header("Accept", "*/*");

        if (range != null && !range.isBlank()) {
            requestBuilder.header("Range", range);
        }

        HttpResponse<InputStream> response = http.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
        int statusCode = response.statusCode();

        // 200 (OK) 또는 206 (Partial Content)이 아니면 원본 응답 코드를 그대로 반환
        if (statusCode != 200 && statusCode != 206) {
            return ResponseEntity.status(statusCode).build();
        }

        HttpHeaders headers = new HttpHeaders();
        String contentType = response.headers().firstValue("content-type").orElseGet(() -> guessContentType(uri.getPath()));
        headers.set(HttpHeaders.CONTENT_TYPE, contentType);
        response.headers().firstValue("content-range").ifPresent(v -> headers.set(HttpHeaders.CONTENT_RANGE, v));
        response.headers().firstValue("etag").ifPresent(v -> headers.set("ETag", v));
        headers.setCacheControl("public, max-age=" + cacheMaxAge);

        HttpStatus responseStatus = (statusCode == 206) ? HttpStatus.PARTIAL_CONTENT : HttpStatus.OK;
        return new ResponseEntity<>(new InputStreamResource(response.body()), headers, responseStatus);
    }

    private String guessContentType(String path) {
        if (path == null) return "application/octet-stream";
        String lowerPath = path.toLowerCase();
        if (lowerPath.endsWith(".png")) return "image/png";
        if (lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg")) return "image/jpeg";
        if (lowerPath.endsWith(".webp")) return "image/webp";
        if (lowerPath.endsWith(".mp4")) return "video/mp4";
        return "application/octet-stream";
    }

}

package com.example.demo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
public class PreviewController {

    private static final Logger logger = LogManager.getLogger(PreviewController.class);
    private static final Set<String> ALLOWED_HOSTS = Set.of("example.com", "www.example.com");
    private final RestTemplate restTemplate;

    public PreviewController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/api/preview")
    public ResponseEntity<Map<String, Object>> preview(@RequestBody PreviewRequest request) {
        logger.info("Fetching preview for URL={}", request.getUrl());

        URI targetUri;
        try {
            targetUri = new URI(request.getUrl());
        } catch (URISyntaxException | NullPointerException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid URL"));
        }

        String scheme = targetUri.getScheme();
        String host = targetUri.getHost();
        if (scheme == null || host == null
                || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
                || !isAllowedHost(host)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "URL is not allowed"));
        }

        String body = restTemplate.getForObject(targetUri.toString(), String.class);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("requestedUrl", targetUri.toString());
        response.put("preview", body);
        return ResponseEntity.ok(response);
    }

    private boolean isAllowedHost(String host) {
        return ALLOWED_HOSTS.contains(host.toLowerCase());
    }
}

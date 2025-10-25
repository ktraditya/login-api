package com.example.curlapiproxy.controller;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.curlapiproxy.model.ApiRequest;
import com.example.curlapiproxy.model.CurlRequest;
import com.example.curlapiproxy.model.CurlResponse;
import com.example.curlapiproxy.service.CurlExecutorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/curl")
@CrossOrigin(origins = "*")
public class CurlController {
    
    private static final Logger logger = LoggerFactory.getLogger(CurlController.class);
    
    @Autowired
    private CurlExecutorService curlExecutorService;
    
    @PostMapping("/execute")
    public CompletableFuture<ResponseEntity<CurlResponse>> executeCurl(@Valid @RequestBody ApiRequest apiRequest) {
        logger.info("Received API request: {}", apiRequest);
        
        // Convert ApiRequest to CurlRequest based on environment and smid
        CurlRequest curlRequest = buildCurlRequest(apiRequest);
        
        return curlExecutorService.executeCurlCommand(curlRequest)
                .thenApply(response -> {
                    if (response.isSuccess()) {
                        return ResponseEntity.ok(response);
                    } else {
                        return ResponseEntity.badRequest().body(response);
                    }
                });
    }
    
    @PostMapping("/execute-raw")
    public CompletableFuture<ResponseEntity<CurlResponse>> executeRawCurl(@Valid @RequestBody CurlRequest request) {
        logger.info("Received raw curl request: {}", request);
        
        return curlExecutorService.executeCurlCommand(request)
                .thenApply(response -> {
                    if (response.isSuccess()) {
                        return ResponseEntity.ok(response);
                    } else {
                        return ResponseEntity.badRequest().body(response);
                    }
                });
    }
    
    private CurlRequest buildCurlRequest(ApiRequest apiRequest) {
        String baseUrl = getBaseUrlForEnvironment(apiRequest.getEnv());
        String url = baseUrl + "/api/data/" + apiRequest.getSmid();
        
        // Build curl command parameters with CSRF header and POST data
        String parameters = String.format("-X POST -H 'CSRF: DFSDKJFHDKJFHDSKJFHFOREUEOWIFNVHFDSGORO' -H 'Content-Type: application/json' " +
                                         "--data-raw '{\"clientNumber\":\"%s\",\"callmode\":\"01\"}'", 
                                         apiRequest.getSmid());
        
        logger.info("Built curl request - URL: {}, Params: {}", url, parameters);
        return new CurlRequest(url, parameters);
    }
    
    private String getBaseUrlForEnvironment(String env) {
        return switch (env.toLowerCase()) {
            case "test" -> "https://api-test.example.com";
            case "qa" -> "https://api-qa.example.com";
            case "qap1" -> "https://api-qap1.example.com";
            default -> throw new IllegalArgumentException("Invalid environment: " + env);
        };
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Curl API Proxy is running");
    }
    
    @PostMapping("/get")
    public CompletableFuture<ResponseEntity<CurlResponse>> executeGet(@RequestParam String url, 
                                                                      @RequestParam(required = false, defaultValue = "") String headers) {
        CurlRequest request = new CurlRequest(url, headers.isEmpty() ? "-X GET" : "-X GET " + headers);
        return executeRawCurl(request);
    }
    
    @PostMapping("/post")
    public CompletableFuture<ResponseEntity<CurlResponse>> executePost(@RequestParam String url,
                                                                       @RequestParam(required = false, defaultValue = "") String data,
                                                                       @RequestParam(required = false, defaultValue = "") String headers) {
        StringBuilder params = new StringBuilder("-X POST");
        if (!data.isEmpty()) {
            params.append(" -d \"").append(data).append("\"");
        }
        if (!headers.isEmpty()) {
            params.append(" ").append(headers);
        }
        
        CurlRequest request = new CurlRequest(url, params.toString());
        return executeRawCurl(request);
    }
}
package com.example.curlapiproxy.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CurlRequest {
    @NotBlank(message = "URL is required")
    private String url;
    
    @NotNull(message = "Parameters are required")
    private String parameters;
    
    public CurlRequest() {}
    
    public CurlRequest(String url, String parameters) {
        this.url = url;
        this.parameters = parameters;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getParameters() {
        return parameters;
    }
    
    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
    
    @Override
    public String toString() {
        return "CurlRequest{" +
                "url='" + url + '\'' +
                ", parameters='" + parameters + '\'' +
                '}';
    }
}
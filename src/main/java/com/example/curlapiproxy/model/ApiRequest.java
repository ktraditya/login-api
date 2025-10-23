package com.example.curlapiproxy.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ApiRequest {
    
    @NotBlank(message = "SMID is required")
    private String smid;
    
    @NotBlank(message = "Environment is required")
    @Pattern(regexp = "^(test|qa|qap1)$", message = "Environment must be one of: test, qa, qap1")
    private String env;
    
    public ApiRequest() {}
    
    public ApiRequest(String smid, String env) {
        this.smid = smid;
        this.env = env;
    }
    
    public String getSmid() {
        return smid;
    }
    
    public void setSmid(String smid) {
        this.smid = smid;
    }
    
    public String getEnv() {
        return env;
    }
    
    public void setEnv(String env) {
        this.env = env;
    }
    
    @Override
    public String toString() {
        return "ApiRequest{" +
                "smid='" + smid + '\'' +
                ", env='" + env + '\'' +
                '}';
    }
}
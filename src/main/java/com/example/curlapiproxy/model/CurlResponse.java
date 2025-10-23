package com.example.curlapiproxy.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurlResponse {
    private String output;
    private String error;
    private int exitCode;
    private LocalDateTime timestamp;
    private String command;
    private Long executionTimeMs;
    private Map<String, String> responseHeaders;
    private Integer httpStatusCode;
    private String contentType;
    
    public CurlResponse() {
        this.timestamp = LocalDateTime.now();
        this.responseHeaders = new HashMap<>();
    }
    
    public CurlResponse(String output, String error, int exitCode, String command) {
        this();
        this.output = output;
        this.error = error;
        this.exitCode = exitCode;
        this.command = command;
    }
    
    public CurlResponse(String output, String error, int exitCode, String command, Long executionTimeMs) {
        this(output, error, exitCode, command);
        this.executionTimeMs = executionTimeMs;
    }
    
    public String getOutput() {
        return output;
    }
    
    public void setOutput(String output) {
        this.output = output;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public int getExitCode() {
        return exitCode;
    }
    
    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getCommand() {
        return command;
    }
    
    public void setCommand(String command) {
        this.command = command;
    }
    
    public boolean isSuccess() {
        return exitCode == 0;
    }
    
    // New getters and setters for enhanced fields
    public Long getExecutionTimeMs() {
        return executionTimeMs;
    }
    
    public void setExecutionTimeMs(Long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }
    
    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }
    
    public void setResponseHeaders(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }
    
    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }
    
    public void setHttpStatusCode(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    // Utility methods for better response handling
    public boolean isHttpSuccess() {
        return httpStatusCode != null && httpStatusCode >= 200 && httpStatusCode < 300;
    }
    
    public boolean isJsonResponse() {
        return contentType != null && contentType.toLowerCase().contains("application/json");
    }
    
    public boolean isXmlResponse() {
        return contentType != null && (contentType.toLowerCase().contains("application/xml") || 
                                      contentType.toLowerCase().contains("text/xml"));
    }
    
    public boolean isHtmlResponse() {
        return contentType != null && contentType.toLowerCase().contains("text/html");
    }
    
    public boolean hasOutput() {
        return output != null && !output.trim().isEmpty();
    }
    
    public boolean hasError() {
        return error != null && !error.trim().isEmpty();
    }
    
    // Method to try parsing JSON output
    public boolean isValidJson() {
        if (!hasOutput()) return false;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(output);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }
    
    // Method to extract HTTP status from curl verbose output
    public void parseVerboseOutput() {
        if (output == null) return;
        
        String[] lines = output.split("\n");
        for (String line : lines) {
            // Look for HTTP status line (e.g., "< HTTP/1.1 200 OK")
            if (line.startsWith("< HTTP/")) {
                String[] parts = line.split(" ");
                if (parts.length >= 2) {
                    try {
                        this.httpStatusCode = Integer.parseInt(parts[1]);
                    } catch (NumberFormatException e) {
                        // Ignore parsing errors
                    }
                }
            }
            // Look for Content-Type header (e.g., "< content-type: application/json")
            else if (line.toLowerCase().startsWith("< content-type:")) {
                this.contentType = line.substring(line.indexOf(":") + 1).trim();
            }
            // Parse other headers
            else if (line.startsWith("< ") && line.contains(":")) {
                String headerLine = line.substring(2);
                int colonIndex = headerLine.indexOf(":");
                if (colonIndex > 0) {
                    String headerName = headerLine.substring(0, colonIndex).trim().toLowerCase();
                    String headerValue = headerLine.substring(colonIndex + 1).trim();
                    responseHeaders.put(headerName, headerValue);
                }
            }
        }
    }
}
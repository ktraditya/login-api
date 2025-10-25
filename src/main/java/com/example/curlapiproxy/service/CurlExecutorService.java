package com.example.curlapiproxy.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.curlapiproxy.model.CurlRequest;
import com.example.curlapiproxy.model.CurlResponse;

@Service
public class CurlExecutorService {
    
    private static final Logger logger = LoggerFactory.getLogger(CurlExecutorService.class);
    private static final int TIMEOUT_SECONDS = 30;
    
    public CompletableFuture<CurlResponse> executeCurlCommand(CurlRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            logger.info("Executing curl command for URL: {}", request.getUrl());
            
            long startTime = System.currentTimeMillis();
            List<String> command = buildCurlCommand(request);
            String commandString = String.join(" ", command);
            
            try {
                ProcessBuilder processBuilder = new ProcessBuilder(command);
                processBuilder.redirectErrorStream(false);
                
                Process process = processBuilder.start();
                
                // Read output and error streams
                StringBuilder output = new StringBuilder();
                StringBuilder error = new StringBuilder();
                
                try (BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                     BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    
                    String line;
                    while ((line = outputReader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                    
                    while ((line = errorReader.readLine()) != null) {
                        error.append(line).append("\n");
                    }
                }
                
                boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                long executionTime = System.currentTimeMillis() - startTime;
                
                if (!finished) {
                    process.destroyForcibly();
                    logger.error("Curl command timed out: {}", commandString);
                    return new CurlResponse("", "Command timed out after " + TIMEOUT_SECONDS + " seconds", -1, commandString, executionTime);
                }
                
                int exitCode = process.exitValue();
                logger.info("Curl command completed with exit code: {} in {}ms", exitCode, executionTime);
                
                CurlResponse response = new CurlResponse(output.toString(), error.toString(), exitCode, commandString, executionTime);
                
                // Parse verbose output if curl was run with -v flag to extract headers and HTTP status
                if (commandString.contains(" -v ") || commandString.contains(" --verbose ")) {
                    response.parseVerboseOutput();
                }
                
                return response;
                
            } catch (IOException | InterruptedException e) {
                long executionTime = System.currentTimeMillis() - startTime;
                logger.error("Error executing curl command: {}", e.getMessage(), e);
                return new CurlResponse("", "Execution error: " + e.getMessage(), -1, commandString, executionTime);
            }
        });
    }
    
    private List<String> buildCurlCommand(CurlRequest request) {
        List<String> command = new ArrayList<>();
        command.add("curl");
        
        // Add parameters if provided
        if (request.getParameters() != null && !request.getParameters().trim().isEmpty()) {
            List<String> params = parseQuotedParameters(request.getParameters().trim());
            for (String param : params) {
                // Basic sanitization - remove potentially dangerous characters
                if (isValidParameter(param)) {
                    command.add(param);
                }
            }
        }
        
        // Add URL last
        command.add(request.getUrl());
        
        return command;
    }
    
    private List<String> parseQuotedParameters(String paramString) {
        List<String> params = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        char quoteChar = 0;
        
        for (int i = 0; i < paramString.length(); i++) {
            char c = paramString.charAt(i);
            
            if (!inQuotes && (c == '\'' || c == '"')) {
                // Start of quoted string
                inQuotes = true;
                quoteChar = c;
                current.append(c);
            } else if (inQuotes && c == quoteChar) {
                // End of quoted string
                inQuotes = false;
                current.append(c);
            } else if (!inQuotes && Character.isWhitespace(c)) {
                // Space outside quotes - end current parameter
                if (current.length() > 0) {
                    params.add(current.toString());
                    current.setLength(0);
                }
            } else {
                // Regular character or space inside quotes
                current.append(c);
            }
        }
        
        // Add final parameter if any
        if (current.length() > 0) {
            params.add(current.toString());
        }
        
        return params;
    }
    
    private boolean isValidParameter(String param) {
        // Basic security check - prevent command injection
        if (param.contains(";") || param.contains("&") || param.contains("|") || 
            param.contains("$(") || param.contains("`") || param.contains("rm ") ||
            param.contains("del ") || param.contains("format ")) {
            logger.warn("Potentially dangerous parameter rejected: {}", param);
            return false;
        }
        return true;
    }
}
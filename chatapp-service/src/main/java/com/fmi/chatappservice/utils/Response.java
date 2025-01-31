package com.fmi.chatappservice.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Response {
    public static ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now().toString());
        response.put("status", status.value());
        response.put("data", data);

        return new ResponseEntity<>(response, status);
    }
}

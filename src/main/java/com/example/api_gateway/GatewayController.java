package com.example.api_gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping
public class GatewayController {

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(value = "/items/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> itemsProxy(@RequestBody(required = false) String body, HttpServletRequest request) throws URISyntaxException {
        return forwardRequest("http://item-service:8081", request.getRequestURI(), request.getMethod(), body);
    }

    @RequestMapping(value = "/orders/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> ordersProxy(@RequestBody(required = false) String body, HttpServletRequest request) throws URISyntaxException {
        return forwardRequest("http://order-service:8082", request.getRequestURI(), request.getMethod(), body);
    }

    @RequestMapping(value = "/payments/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> paymentsProxy(@RequestBody(required = false) String body, HttpServletRequest request) throws URISyntaxException {
        return forwardRequest("http://payment-service:8083", request.getRequestURI(), request.getMethod(), body);
    }

    private ResponseEntity<?> forwardRequest(String serviceUrl, String requestPath, String method, String body) throws URISyntaxException {
        String targetUrl = serviceUrl + requestPath;
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            if ("GET".equalsIgnoreCase(method)) {
                return restTemplate.getForEntity(new URI(targetUrl), String.class);
            } else if ("POST".equalsIgnoreCase(method)) {
                HttpEntity<String> request = new HttpEntity<>(body, headers);
                return restTemplate.postForEntity(new URI(targetUrl), request, String.class);
            } else if ("PUT".equalsIgnoreCase(method)) {
                HttpEntity<String> request = new HttpEntity<>(body, headers);
                restTemplate.put(new URI(targetUrl), request);
                return ResponseEntity.ok().build();
            } else if ("DELETE".equalsIgnoreCase(method)) {
                restTemplate.delete(new URI(targetUrl));
                return ResponseEntity.ok().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
        
        return ResponseEntity.badRequest().build();
    }
}

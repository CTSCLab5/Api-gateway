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
import java.util.Enumeration;

@RestController
@RequestMapping
public class GatewayController {

    @Autowired
    private RestTemplate restTemplate;

    // Microservices URLs
    private static final String USER_SERVICE_URL = "https://user-service-268672367192.us-central1.run.app";
    private static final String DOCTOR_SERVICE_URL = "https://doctor-service-268672367192.us-central1.run.app";
    private static final String PATIENT_SERVICE_URL = "https://patient-service-v6irng3ypq-uc.a.run.app";

    @RequestMapping(value = "/api/auth/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
    public ResponseEntity<?> authProxy(@RequestBody(required = false) String body, HttpServletRequest request) throws URISyntaxException {
        return forwardRequest(USER_SERVICE_URL, request.getRequestURI(), request.getMethod(), body, request);
    }

    @RequestMapping(value = "/api/doctors/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
    public ResponseEntity<?> doctorsProxy(@RequestBody(required = false) String body, HttpServletRequest request) throws URISyntaxException {
        return forwardRequest(DOCTOR_SERVICE_URL, request.getRequestURI(), request.getMethod(), body, request);
    }

    @RequestMapping(value = "/api/slots/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
    public ResponseEntity<?> slotsProxy(@RequestBody(required = false) String body, HttpServletRequest request) throws URISyntaxException {
        return forwardRequest(DOCTOR_SERVICE_URL, request.getRequestURI(), request.getMethod(), body, request);
    }

    @RequestMapping(value = "/api/patients/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
    public ResponseEntity<?> patientsProxy(@RequestBody(required = false) String body, HttpServletRequest request) throws URISyntaxException {
        return forwardRequest(PATIENT_SERVICE_URL, request.getRequestURI(), request.getMethod(), body, request);
    }

    private ResponseEntity<?> forwardRequest(String serviceUrl, String requestPath, String method, String body, HttpServletRequest request) throws URISyntaxException {
        String targetUrl = serviceUrl + requestPath;
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Forward Authorization header if present
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && !authHeader.isEmpty()) {
                headers.set("Authorization", authHeader);
            }
            
            if ("GET".equalsIgnoreCase(method)) {
                HttpEntity<Void> entity = new HttpEntity<>(headers);
                return restTemplate.exchange(new URI(targetUrl), org.springframework.http.HttpMethod.GET, entity, String.class);
            } else if ("POST".equalsIgnoreCase(method)) {
                HttpEntity<String> entity = new HttpEntity<>(body, headers);
                return restTemplate.postForEntity(new URI(targetUrl), entity, String.class);
            } else if ("PUT".equalsIgnoreCase(method)) {
                HttpEntity<String> entity = new HttpEntity<>(body, headers);
                restTemplate.put(new URI(targetUrl), entity);
                return ResponseEntity.ok().build();
            } else if ("DELETE".equalsIgnoreCase(method)) {
                HttpEntity<Void> entity = new HttpEntity<>(headers);
                restTemplate.exchange(new URI(targetUrl), org.springframework.http.HttpMethod.DELETE, entity, String.class);
                return ResponseEntity.ok().build();
            } else if ("PATCH".equalsIgnoreCase(method)) {
                HttpEntity<String> entity = new HttpEntity<>(body, headers);
                return restTemplate.exchange(new URI(targetUrl), org.springframework.http.HttpMethod.PATCH, entity, String.class);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
        
        return ResponseEntity.badRequest().build();
    }
}

package cours.iir4.smartrecyclebackend.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @GetMapping("/public")
    public Map<String, String> publicAccess() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Public Content.");
        response.put("status", "success");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return response;
    }
    
    @GetMapping("/protected")
    public Map<String, String> protectedAccess() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Protected Content. You need to be authenticated to see this.");
        response.put("status", "success");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return response;
    }
    
    @GetMapping("/health")
    public Map<String, String> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "SmartRecycle Backend is running");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return response;
    }
}
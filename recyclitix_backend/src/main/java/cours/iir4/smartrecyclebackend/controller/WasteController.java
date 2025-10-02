package cours.iir4.smartrecyclebackend.controller;

import cours.iir4.smartrecyclebackend.model.User;
import cours.iir4.smartrecyclebackend.model.WasteResult;
import cours.iir4.smartrecyclebackend.payload.request.ImageRequest;
import cours.iir4.smartrecyclebackend.repository.UserRepository;
import cours.iir4.smartrecyclebackend.repository.WasteResultRepository;
import cours.iir4.smartrecyclebackend.security.services.UserDetailsImpl;
import cours.iir4.smartrecyclebackend.service.WasteClassificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.HashMap;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Controller for waste-related operations
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/waste")
public class WasteController {

    @Autowired
    private WasteClassificationService wasteClassificationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WasteResultRepository wasteResultRepository;

    /**
     * Endpoint for scanning and classifying waste
     * 
     * @param imageRequest the request containing the image data
     * @return the classification result
     */
    @PostMapping("/scan")
    public ResponseEntity<?> scanWaste(@Valid @RequestBody ImageRequest imageRequest) {
        User user = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getPrincipal().equals("anonymousUser")) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            Optional<User> userOptional = userRepository.findById(userDetails.getId());
            if (userOptional.isPresent()) {
                user = userOptional.get();
            }
        }

        WasteResult result = wasteClassificationService.classifyWaste(imageRequest, user);

        return ResponseEntity.ok(result);
    }

    /**
     * Get the waste classification history for the authenticated user
     *
     * @return a list of waste classification results
     */
    @GetMapping("/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getHistory() {
        // Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Find the user in the database
        Optional<User> userOptional = userRepository.findById(userDetails.getId());
        if (!userOptional.isPresent()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        // Get the user's waste classification history
        List<WasteResult> history = wasteResultRepository.findByUserOrderByScanTimeDesc(userOptional.get());

        return ResponseEntity.ok(history);
    }

    /**
     * Save a complete waste result with all fields
     * 
     * @param wasteResult the waste result to save
     * @return the saved waste result
     */
    @PostMapping("/save")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> saveWasteResult(@Valid @RequestBody WasteResult wasteResult) {
        // Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Find the user in the database
        Optional<User> userOptional = userRepository.findById(userDetails.getId());
        if (!userOptional.isPresent()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        // Set the user and timestamps
        wasteResult.setUser(userOptional.get());
        if (wasteResult.getWasteDate() == null) {
            wasteResult.setWasteDate(LocalDateTime.now());
        }
        if (wasteResult.getScanTime() == null) {
            wasteResult.setScanTime(LocalDateTime.now());
        }

        // Save the waste result
        WasteResult savedResult = wasteResultRepository.save(wasteResult);

        return ResponseEntity.ok(savedResult);
    }

    /**
     * Get a specific waste classification result
     * 
     * @param id the ID of the waste result
     * @return the waste classification result
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getWasteResult(@PathVariable Long id) {
        // Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Find the waste result
        Optional<WasteResult> resultOptional = wasteResultRepository.findById(id);
        if (!resultOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        // Check if the waste result belongs to the authenticated user
        WasteResult result = resultOptional.get();
        if (!result.getUser().getId().equals(userDetails.getId())) {
            return ResponseEntity.status(403).body("Access denied");
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Delete a specific waste classification result
     * @param id the ID of the waste result
     * @return status message
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteWasteResult(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Optional<WasteResult> resultOptional = wasteResultRepository.findById(id);
        if (!resultOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        WasteResult result = resultOptional.get();
        if (!result.getUser().getId().equals(userDetails.getId())) {
            return ResponseEntity.status(403).body("Access denied");
        }
        wasteResultRepository.deleteById(id);
        return ResponseEntity.ok().body("Waste result deleted successfully");
    }

    // Endpoint for uploading an image
    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file provided");
        }
        try {
            String uploadsDir = System.getProperty("user.dir") + "/uploads/";
            File dir = new File(uploadsDir);
            if (!dir.exists()) dir.mkdirs();
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String filename = Instant.now().toEpochMilli() + "_" + originalFilename;
            Path filePath = Paths.get(uploadsDir + filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            // Suppose static files are served from /uploads/
            String imageUrl = "/uploads/" + filename;
            HashMap<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Image upload failed");
        }
    }
}

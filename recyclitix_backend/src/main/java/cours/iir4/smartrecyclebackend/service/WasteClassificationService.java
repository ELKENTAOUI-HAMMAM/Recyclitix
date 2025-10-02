package cours.iir4.smartrecyclebackend.service;

import cours.iir4.smartrecyclebackend.model.User;
import cours.iir4.smartrecyclebackend.model.WasteResult;
import cours.iir4.smartrecyclebackend.payload.request.ImageRequest;
import cours.iir4.smartrecyclebackend.repository.WasteResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for waste classification
 */
@Service
public class WasteClassificationService {

    @Autowired
    private WasteResultRepository wasteResultRepository;

    private static final Map<String, String> WASTE_TYPES_INFO = new HashMap<>();

    static {
        WASTE_TYPES_INFO.put("plastic", "Plastic waste should be cleaned and sorted by type. Most plastic containers have a recycling symbol with a number that identifies the type of plastic.");
        WASTE_TYPES_INFO.put("paper", "Paper waste should be clean and dry. Avoid recycling paper that is contaminated with food or other substances.");
        WASTE_TYPES_INFO.put("glass", "Glass containers should be empty, clean, and sorted by color. Remove caps and lids before recycling.");
        WASTE_TYPES_INFO.put("metal", "Metal cans and containers should be empty and clean. Some recycling programs accept aluminum foil and trays.");
        WASTE_TYPES_INFO.put("organic", "Organic waste can be composted. This includes food scraps, yard waste, and other biodegradable materials.");
        WASTE_TYPES_INFO.put("non-recyclable", "Non-recyclable waste should be disposed of in regular trash. This includes items that cannot be recycled or composted.");
    }

    private static final int MAX_IMAGE_SIZE = 2 * 1024 * 1024;

    /**
     * Classify waste based on image data
     * 
     * @param imageRequest the request containing the image data
     * @param user the user who submitted the request
     * @return the classification result
     * @throws IllegalArgumentException if the image is too large or invalid
     * @throws IOException if there's an error processing the image
     */
    public WasteResult classifyWaste(ImageRequest imageRequest, User user) {
        if (imageRequest.getImageData() == null || imageRequest.getImageData().isEmpty()) {
            throw new IllegalArgumentException("Image URL cannot be empty");
        }

        // Utiliser l'URL directement
        String photoUrl = imageRequest.getImageData();

        String[] wasteTypes = {"plastic", "paper", "glass", "metal", "organic", "non-recyclable"};
        String wasteType = wasteTypes[(int) (Math.random() * wasteTypes.length)];

        String recyclingInstructions = WASTE_TYPES_INFO.getOrDefault(wasteType,
                "No specific recycling instructions available for this type of waste.");

        String environmentalImpact = generateEnvironmentalImpact(wasteType);

        String description = generateDescription(wasteType);

        WasteResult result = new WasteResult();
        result.setWasteType(wasteType);
        result.setConfidence(75.0 + Math.random() * 20.0); // Mock confidence between 75% and 95%
        result.setObjectDescription(description);
        result.setInstructions(recyclingInstructions);
        result.setEnvironmentalImpact(environmentalImpact);
        if (user != null) {
            result.setUser(user);
        }
        result.setWasteDate(LocalDateTime.now());
        result.setScanTime(LocalDateTime.now());
        // Attribution des points selon le type de déchet
        int points = switch (wasteType) {
            case "plastic" -> 10;
            case "paper" -> 8;
            case "glass" -> 12;
            case "metal" -> 15;
            case "organic" -> 5;
            default -> 2;
        };
        result.setWastePoints(points);
        // Stocker l'image analysée dans waste_icon
        result.setWasteIcon(imageRequest.getImageData());
        // Copier l'URL de l'image uploadée
        result.setImageUrl(imageRequest.getImageUrl());
        return wasteResultRepository.save(result);
    }

    /**
     * Generate a description for the waste type
     * 
     * @param wasteType the type of waste
     * @return a description
     */
    private String generateDescription(String wasteType) {
        switch (wasteType) {
            case "plastic":
                return "This appears to be a plastic item. Plastic is a synthetic material made from polymers and is widely used for packaging and other applications.";
            case "paper":
                return "This appears to be a paper item. Paper is made from wood pulp and is used for writing, printing, and packaging.";
            case "glass":
                return "This appears to be a glass item. Glass is made from sand and is used for containers, windows, and other applications.";
            case "metal":
                return "This appears to be a metal item. Metals are elements or alloys that are good conductors of electricity and heat.";
            case "organic":
                return "This appears to be organic waste. Organic waste comes from plants or animals and is biodegradable.";
            case "non-recyclable":
                return "This appears to be non-recyclable waste. This type of waste cannot be recycled and should be disposed of in regular trash.";
            default:
                return "Unable to provide a detailed description for this type of waste.";
        }
    }

    /**
     * Generate environmental impact information for the waste type
     * 
     * @param wasteType the type of waste
     * @return environmental impact information
     */
    private String generateEnvironmentalImpact(String wasteType) {
        switch (wasteType) {
            case "plastic":
                return "Plastic waste has a significant environmental impact. It can take hundreds of years to decompose and often ends up in oceans, harming marine life. Recycling plastic reduces the need for new plastic production and saves energy.";
            case "paper":
                return "Paper waste has a moderate environmental impact. While it is biodegradable, its production contributes to deforestation. Recycling paper saves trees, reduces energy consumption, and decreases landfill waste.";
            case "glass":
                return "Glass waste has a low environmental impact if recycled. It is 100% recyclable and can be recycled endlessly without loss in quality. Recycling glass reduces energy consumption and raw material use.";
            case "metal":
                return "Metal waste has a high environmental impact if not recycled. Mining for metals contributes to habitat destruction and pollution. Recycling metals saves energy and reduces greenhouse gas emissions.";
            case "organic":
                return "Organic waste has a low environmental impact if composted. When sent to landfills, it produces methane, a potent greenhouse gas. Composting organic waste creates nutrient-rich soil and reduces methane emissions.";
            case "non-recyclable":
                return "Non-recyclable waste has a high environmental impact. It contributes to landfill growth and can release harmful substances into the environment. Reducing consumption of non-recyclable items is the best approach.";
            default:
                return "Unable to provide environmental impact information for this type of waste.";
        }
    }
}

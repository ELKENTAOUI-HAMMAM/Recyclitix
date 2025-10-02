package cours.iir4.smartrecyclebackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recycling-points")
public class RecyclingPointController {

    @GetMapping
    public List<Map<String, Object>> getRecyclingPoints(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam int radius) {
        // Points de tri au Maroc et à Marrakech
        return Arrays.asList(
                Map.of(
                        "id", 1,
                        "name", "Marrakech Medina Sorting Center",
                        "address", "Avenue Mohammed VI, Marrakech 40000, Morocco",
                        "latitude", 31.6295,
                        "longitude", -7.9811,
                        "type", "MIXED",
                        "acceptedMaterials", Arrays.asList("Plastic", "Paper", "Glass", "Metal"),
                        "hours", "Mon-Sat: 8am-6pm",
                        "contact", "+212 524-123456"
                ),
                Map.of(
                        "id", 2,
                        "name", "Gueliz Recycling Point",
                        "address", "Boulevard Mohammed V, Gueliz, Marrakech",
                        "latitude", 31.6336,
                        "longitude", -8.0097,
                        "type", "PLASTIC",
                        "acceptedMaterials", Arrays.asList("Plastic", "Paper"),
                        "hours", "Mon-Fri: 9am-7pm",
                        "contact", "+212 524-789012"
                ),
                Map.of(
                        "id", 3,
                        "name", "Eco-Center Majorelle",
                        "address", "Rue Yves Saint Laurent, Marrakech",
                        "latitude", 31.6424,
                        "longitude", -8.0028,
                        "type", "GLASS",
                        "acceptedMaterials", Arrays.asList("Glass", "Metal"),
                        "hours", "Mon-Sun: 8am-8pm",
                        "contact", "+212 524-345678"
                ),
                Map.of(
                        "id", 4,
                        "name", "Palmeraie Sorting Center",
                        "address", "Route de Fes, Palmeraie, Marrakech",
                        "latitude", 31.6519,
                        "longitude", -7.9642,
                        "type", "MIXED",
                        "acceptedMaterials", Arrays.asList("Plastic", "Paper", "Glass", "Metal", "Cardboard"),
                        "hours", "Mon-Sat: 7am-5pm",
                        "contact", "+212 524-901234"
                ),
                Map.of(
                        "id", 5,
                        "name", "Hivernage Green Point",
                        "address", "Avenue Mohammed VI, Hivernage, Marrakech",
                        "latitude", 31.6188,
                        "longitude", -8.0205,
                        "type", "PLASTIC",
                        "acceptedMaterials", Arrays.asList("Plastic", "Paper", "Cardboard"),
                        "hours", "Mon-Fri: 8:30am-6:30pm",
                        "contact", "+212 524-567890"
                )
        );
    }
} 
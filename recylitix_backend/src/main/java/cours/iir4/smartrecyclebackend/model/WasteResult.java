package cours.iir4.smartrecyclebackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "waste_results")
public class WasteResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "waste_icon")
    private String wasteIcon;

    @Column(name = "waste_type")
    private String wasteType;

    @Column(name = "waste_category")
    private String wasteCategory;

    @Column(name = "waste_date")
    private LocalDateTime wasteDate;

    @Column(name = "waste_points")
    private Integer wastePoints;

    @Column(name = "time_ago")
    private String timeAgo;

    @Column(name = "object_description", length = 1000)
    private String objectDescription;

    @Column(name = "instructions", length = 1000)
    private String instructions;

    @Column(name = "confidence")
    private Double confidence;

    @Column(name = "environmental_impact", length = 1000)
    private String environmentalImpact;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @ManyToOne(optional = true)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(name = "scan_time")
    private LocalDateTime scanTime;

    public WasteResult(String wasteIcon, String wasteType, String wasteCategory, 
                      Integer wastePoints, String timeAgo, String objectDescription, 
                      String instructions, User user) {
        this.wasteIcon = wasteIcon;
        this.wasteType = wasteType;
        this.wasteCategory = wasteCategory;
        this.wastePoints = wastePoints;
        this.timeAgo = timeAgo;
        this.objectDescription = objectDescription;
        this.instructions = instructions;
        this.user = user;
        this.wasteDate = LocalDateTime.now();
        this.scanTime = LocalDateTime.now();
    }

    public WasteResult(String wasteType, Double confidence, String description, 
                      String recyclingInstructions, User user) {
        this.wasteType = wasteType;
        this.confidence = confidence;
        this.objectDescription = description;
        this.instructions = recyclingInstructions;
        this.user = user;
        this.wasteDate = LocalDateTime.now();
        this.scanTime = LocalDateTime.now();
    }
}

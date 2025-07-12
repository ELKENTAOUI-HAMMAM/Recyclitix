package cours.iir4.smartrecyclebackend.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request class for sending image data to be processed for waste classification.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageRequest {
    private String imageData; // Ce champ contiendra l'URL de l'image
    private String imageUrl;

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
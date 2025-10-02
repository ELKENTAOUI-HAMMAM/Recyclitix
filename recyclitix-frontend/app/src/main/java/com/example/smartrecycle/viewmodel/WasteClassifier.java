package com.example.smartrecycle.viewmodel;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

public class WasteClassifier {
    private final Interpreter tflite;
    private static final String[] WASTE_TYPES = {
            "metal", "glass", "paper", "cardboard", "plastic"
    };
    
    // Map to store detailed descriptions for each waste type
    private static final Map<String, String> WASTE_DESCRIPTIONS = new HashMap<String, String>() {{
        put("metal", "Metal waste includes items like aluminum cans, steel food containers, and metal bottle caps. " +
                "These items are highly recyclable and can be melted down and reused indefinitely without losing quality.");
        
        put("glass", "Glass waste includes bottles, jars, and broken glass items. " +
                "Glass is 100% recyclable and can be recycled endlessly without loss in quality or purity. " +
                "Different colors of glass should be separated for optimal recycling.");
        
        put("paper", "Paper waste includes newspapers, magazines, office paper, cardstock, and mail. " +
                "Paper products can be recycled 5-7 times before the fibers become too short to be useful. " +
                "Keep paper dry and free from food contamination for proper recycling.");
        
        put("cardboard", "Cardboard waste includes corrugated boxes, shipping containers, and packaging materials. " +
                "Cardboard is highly recyclable and should be flattened to save space. " +
                "Remove any tape, labels, or other non-cardboard materials before recycling.");
        
        put("plastic", "Plastic waste includes bottles, containers, packaging, and other plastic items. " +
                "Not all plastics are recyclable - check the recycling number (1-7) on the bottom. " +
                "Plastics #1 (PET) and #2 (HDPE) are most commonly accepted for recycling.");
    }};
    
    // Map to store recycling instructions for each waste type
    private static final Map<String, String> RECYCLING_INSTRUCTIONS = new HashMap<String, String>() {{
        put("metal", "1. Empty and rinse the metal container\n" +
                "2. Remove paper labels if possible\n" +
                "3. You can flatten cans to save space\n" +
                "4. Place in the metal recycling bin");
        
        put("glass", "1. Empty and rinse the glass container\n" +
                "2. Remove caps and lids (recycle separately)\n" +
                "3. Sort by color if required locally\n" +
                "4. Place in the glass recycling bin");
        
        put("paper", "1. Make sure the paper is clean and dry\n" +
                "2. Remove any plastic wrapping or tape\n" +
                "3. Flatten boxes to save space\n" +
                "4. Place in the paper recycling bin");
        
        put("cardboard", "1. Remove any packaging materials inside\n" +
                "2. Flatten the cardboard to save space\n" +
                "3. Keep it dry and clean\n" +
                "4. Place in the cardboard recycling bin");
        
        put("plastic", "1. Empty and rinse the container\n" +
                "2. Check the recycling number (1-7) to ensure it's recyclable\n" +
                "3. Remove caps if required by local guidelines\n" +
                "4. Place in the plastic recycling bin");
    }};

    public WasteClassifier(Context context) throws IOException {
        try {
            tflite = new Interpreter(loadModelFile(context));
        } catch (Exception e) {
            Log.e("WasteClassifier", "Failed to initialize TFLite", e);
            throw e;
        }
    }

    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public String classifyImage(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e("WasteClassifier", "Input bitmap is null");
            return "error";
        }

        try {
            // 1. Resize to match training input size
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);

            // 2. Prepare input tensor
            float[][][][] input = new float[1][224][224][3];
            int[] pixels = new int[224 * 224];
            resizedBitmap.getPixels(pixels, 0, 224, 0, 0, 224, 224);

            // 3. Convert and normalize pixels to match tf.keras.preprocessing.image.img_to_array
            for (int i = 0; i < 224; i++) {
                for (int j = 0; j < 224; j++) {
                    int pixel = pixels[i * 224 + j];
                    // Scale to [0, 255] range like in training
                    input[0][i][j][0] = (pixel >> 16) & 0xFF; // R
                    input[0][i][j][1] = (pixel >> 8) & 0xFF;  // G
                    input[0][i][j][2] = pixel & 0xFF;         // B
                }
            }

            // 4. Run inference
            float[][] output = new float[1][WASTE_TYPES.length];
            if (tflite == null) {
                Log.e("WasteClassifier", "TFLite interpreter is null");
                return "error";
            }

            tflite.run(input, output);

            // Debug output values
            StringBuilder sb = new StringBuilder("Predictions: ");
            for (int i = 0; i < output[0].length; i++) {
                sb.append(WASTE_TYPES[i]).append(":").append(output[0][i]).append(" ");
            }
            Log.d("WasteClassifier", sb.toString());

            return WASTE_TYPES[getMaxIndex(output[0])];

        } catch (Exception e) {
            Log.e("WasteClassifier", "Error classifying image", e);
            return "error";
        }
    }

    private int getMaxIndex(float[] array) {
        int maxIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }
    
    // Get the detailed description for a waste type
    public static String getWasteDescription(String wasteType) {
        return WASTE_DESCRIPTIONS.getOrDefault(wasteType.toLowerCase(), 
                "No description available for this waste type.");
    }
    
    // Get recycling instructions for a waste type
    public static String getRecyclingInstructions(String wasteType) {
        return RECYCLING_INSTRUCTIONS.getOrDefault(wasteType.toLowerCase(), 
                "No recycling instructions available for this waste type.");
    }
    
    // Check if a waste type is recyclable
    public static boolean isRecyclable(String wasteType) {
        switch (wasteType.toLowerCase()) {
            case "plastic":
            case "paper":
            case "glass":
            case "metal":
            case "cardboard":
                return true;
            default:
                return false;
        }
    }

    public void close() {
        if (tflite != null) {
            tflite.close();
        }
    }
}
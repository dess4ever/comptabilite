package com.high4resto.comptabilite.services.implementations;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageSource;

import jakarta.annotation.PostConstruct;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OcrService {
    
    @Autowired
    private BucketService bucketService;

    private ImageSource imgSource;
    private Feature textFeature;
    private AnnotateImageResponse visionResponse;

    @PostConstruct
    public void init() {
      textFeature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
    }

    // upload object to bucket
    public String uploadImageObjectAndGetText(String objectName, byte[] content) throws IOException {  
      bucketService.saveToBucket(objectName, content);
      return getTextFromBucket(objectName);
    }
    

    // Get text from image
    private String  getTextFromBucket(String filename) {
        List<AnnotateImageRequest> visionRequests = new ArrayList<>();
        String gcsPath = String.format("gs://%s/%s", bucketService.UPLOAD_BUCKET_NAME, filename);
      
        imgSource = ImageSource.newBuilder().setGcsImageUri(gcsPath).build();
        Image img = Image.newBuilder().setSource(imgSource).build();
      
        AnnotateImageRequest visionRequest =
            AnnotateImageRequest.newBuilder().addFeatures(textFeature).setImage(img).build();
        visionRequests.add(visionRequest);
      
        // Detect text in an image using the Cloud Vision API
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
          visionResponse = client.batchAnnotateImages(visionRequests).getResponses(0);
          if (visionResponse == null || !visionResponse.hasFullTextAnnotation()) {
            return String.format("Image %s contains no text", filename);
          }
      
          if (visionResponse.hasError()) {
            return visionResponse.getError().getMessage();
          }
        } catch (IOException e) {
          return e.getMessage();
        }
      
        return visionResponse.getFullTextAnnotation().getText();
    }
    
}

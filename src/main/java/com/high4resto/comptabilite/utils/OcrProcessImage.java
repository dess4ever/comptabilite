package com.high4resto.comptabilite.utils;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageSource;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class OcrProcessImage {
    private static final String UPLOAD_BUCKET_NAME = System.getenv("UPLOAD_BUCKET_NAME");
    private static final String PROJECT_ID = System.getenv("PROJECT_ID");


    public OcrProcessImage() {
    }
    // upload object to bucket
    public static String uploadImageObjectAndGetText(String objectName, byte[] content) throws IOException {  
      Storage storage = StorageOptions.newBuilder().setProjectId(PROJECT_ID).build().getService();
      BlobId blobId = BlobId.of(UPLOAD_BUCKET_NAME, objectName);
      BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
      Storage.BlobTargetOption precondition;
      if (storage.get(UPLOAD_BUCKET_NAME, objectName) == null) {
        precondition = Storage.BlobTargetOption.doesNotExist();
      } else {
        precondition =
            Storage.BlobTargetOption.generationMatch();
      }
      storage.create(blobInfo, content,precondition);
  
      System.out.println(
          "File " + " uploaded to bucket " + UPLOAD_BUCKET_NAME + " as " + objectName);
    return getTextFromBucket(objectName);
    }
    

    // Get text from image
    public static String  getTextFromBucket(String filename) {
        String bucket = UPLOAD_BUCKET_NAME;
        List<AnnotateImageRequest> visionRequests = new ArrayList<>();
        String gcsPath = String.format("gs://%s/%s", bucket, filename);
      
        ImageSource imgSource = ImageSource.newBuilder().setGcsImageUri(gcsPath).build();
        Image img = Image.newBuilder().setSource(imgSource).build();
      
        Feature textFeature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest visionRequest =
            AnnotateImageRequest.newBuilder().addFeatures(textFeature).setImage(img).build();
        visionRequests.add(visionRequest);
      
        // Detect text in an image using the Cloud Vision API
        AnnotateImageResponse visionResponse;
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

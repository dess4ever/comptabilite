package com.high4resto.comptabilite.services.implementations;

import com.google.cloud.documentai.v1.Document;
import com.google.cloud.documentai.v1.DocumentProcessorServiceClient;
import com.google.cloud.documentai.v1.DocumentProcessorServiceSettings;
import com.google.cloud.documentai.v1.ProcessRequest;
import com.google.cloud.documentai.v1.ProcessResponse;
import com.google.cloud.documentai.v1.RawDocument;
import com.google.protobuf.ByteString;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import org.springframework.stereotype.Service;

@Service
public class OcrService {

  private DocumentProcessorServiceClient client;
  private final String name = System.getenv("GOOGLE_OCR_PROCESSOR_END_POINT");

  @PostConstruct
  public void init() {
    try {
      client = DocumentProcessorServiceClient.create(
          DocumentProcessorServiceSettings.newBuilder().setEndpoint("eu-documentai.googleapis.com:443").build());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public String getTextFromImage(byte[] imageFileData, String extention)
  {
    ByteString content = ByteString.copyFrom(imageFileData);
    RawDocument document =
    RawDocument.newBuilder().setContent(content).setMimeType("image/" + extention).build();
    ProcessRequest request =
    ProcessRequest.newBuilder().setName(name).setRawDocument(document).build();
    ProcessResponse result = client.processDocument(request);
    Document documentResponse = result.getDocument();
    return documentResponse.getText();
  }

  public String getTextFromImagePdf(byte[] imageFileData) {
    ByteString content = ByteString.copyFrom(imageFileData);
    RawDocument document =
    RawDocument.newBuilder().setContent(content).setMimeType("application/pdf").build();
    ProcessRequest request =
    ProcessRequest.newBuilder().setName(name).setRawDocument(document).build();
    ProcessResponse result = client.processDocument(request);
    Document documentResponse = result.getDocument();
    return documentResponse.getText();
  }
}

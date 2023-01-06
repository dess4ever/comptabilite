package com.high4resto.comptabilite.services.implementations;

import org.springframework.stereotype.Service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import jakarta.annotation.PostConstruct;

@Service
public class BucketService {
    public final String UPLOAD_BUCKET_NAME = System.getenv("UPLOAD_BUCKET_NAME");
    private final String PROJECT_ID = System.getenv("PROJECT_ID");

    private Storage storage;
    private BlobId blobId;
    private BlobInfo blobInfo;
    
    @PostConstruct
    public void init() {
        storage = StorageOptions.newBuilder().setProjectId(PROJECT_ID).build().getService();
    }

    public void saveToBucket(String objectName, byte[] content) {
        
        blobId = BlobId.of(UPLOAD_BUCKET_NAME, objectName);
        blobInfo = BlobInfo.newBuilder(blobId).build();
        Storage.BlobTargetOption precondition;
        if (storage.get(UPLOAD_BUCKET_NAME, objectName) == null) {
            precondition = Storage.BlobTargetOption.doesNotExist();
        } else {
            precondition = Storage.BlobTargetOption.generationMatch();
        }
        storage.create(blobInfo, content, precondition);

        System.out.println(
                "File " + " uploaded to bucket " + UPLOAD_BUCKET_NAME + " as " + objectName);

    }

    public void deleteFromBucket(String objectName) {
        storage.delete(UPLOAD_BUCKET_NAME, objectName);
    }

    public byte[] getFromBucket(String objectName) {
        return storage.get(UPLOAD_BUCKET_NAME, objectName).getContent();
    }

}

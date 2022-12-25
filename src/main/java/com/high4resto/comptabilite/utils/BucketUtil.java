package com.high4resto.comptabilite.utils;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class BucketUtil {
    private static final String UPLOAD_BUCKET_NAME = System.getenv("UPLOAD_BUCKET_NAME");
    private static final String PROJECT_ID = System.getenv("PROJECT_ID");

    public static void saveToBucket(String objectName, byte[] content) {
        
        Storage storage = StorageOptions.newBuilder().setProjectId(PROJECT_ID).build().getService();
        BlobId blobId = BlobId.of(UPLOAD_BUCKET_NAME, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
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

}

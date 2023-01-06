package com.high4resto.comptabilite.services.contract;

import java.util.List;

import org.primefaces.model.file.UploadedFile;

import com.high4resto.comptabilite.dataStruct.ResultSearch;
import com.high4resto.comptabilite.documents.LogMessage;

public interface FileUploadServiceContract {
    public abstract List<ResultSearch> getAllDocuments();
    public abstract List<LogMessage> saveDocument(UploadedFile file);
    public abstract List<LogMessage> deleteDocument(ResultSearch document);
    public abstract List<LogMessage> updateDocument(ResultSearch document);
    public abstract List<ResultSearch> searchDocumentWhithContent(String content);
}

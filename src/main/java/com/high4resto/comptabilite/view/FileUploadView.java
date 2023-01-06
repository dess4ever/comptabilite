package com.high4resto.comptabilite.view;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.file.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.high4resto.comptabilite.dataStruct.ResultSearch;
import com.high4resto.comptabilite.documents.UploadedDocument;
import com.high4resto.comptabilite.services.implementations.FileUploadService;
import com.high4resto.comptabilite.utils.PrimefaceUtil;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseId;
import lombok.Getter;
import lombok.Setter;

@Component
@SessionScope
public class FileUploadView implements Serializable {
    private static final long serialVersionUID = 1L;
    @Autowired
    private FileUploadService fileUploadService;
    @Getter
    @Setter
    private List<ResultSearch> documents;
    @Getter
    @Setter
    private List<ResultSearch> filteredDocuments;
    @Getter
    @Setter
    public ResultSearch selectDocument;
    @Getter
    @Setter
    private boolean pdf;
    @Getter
    @Setter
    private boolean image;
    @Getter
    @Setter
    private String searchInput;

    @PostConstruct
    public void init() {
        this.documents = fileUploadService.getAllDocuments();
    }

    public void onRowSelect(SelectEvent<ResultSearch> event) {
        this.selectDocument = event.getObject();
    }

    public void onRowEdit(RowEditEvent<ResultSearch> event) {
        ResultSearch tFile = (ResultSearch) event.getObject();
        PrimefaceUtil.addMessages(fileUploadService.updateDocument(tFile));
    }

    public void deleteDocument() {
        PrimefaceUtil.addMessages(fileUploadService.deleteDocument(this.selectDocument));
        documents.remove(selectDocument);
        selectDocument = null;
    }

    public void setSelectedDocument(ResultSearch selectedDocument) {
        this.selectDocument = selectedDocument;
    }

    public void onRowCancel(RowEditEvent<UploadedDocument> event) {
        PrimefaceUtil.warn("Les modification ont été annulées");
    }

    public void view() {
        this.pdf = false;
        this.image = false;
        if (this.selectDocument != null) {
            String fileName = this.selectDocument.getDocument().getFileName();
            if (fileName.endsWith(".pdf"))
                this.pdf = true;
            else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png"))
                this.image = true;
        }
    }

    public StreamedContent getStreamPDF() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            new DefaultStreamedContent();
            // So, we're rendering the HTML. Return a stub StreamedContent so that it will
            // generate right URL.
            return new DefaultStreamedContent();
        } else {
            if (this.selectDocument == null)
                return DefaultStreamedContent.builder().contentType("application/pdf").build();
            new DefaultStreamedContent();
            return DefaultStreamedContent.builder().contentType("application/pdf")
                    .name(this.selectDocument.getDocument().getFileName())
                    .stream(() -> new ByteArrayInputStream(this.selectDocument.getDocument().getContent())).build();
        }
    }

    public StreamedContent getStreamImage() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            new DefaultStreamedContent();
            // So, we're rendering the HTML. Return a stub StreamedContent so that it will
            // generate right URL.
            return new DefaultStreamedContent();
        } else {
            if (this.selectDocument == null)
                return DefaultStreamedContent.builder().contentType("image/png").build();
            new DefaultStreamedContent();
            return DefaultStreamedContent.builder().contentType("image/png").name(this.selectDocument.getDocument().getFileName())
                    .stream(() -> new ByteArrayInputStream(this.selectDocument.getDocument().getContent())).build();
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        UploadedFile files = event.getFile();
        PrimefaceUtil.addMessages(fileUploadService.saveDocument(files));
        documents = fileUploadService.getAllDocuments();
    }
    
    public void search() {
        this.documents=fileUploadService.searchDocumentWhithContent(this.searchInput);
    }
}
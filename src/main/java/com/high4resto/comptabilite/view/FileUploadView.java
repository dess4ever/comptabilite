package com.high4resto.comptabilite.view;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
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

import com.high4resto.comptabilite.documents.UploadedDocument;
import com.high4resto.comptabilite.repository.UploadedDocumentRepository;
import com.high4resto.comptabilite.utils.OcrProcessImage;
import com.high4resto.comptabilite.utils.TextUtil;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseId;
import lombok.Getter;
import lombok.Setter;

@Component
@SessionScope
public class FileUploadView implements Serializable {
    @Getter @Setter
    private List<UploadedDocument> documents;
    @Autowired 
    UploadedDocumentRepository documentController;
    private static final long serialVersionUID=1L;
    @Getter @Setter
    public UploadedDocument selectDocument;
    @Getter @Setter
    private boolean pdf;
    @Getter @Setter
    private boolean image;

    @PostConstruct
    public void init() {
        this.documents=documentController.findAll();
    }

    public void onRowSelect(SelectEvent<UploadedDocument> event) {
        this.selectDocument=event.getObject();
    }

    public void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().
                addMessage(null, new FacesMessage(severity, summary, detail));
    }

    public void onRowEdit(RowEditEvent<UploadedDocument> event) {
        UploadedDocument tFile=(UploadedDocument) event.getObject();
        addMessage(FacesMessage.SEVERITY_INFO, "Info", "La modification a été enregistrée");
        documentController.save(tFile);
    }

    public void deleteDocument()
    {
        documentController.delete(selectDocument);
        documents.remove(selectDocument);
        addMessage(FacesMessage.SEVERITY_WARN, "Attention","Le document ayant pour nom:"+this.selectDocument.getFileName()+" a été effacé!");
        selectDocument=null;
    }

    public void setSelectedDocument(UploadedDocument selectedDocument) {
        this.selectDocument = selectedDocument;
    }

    public void onRowCancel(RowEditEvent<UploadedDocument> event) {
        addMessage(FacesMessage.SEVERITY_WARN, "Attention" , "Les modification ont été annulées");
    }

    public void view()
    {
        this.pdf=false;
        this.image=false;
       if(this.selectDocument!=null)
       {
           String fileName=this.selectDocument.getFileName();
           if(fileName.endsWith(".pdf"))
               this.pdf=true;
            else if(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png"))
                this.image=true;       }
    }

    public StreamedContent getStreamPDF() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            new DefaultStreamedContent();
            // So, we're rendering the HTML. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        } else {
            if(this.selectDocument==null)
                return DefaultStreamedContent.builder().contentType("application/pdf").build();
            new DefaultStreamedContent();
            return DefaultStreamedContent.builder().contentType("application/pdf").name(this.selectDocument.getFileName()).stream(()->new ByteArrayInputStream(this.selectDocument.getContent())).build();
        }
    }
    public StreamedContent getStreamImage() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            new DefaultStreamedContent();
            // So, we're rendering the HTML. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        } else {
            if(this.selectDocument==null)
                return DefaultStreamedContent.builder().contentType("image/png").build();
            new DefaultStreamedContent();
            return DefaultStreamedContent.builder().contentType("image/png").name(this.selectDocument.getFileName()).stream(()->new ByteArrayInputStream(this.selectDocument.getContent())).build();
        }
    }
 
    public void handleFileUpload(FileUploadEvent event) {
        System.out.println(event.getFile().getSize());
        UploadedFile files = event.getFile();
        if (files != null && files.getContent() != null && files.getContent().length > 0 && files.getFileName() != null) {
            String message;
			byte[] file=event.getFile().getContent();
            UploadedDocument document=new UploadedDocument();
            try {
                document.setHash(TextUtil.SHAsum(file));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            document.setContent(file);
            try {
                String fileName=event.getFile().getFileName();
                // if file is pdf
                if(fileName.endsWith(".pdf"))
                    document.setBrut(TextUtil.getTextFromPDF(file));
                // if file is image
                else if(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png"))
                    {
                        String text=OcrProcessImage.uploadImageObjectAndGetText(document.getHash()+fileName, file);
                        document.setBrut(text);

                    }
                    document.setFileName(fileName);

                } catch (IOException e1) {
                e1.printStackTrace();
            }
            Date dateNow= new Date();
            document.setDate(dateNow.toString());

            List<UploadedDocument> findByHash=documentController.findByHash(document.getHash());
            if(findByHash.isEmpty())
            {
                message="Le fichier n'a pas encore été téléversé je le rajoute";
                documentController.save(document);
                this.documents=documentController.findAll();
            }
            else
            {
                message="Le fichier a déja été téléversé je ne fais rien. Nom du fichier:"+document.getFileName();
            }
            addMessage(FacesMessage.SEVERITY_INFO, "Info", message);

        }
    }    
}
package com.high4resto.comptabilite.view;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

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
    private UploadedFile file;
    @Getter @Setter
    private List<UploadedDocument> documents;
    @Autowired 
    UploadedDocumentRepository documentController;
    private static final long serialVersionUID=1L;
    @Getter @Setter
	private transient UploadedFile uploadedFile;
    @Getter @Setter
    public UploadedDocument selectDocument;

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

    public void viewPDF()
    {
        System.out.println("Salut");
    }

    public StreamedContent getStream() throws IOException {
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
       
	public void upload() {
		if (this.uploadedFile != null) {
            String message;
			byte[] file=this.uploadedFile.getContent();
            UploadedDocument document=new UploadedDocument();
            document.setContent(file);
            try {
                document.setBrut(TextUtil.getTextFromPDF(file));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            Date dateNow= new Date();
            document.setDate(dateNow.toString());
            document.setFileName(uploadedFile.getFileName());
            try {
                document.setHash(TextUtil.SHAsum(file));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
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
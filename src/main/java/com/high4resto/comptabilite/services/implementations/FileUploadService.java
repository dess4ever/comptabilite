package com.high4resto.comptabilite.services.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.primefaces.model.file.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.high4resto.comptabilite.dataStruct.ResultSearch;
import com.high4resto.comptabilite.documents.LogMessage;
import com.high4resto.comptabilite.documents.UploadedDocument;
import com.high4resto.comptabilite.repository.UploadedDocumentRepository;
import com.high4resto.comptabilite.services.contract.FileUploadServiceContract;
import com.high4resto.comptabilite.utils.TextUtil;

@Service
public class FileUploadService implements FileUploadServiceContract {
    @Autowired
    private OcrService ocrService;
    @Autowired
    private BucketService bucketService;
    @Autowired
    private UploadedDocumentRepository documentController;

    @Override
    public List<ResultSearch> getAllDocuments() {
        List<ResultSearch> documents=new ArrayList<>();
        documentController.findAll().forEach(document->{
            documents.add(new ResultSearch(document));
        });
        return documents;
    }

    @Override
    public List<LogMessage> saveDocument(UploadedFile files) {
        List<LogMessage> logs=new ArrayList<>();
        if (files != null && files.getContent() != null && files.getContent().length > 0
        && files.getFileName() != null) {
            byte[] file = files.getContent();
            String fileName = files.getFileName();
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (extension.equals("pdf") || extension.equals("png") || extension.equals("jpg")) {
                if (file != null && file.length!=0) {
                    ResultSearch document = new ResultSearch(new UploadedDocument());
                    document.getDocument().setFileName(fileName);
                    document.getDocument().setContent(file);
                    document.setScore(0.0);
                    try{
                        document.getDocument().setHash(TextUtil.SHAsum(file));
                    }
                    catch(Exception e){
                        logs.add(new LogMessage(3, "Erreur lors du hashage du fichier"));
                    }

                    if( documentController.findByHash(document.getDocument().getHash()).isEmpty()){
                        if(extension.equals("pdf")){
                            try
                            {
                                document.getDocument().setBrut(TextUtil.getTextFromPDF(file));
                                bucketService.saveToBucket(document.getDocument().getHash()+fileName, file);
                            }
                            catch(Exception e){
                                logs.add(new LogMessage(3, "Erreur lors de la conversion du pdf en texte"));
                                return logs;
                            }
                        }
                        else{
                            try
                            {
                                document.getDocument().setBrut(ocrService.uploadImageObjectAndGetText(fileName, file));
                            }
                            catch(Exception e){
                                logs.add(new LogMessage(3, "Erreur lors de la conversion de l'image en texte"));
                                return logs;
                            }
                        }
                        documentController.save(document.getDocument());
                    }
                    else{
                        logs.add(new LogMessage(3, "Le fichier existe d??j??"));
                    }

                } else {
                    logs.add(new LogMessage(3, "Le fichier t??l??charg?? est vide"));
                }
            } else {
                logs.add(new LogMessage(3, "Le fichier t??l??charg?? n'est pas un pdf ou une image"));
            }
        } else {
            logs.add(new LogMessage(3, "Le fichier t??l??charg?? est vide"));
        }       
        return logs;
    }

    @Override
    public List<LogMessage> deleteDocument(ResultSearch document) {
        documentController.delete(document.getDocument());
        bucketService.deleteFromBucket(document.getDocument().getHash()+document.getDocument().getFileName());
        List<LogMessage> logs=new ArrayList<>();
        logs.add(new LogMessage(1, "Le fichier a ??t?? supprim??"));
        return logs;
    }

    @Override
    public List<LogMessage> updateDocument(ResultSearch document) {
        documentController.save(document.getDocument());
        List<LogMessage> logs=new ArrayList<>();
        logs.add(new LogMessage(1, "Le fichier a ??t?? mis ?? jour"));
        return logs;
    }

    @Override
    public List<ResultSearch> searchDocumentWhithContent(String content) {
        if(content!=null){
            List<ResultSearch> documents=new ArrayList<>();
            documentController.findAll().forEach(document->{
                ResultSearch result=new ResultSearch(content, document);
                documents.add(result);
            });
            Collections.sort(documents);
            return documents;
        }
        else{
            return getAllDocuments();
        }
    }

}

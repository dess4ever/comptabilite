package com.high4resto.comptabilite.utils;

import java.util.List;

import com.high4resto.comptabilite.documents.LogMessage;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

public class PrimefaceUtil {
    // Write info message to primeface growl
    public static void info(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", message));
    }
    // Write error message to primeface growl
    public static void error(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", message));
    }
    // Write warn message to primeface growl
    public static void warn(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Attention", message));
    }
    // Write List of all messages to primeface growl
    public static void addMessages(List<LogMessage> messages) {
        for (LogMessage message : messages) {
            if (message.getLevel() == 0) {
                PrimefaceUtil.info(message.getMessage());
            } else if (message.getLevel() == 1) {
                PrimefaceUtil.warn(message.getMessage());
            } else if (message.getLevel() == 2) {
                PrimefaceUtil.error(message.getMessage());
            }
        }
    }
}

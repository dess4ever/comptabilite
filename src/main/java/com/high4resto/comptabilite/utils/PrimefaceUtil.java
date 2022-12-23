package com.high4resto.comptabilite.utils;

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
}

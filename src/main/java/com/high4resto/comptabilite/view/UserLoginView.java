package com.high4resto.comptabilite.view;

import java.io.Serializable;

import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.high4resto.comptabilite.documents.User;
import com.high4resto.comptabilite.repository.UserRepository;
import com.high4resto.comptabilite.utils.TextUtil;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Getter;
import lombok.Setter;

@Component("login")
@SessionScope
public class UserLoginView implements Serializable{
    @Getter @Setter
    private String username;
    @Getter @Setter
    private String password;
    @Getter @Setter
    private boolean loggedIn=false;
    @Getter @Setter
    private boolean first=false;
    @Getter @Setter
    private String role;
    @Getter @Setter
    private User user;


    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        long countUser=this.userRepository.count();
        System.out.println("countUser: "+countUser);
        if(countUser==0)
        {
            this.first=true;
        }

    }

    public void login() {
        FacesMessage message = null;
        User tpUser=userRepository.findByUsername(username);
        if(tpUser==null)
        {
            loggedIn = false;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Loggin Error", "Invalid credentials");
        }
        else
        if(username != null  && TextUtil.generateSha1(password).equals(tpUser.getPassword())) {
            loggedIn = true;
            this.user=tpUser;
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome", username);
        } else {
            loggedIn = false;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Loggin Error", "Invalid credentials");
        }
         
        FacesContext.getCurrentInstance().addMessage(null, message);
        PrimeFaces.current().ajax().addCallbackParam("loggedIn", loggedIn);
    }

    public void disconnect() {
        loggedIn=false;
        System.out.println("disconnect");
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    }  

}

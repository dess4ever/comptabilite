package com.high4resto.comptabilite.view;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.high4resto.comptabilite.documents.User;
import com.high4resto.comptabilite.repository.UserRepository;
import com.high4resto.comptabilite.utils.PrimefaceUtil;
import com.high4resto.comptabilite.utils.TextUtil;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import lombok.Getter;
import lombok.Setter;

@Component("login")
@SessionScope
public class UserLoginView implements Serializable{
    private static final long serialVersionUID = -1090833621398787682L;
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
        if(countUser==0)
        {
            this.first=true;
        }

    }

    public void login() {
        User tpUser=userRepository.findByUsername(username);
        if(tpUser==null)
        {
            loggedIn = false;
            PrimefaceUtil.error("L'utilisateur n'existe pas");
        }
        else
        if(username != null  && TextUtil.generateSha1(password).equals(tpUser.getPassword())) {
            loggedIn = true;
            this.user=tpUser;
        } else {
            loggedIn = false;
            PrimefaceUtil.error("Mot de passe incorrect");   
        }
         
    }

    public void disconnect() {
        loggedIn=false;
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    }  

}

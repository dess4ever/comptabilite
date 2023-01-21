package com.high4resto.comptabilite.view;

import java.io.Serializable;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.high4resto.comptabilite.documents.Tva;
import com.high4resto.comptabilite.documents.User;
import com.high4resto.comptabilite.repository.TvaRepository;
import com.high4resto.comptabilite.repository.UserRepository;
import com.high4resto.comptabilite.utils.TextUtil;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import lombok.Getter;
import lombok.Setter;

@SessionScope
@Component("firstConnection")
public class FirstConnectionView implements Serializable {
    private static final long serialVersionUID = -1468804121595417606L;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TvaRepository tvaRepository;
    @Getter @Setter
    private User user;

    public void save() {
        this.user.setRole("admin");
        this.userRepository.save(this.user);
        
        Tva tva=new Tva();
        tva.setValue(2.1);
        tva.setCode("A");
        tva.setValidity(new Date().toString());
        this.tvaRepository.save(tva);
        tva=new Tva();
        tva.setValue(5.5);
        tva.setCode("B");
        tva.setValidity(new Date().toString());
        this.tvaRepository.save(tva);
        tva=new Tva();
        tva.setValue(10.0);
        tva.setCode("C");
        tva.setValidity(new Date().toString());
        this.tvaRepository.save(tva);
        tva=new Tva();
        tva.setValue(20);
        tva.setCode("D");
        tva.setValidity(new Date().toString());
        this.tvaRepository.save(tva);

        this.user.setPassword(TextUtil.generateSha1(this.user.getPassword()));
        this.userRepository.save(this.user);
        try {
            Thread.sleep(2000);
            FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @PostConstruct
    public void init() {
        this.user=new User();
    }

}

package com.high4resto.comptabilite.view;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.high4resto.comptabilite.services.implementations.AiService;
import com.high4resto.comptabilite.utils.TextUtil;

import lombok.Getter;
import lombok.Setter;

@Component
@SessionScope
public class Terminal implements Serializable {

    private static final long serialVersionUID = 7198126702926881167L;
    @Autowired
    private AiService aiService;
    @Getter @Setter
    private String test="";

    @Getter @Setter
    private String welcome="Bienvenu sur le terminal du logiciel de comptabilité<br/>Pour afficher la liste de commandes disponible tapez ls cmd.<br/> Pour utiliser l'assistance tapez H: **Votre question**";

    public String handleCommand(String command, String[] params)
    {
        if("H:".equals(command))
        {
            String prompt="";
            for(String acu:params)
            {
                prompt+=acu+" ";
            }
            return aiService.chat(prompt+"\nIA:");
        }
        if("C:".equals(command))
        {
            String prompt="";
            for(String acu:params)
            {
                prompt+=acu+" ";
            }
            return String.valueOf(TextUtil.evaluateArithmeticExpression(prompt));
        }
        return command;
    }

    public void clear()
    {
        aiService.reset();
    }
}

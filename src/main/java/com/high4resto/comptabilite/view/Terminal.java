package com.high4resto.comptabilite.view;

import java.io.Serializable;

import org.springframework.stereotype.Component;

import com.high4resto.comptabilite.utils.OpenAiUtil;
import com.high4resto.comptabilite.utils.TextUtil;

import jakarta.faces.view.ViewScoped;
import lombok.Getter;
import lombok.Setter;

@Component
@ViewScoped
public class Terminal implements Serializable {

    private OpenAiUtil openAiUtil=new OpenAiUtil();
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
            return openAiUtil.chat(prompt+"\nIA:");
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
        openAiUtil.reset();
    }
}

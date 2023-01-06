package com.high4resto.comptabilite.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document
public class Society {
    @Id
    private String id;
    @Getter @Setter
    private Address address;
    @Getter @Setter
    private String siret;
    @Getter @Setter
    private String siren;
    @Getter @Setter
    private String rcs;
    @Getter @Setter
    private String tva_Number;
    @Getter @Setter
    private String phone;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private String description;
    @Getter @Setter
    private String other_Info;
    @Getter @Setter
    private String contact; 
    @Getter @Setter
    private String ape;
    public Society()
    {
        this.address=new Address();
    }
    @Override
    public String toString()
    {
        String buffer="";
        buffer+=name+"\n"+"Siret:"+siret+"\n";
        buffer+="Numéro de TVA:"+tva_Number+"\n";
        buffer+="RCS:"+rcs+"\n";
        buffer+=address.toString()+"\n";
        buffer+="Téléphone:"+phone+"\n";
        return buffer;
    }

    public int getStrenght()
    {
        int strenght=0;
        if(siret!=null && !siret.isEmpty())
            strenght+=1;
        if(siren!=null && !siren.isEmpty())
            strenght+=1;
        if(rcs!=null && !rcs.isEmpty())
            strenght+=1;
        if(tva_Number!=null && !tva_Number.isEmpty())
            strenght+=1;
        if(phone!=null && !phone.isEmpty())
            strenght+=1;
        if(name!=null && !name.isEmpty())
            strenght+=1;
        if(description!=null && !description.isEmpty())
            strenght+=1;
        if(other_Info!=null && !other_Info.isEmpty())
            strenght+=1;
        if(contact!=null && !contact.isEmpty())
            strenght+=1;
        if(ape!=null && !ape.isEmpty())
            strenght+=1;
        return strenght;
    }
}

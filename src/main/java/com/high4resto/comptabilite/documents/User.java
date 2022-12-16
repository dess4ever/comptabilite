package com.high4resto.comptabilite.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "user")
public class User {
    @Id
    private String id;
    @Getter @Setter
    @Indexed(unique = true)
    private String username;
    @Getter @Setter
    private String firstName;
    @Getter @Setter
    private String lastName;
    @Getter @Setter
    private String password;
    @Getter @Setter
    private String role;
    @Getter @Setter
    private String email;
    @Getter @Setter
    private Society society;
    @Getter @Setter
    private Rib rib;
    public User()
    {
        this.society=new Society();
        this.rib=new Rib();
    }
}

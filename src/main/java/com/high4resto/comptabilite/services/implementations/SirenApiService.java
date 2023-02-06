package com.high4resto.comptabilite.services.implementations;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.high4resto.comptabilite.documents.Society;
import com.high4resto.comptabilite.documents.Vendor;

@Service
public class SirenApiService {
    // get json from api data.sire-api.fr with siret. header must be X-Client-Secret and value must be setting by env variable SIREN_TOKEN
    private String getJsonFromSiret(String siret) throws IOException {
        String url="https://data.siren-api.fr/v3/etablissements/" + siret;
        String json = "";

            json = Jsoup.connect(url)
                    .header("X-Client-Secret", System.getenv("SIREN_TOKEN"))
                    .ignoreContentType(true)
                    .execute()
                    .body();


        return json;
    }
    //Get field value from siren json with GSON
    private String getFielFromSirenJson(String json, String field) {
        String value = "";
        JsonObject jsonObject =JsonParser.parseString(json).getAsJsonObject();
        if (jsonObject.has("etablissement")) {
            value = jsonObject.get("etablissement").toString();
        }
        jsonObject = JsonParser.parseString(value).getAsJsonObject();
        if (jsonObject.has(field)) {
            try{
                value = jsonObject.get(field).getAsString();

            }
            catch(Exception e){
                value = jsonObject.get(field).toString();
            }
        }
        return value;
    }
    // get field from json with GSON
    private String getFielFromJson(String json, String field) {
        String value = "";
        JsonObject jsonObject =JsonParser.parseString(json).getAsJsonObject();
        if (jsonObject.has(field)) {
            try{
                value = jsonObject.get(field).getAsString();

            }
            catch(Exception e){
                value = jsonObject.get(field).toString();
            }
        }
        return value;
    }

    // Get Tva number from siren
    public String getTvaFromSiren(String siren) {
        StringBuilder tva = new StringBuilder();
        tva.append("FR");
        int sirenInt = Integer.parseInt(siren);
        int key = (12+3*(sirenInt%97))%97;
        tva.append(String.format("%02d", key));
        tva.append(siren);
        return tva.toString();
    }
    // Verify if the SIRET is valid with regex
    public boolean isSiretValid(String siret) {
        return siret.matches("^[0-9]{14}$");
    }

    // retrive vendor data with sitet from api data.sire-api.fr
    public Vendor getVendorFromSiret(String siret) throws IOException {
        Vendor vendor = new Vendor();
        String json = getJsonFromSiret(siret);
        if (json != "") {
            vendor.getSociety().setSiret(siret);
            
            String siren = getFielFromSirenJson(json, "siren");
            if(siren != "null")
                vendor.getSociety().setSiren(siren);
            
            String nom= getFielFromJson(getFielFromSirenJson(json, "unite_legale"),"denomination" );
            if(nom != "null")
            vendor.getSociety().setName(nom);

            StringBuilder address = new StringBuilder();
            String numero_voie = getFielFromSirenJson(json, "numero_voie");
            if(!numero_voie.equals("null"))
                address.append(numero_voie+" ");
            String type_voie = getFielFromSirenJson(json, "type_voie");
            if(!type_voie.equals("null"))
                address.append(type_voie+" ");
            String libelle_voie = getFielFromSirenJson(json, "libelle_voie");
            if(!libelle_voie.equals("null"))
                address.append(libelle_voie+" ");
            String complement_adresse = getFielFromSirenJson(json, "complement_adresse");
            if(!complement_adresse.equals("null"))
                address.append(complement_adresse+" ");
            String code_postal = getFielFromSirenJson(json, "code_postal");
            if(!code_postal.equals("null"))
                address.append(code_postal+" ");
            String libelle_commune = getFielFromSirenJson(json, "libelle_commune");
            if(!libelle_commune.equals("null"))
                address.append(libelle_commune+" ");        
            vendor.getSociety().getAddress().setName(address.toString());
            
            String ape= getFielFromSirenJson(json, "activite_principale");
            if(!ape.equals("null"))
                vendor.getSociety().setApe(ape);

            vendor.getSociety().setTva_Number(getTvaFromSiren(vendor.getSociety().getSiren()));
        }
        return vendor;
    }
    public Society getCustomerFromSiret(String siret) throws IOException
    {
        Society society = new Society();
        String json = getJsonFromSiret(siret);
        if (json != "") {
            society.setSiret(siret);
            
            String siren = getFielFromSirenJson(json, "siren");
            if(siren != "null")
                society.setSiren(siren);
            
            String nom= getFielFromJson(getFielFromSirenJson(json, "unite_legale"),"denomination" );
            if(nom != "null")
            society.setName(nom);

            StringBuilder address = new StringBuilder();
            String numero_voie = getFielFromSirenJson(json, "numero_voie");
            if(!numero_voie.equals("null"))
                address.append(numero_voie+" ");
            String type_voie = getFielFromSirenJson(json, "type_voie");
            if(!type_voie.equals("null"))
                address.append(type_voie+" ");
            String libelle_voie = getFielFromSirenJson(json, "libelle_voie");
            if(!libelle_voie.equals("null"))
                address.append(libelle_voie+" ");
            String complement_adresse = getFielFromSirenJson(json, "complement_adresse");
            if(!complement_adresse.equals("null"))
                address.append(complement_adresse+" ");
            String code_postal = getFielFromSirenJson(json, "code_postal");
            if(!code_postal.equals("null"))
                address.append(code_postal+" ");
            String libelle_commune = getFielFromSirenJson(json, "libelle_commune");
            if(!libelle_commune.equals("null"))
                address.append(libelle_commune+" ");        
            society.getAddress().setName(address.toString());
            
            String ape= getFielFromSirenJson(json, "activite_principale");
            if(!ape.equals("null"))
                society.setApe(ape);

            society.setTva_Number(getTvaFromSiren(society.getSiren()));
        }
        return society;
    }
}


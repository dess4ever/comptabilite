package com.high4resto.comptabilite.utils;

import java.io.IOException;

import org.jsoup.Jsoup;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.high4resto.comptabilite.documents.Vendor;

public class SirenApiUtil {
    // get json from api data.sire-api.fr with siret. header must be X-Client-Secret and value must be setting by env variable SIREN_TOKEN
    private static String getJsonFromSiret(String siret) {
        String url="https://data.siren-api.fr/v3/etablissements/" + siret;
        String json = "";
        try {
            json = Jsoup.connect(url)
                    .header("X-Client-Secret", System.getenv("SIREN_TOKEN"))
                    .ignoreContentType(true)
                    .execute()
                    .body();
        } catch (IOException e) {
            e.printStackTrace();
        return "";
        }

        return json;
    }
    //Get field value from siren json with GSON
    private static String getFielFromSirenJson(String json, String field) {
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
    private static String getFielFromJson(String json, String field) {
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
    public static String getTvaFromSiren(String siren) {
        StringBuilder tva = new StringBuilder();
        tva.append("FR");
        int sirenInt = Integer.parseInt(siren);
        int key = (12+3*(sirenInt%97))%97;
        tva.append(String.format("%02d", key));
        tva.append(siren);
        return tva.toString();
    }

    // retrive vendor data with sitet from api data.sire-api.fr
    public static Vendor getVendorFromSiret(String siret) {
        Vendor vendor = new Vendor();
        String json = getJsonFromSiret(siret);
        if (json != "") {
            vendor.getSociety().setSiret(siret);
            vendor.getSociety().setSiren(getFielFromSirenJson(json, "siren"));
            vendor.getSociety().setName( getFielFromJson(getFielFromSirenJson(json, "unite_legale"),"denomination" ));
            vendor.getSociety().getAddress().setName(
                getFielFromSirenJson(json, "numero_voie")
            +" "+getFielFromSirenJson(json, "type_voie")
            +" "+getFielFromSirenJson(json, "libelle_voie")
            +" "+getFielFromSirenJson(json, "complement_adresse")
            +" "+getFielFromSirenJson(json, "code_postal")
            +" "+getFielFromSirenJson(json, "libelle_commune"));
            vendor.getSociety().setApe(getFielFromSirenJson(json, "activite_principale"));
            vendor.getSociety().setTva_Number(getTvaFromSiren(vendor.getSociety().getSiren()));
        }
        return vendor;
    }
}


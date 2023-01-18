package com.high4resto.comptabilite.dataStruct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TreeDictionnary {
    private TreeDictionnaryNode root;

    public TreeDictionnary() {
        root = new TreeDictionnaryNode(' ');
    }

    public boolean addWord(String m, String f, int frequence) {
        return root.addWord(m, f, 0, frequence);
    }

    public boolean addWord(String m) {
        if (m != null && m.length() > 3) {
            boolean result = searchWordandAdd(m);
            if (!result) {
                return root.addWord(m.toLowerCase(), m, 0, 1);
            }
            return result;

        }
        return false;
    }

    private boolean searchWordandAdd(String m) {
        m += '*';
        if (isPrefixed(m)) {
            TreeDictionnaryNode parcoureur = root.searchChildre(m.charAt(0));
            for (int i = 1; i < m.length(); i++) {
                parcoureur = parcoureur.searchChildre(m.charAt(i));
            }

            parcoureur.getMot().setFrequence(parcoureur.getMot().getFrequence() + 1);
            return true;
        }
        return false;
    }

    public boolean isPrefixed(String m) {
        return root.isPrefixed(m, 0);
    }

    public boolean isWord(String m) {
        return root.isWord(m, 0);
    }

    public List<String> getList(String begin, int size) {
        List<String> liste = new ArrayList<String>();
        if (isPrefixed(begin)) {
            TreeDictionnaryNode parcoureur = root.searchChildre(begin.charAt(0));
            for (int i = 1; i < begin.length(); i++) {
                parcoureur = parcoureur.searchChildre(begin.charAt(i));
            }
            List<Mot> list = new ArrayList<Mot>();
            parcoureur.toList(list);
            try
            {
                Collections.sort(list);
            }
            catch (Exception e) {
           }
            int limite = list.size();
            if (limite > size)
                limite = size;
            for (int i = 0; i < limite; i++) {
                if(list.get(i)!=null)
                liste.add(list.get(i).getMot());
            }
        }
        return liste;
    }

    public List<Mot> getListMot(String begin, int size) {
        List<Mot> liste = new ArrayList<Mot>();
        if (isPrefixed(begin)) {
            TreeDictionnaryNode parcoureur = root.searchChildre(begin.charAt(0));
            for (int i = 1; i < begin.length(); i++) {
                parcoureur = parcoureur.searchChildre(begin.charAt(i));
            }
            parcoureur.toList(liste);
            Collections.sort(liste);
            int limite = liste.size();
            if (limite > size)
                limite = size;
            liste = liste.subList(0, limite);
        }
        return liste;
    }

    public List<String> getAll() {
        List<String> liste = new ArrayList<String>();
        List<Mot> list = new ArrayList<Mot>();
        root.toList(list);
        Collections.sort(list);
        for (Mot mot : list) {
            liste.add(mot.getMot());
        }
        return liste;
    }

}

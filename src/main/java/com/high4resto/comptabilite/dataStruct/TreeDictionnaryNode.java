package com.high4resto.comptabilite.dataStruct;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class TreeDictionnaryNode {
    private Character c;
    private LinkedList<TreeDictionnaryNode> childrens=new LinkedList<TreeDictionnaryNode>();
    @Getter @Setter
    private Mot mot;

    public TreeDictionnaryNode(Character c) {
        this.c = c;
    }

    public boolean addWord(String m,String f,int pos, int frequence)
    {
        assert(pos<m.length());
        if(pos==m.length())
        {
            for(TreeDictionnaryNode n:childrens)
            {
                if(n.c=='*')
                return false;
            }
            TreeDictionnaryNode newNode=new TreeDictionnaryNode('*');
            Mot mot=new Mot();
            mot.setMot(f);
            mot.setFrequence(frequence);
            newNode.setMot(mot);
            childrens.add(newNode);
            return true;
        }

        for(TreeDictionnaryNode n:childrens)
        {
            if(n.c==m.charAt(pos))
            {
                return n.addWord(m,f,pos+1,frequence);
            }
        }

        TreeDictionnaryNode newNode=new TreeDictionnaryNode(m.charAt(pos));
        childrens.add(newNode);
        return newNode.addWord(m,f,pos+1,frequence);
    }

    public boolean isPrefixed(String m,int pos)
    {
        assert(pos<m.length());
        if(pos==m.length())
        {
            return true;
        }
        for(TreeDictionnaryNode n:childrens)
        {
            if(n.c==m.charAt(pos))
            {
                return n.isPrefixed(m,pos+1);
            }
        }

        return false;
    }

    public boolean isWord(String m,int pos)
    {
        assert(pos<m.length());
        if(pos==m.length())
        {
            for(TreeDictionnaryNode n:childrens)
            {
                if(n.c=='*')
                return true;
            }
            return false;
        }
        for(TreeDictionnaryNode n:childrens)
        {
            if(n.c==m.charAt(pos))
            {
                return n.isWord(m,pos+1);
            }
        }

        return false;
    }

    public void addChildren(TreeDictionnaryNode a)
    {
        int pos=0;
        for(TreeDictionnaryNode n:childrens)
        {
            if (n.c>a.c)
                break;
            pos++;
        }
        childrens.add(pos,a);
    }

    public  TreeDictionnaryNode searchChildre(char c)
    {
        for(TreeDictionnaryNode n:childrens)
        {
            if(n.c==c)
                return n;
        }
        return null;
    }

    public boolean isWord()
    {
        return (searchChildre('*')!=null);
    }

    public static List<Mot> response=new ArrayList<Mot>();
    public List<Mot> getResponse()
    {
        return response;
    }

    public void toList(List<Mot> small)
    {
        if (c=='*')
        {
            small.add(mot);
        }
            for(TreeDictionnaryNode n:childrens)
            {
                n.toList(small);
            }
    }
}

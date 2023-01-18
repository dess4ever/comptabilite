package com.high4resto.comptabilite.services.implementations;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.high4resto.comptabilite.documents.ItemFilter;
import com.high4resto.comptabilite.documents.LogMessage;
import com.high4resto.comptabilite.repository.ItemFilterRepository;
import com.high4resto.comptabilite.services.contract.ItemFilterServiceContract;

@Service
public class ItemFilterService implements ItemFilterServiceContract{
    @Autowired
    private ItemFilterRepository itemFilterRepository;

    @Override
    public List<ItemFilter> getAllItemFilters() {
        return itemFilterRepository.findAll();
    }

    @Override
    public List<LogMessage> saveItemFilter(ItemFilter itemFilter) {
        List<LogMessage> logMessages = new ArrayList<>();
        if(itemFilter!=null && itemFilter.getName()!=null && !itemFilter.getName().isEmpty())
        {
            itemFilterRepository.save(itemFilter);
            logMessages.add(new LogMessage(1,"Filtre d'articles enregistré" ));
            return logMessages;
        }
        return logMessages;
    }

    @Override
    public List<LogMessage> deleteItemFilter(ItemFilter itemFilter) {
        List<LogMessage> logMessages = new ArrayList<>();
        if(itemFilter!=null && itemFilter.getName()!=null && !itemFilter.getName().isEmpty())
        {
            itemFilterRepository.delete(itemFilter);
            logMessages.add(new LogMessage(1,"Filtre d'articles supprimé" ));
            return logMessages;
        }
        return logMessages;
    }

    @Override
    public List<LogMessage> updateItemFilter(ItemFilter itemFilter) {
        List<LogMessage> logMessages = new ArrayList<>();
        if(itemFilter!=null && itemFilter.getName()!=null && !itemFilter.getName().isEmpty())
        {
            itemFilterRepository.save(itemFilter);
            logMessages.add(new LogMessage(1,"Filtre d'articles enregistré" ));
            return logMessages;
        }
        return logMessages;
    }
    
}

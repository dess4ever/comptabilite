package com.high4resto.comptabilite.services.contract;

import java.util.List;

import com.high4resto.comptabilite.documents.ItemFilter;
import com.high4resto.comptabilite.documents.LogMessage;

public interface ItemFilterServiceContract {
    public abstract List<ItemFilter> getAllItemFilters();
    public abstract List<LogMessage> saveItemFilter(ItemFilter itemFilter);
    public abstract List<LogMessage> deleteItemFilter(ItemFilter itemFilter);
    public abstract List<LogMessage> updateItemFilter(ItemFilter itemFilter);  
}

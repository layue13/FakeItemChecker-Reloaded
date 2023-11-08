package com.layue13.fakeitemcheckerreloaded.fakeditem.database;

import com.layue13.fakeitemcheckerreloaded.fakeditem.FakedItem;

import java.util.Collection;

public abstract class FakedItemDatabase {
    public abstract void init();

    public abstract Collection<FakedItem> getFakedItemList();

    public abstract void addFakedItem(FakedItem fakedItem);

    public abstract boolean removeFakedItem(FakedItem fakedItem);
}

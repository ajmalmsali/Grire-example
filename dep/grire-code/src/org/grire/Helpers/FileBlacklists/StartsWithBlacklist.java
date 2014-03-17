package org.grire.Helpers.FileBlacklists;

import java.util.LinkedHashSet;

/**
 * Created by Lazaros on 7/2/2014.
 */
public class StartsWithBlacklist implements Blacklist {

    LinkedHashSet<String> list;

    public StartsWithBlacklist(String... prefixes) {
        list=new LinkedHashSet<>();
        for (String s : prefixes)
            list.add(s);
    }

    @Override
    public boolean isBlacklisted(String name) {
        for (String s:list)
            if (name.startsWith(s)) return true;
        return false;
    }
}

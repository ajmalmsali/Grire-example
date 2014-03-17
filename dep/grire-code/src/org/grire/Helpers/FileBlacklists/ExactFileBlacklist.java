package org.grire.Helpers.FileBlacklists;

import java.util.LinkedHashSet;

/**
 * Created by Lazaros on 7/2/2014.
 */
public class ExactFileBlacklist implements Blacklist {

    LinkedHashSet<String> list;

    public ExactFileBlacklist(String... filenames) {
        list=new LinkedHashSet<>();
        for (String s : filenames)
            list.add(s);
    }

    @Override
    public boolean isBlacklisted(String name) {
        return list.contains(name);
    }

}

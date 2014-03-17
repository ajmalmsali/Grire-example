package org.grire.Helpers.FileBlacklists;

import java.util.LinkedHashSet;

/**
 * Created by Lazaros on 7/2/2014.
 */
public class ExtensionBlacklist implements Blacklist {

    LinkedHashSet<String> list;

    public ExtensionBlacklist(String... extensions) {
        list=new LinkedHashSet<>();
        for (String s : extensions)
            list.add(s);
    }

    @Override
    public boolean isBlacklisted(String name) {
        return list.contains(name.substring(name.lastIndexOf('.')));
    }
}

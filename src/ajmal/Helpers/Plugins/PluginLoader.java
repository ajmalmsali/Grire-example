/* This file is part of the GRire project: https://code.google.com/p/grire/
 *
 * GRire is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRire is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 * Copyright (C) 2013 Lazaros Tsochatzidis <ltsochat at ee.duth.gr>
 */
package ajmal.Helpers.Plugins;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.util.PluginManagerUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

/**
 * Author: Lazaros Tsochatzidis
 */
public class PluginLoader {

    public static List<GRirePlugin> LoadPluginsFromDefaultFile() throws FileNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, ClassNotFoundException, InvocationTargetException, MalformedURLException {
        File plugs=new File("plugins");
        Scanner sc=new Scanner(plugs);
        List<GRirePlugin> pluginsAll=new ArrayList<>();
        while (sc.hasNext()) {
            String line=sc.nextLine();
            if (!line.equals("")) {
                pluginsAll.addAll(PluginLoader.LoadPluginsFromJAR(new File(line)));
            }
        }
        return pluginsAll;
    }

    public static Collection<GRirePlugin> LoadPluginsFromJAR(File jarFile) {
        PluginManager pm;
        pm = PluginManagerFactory.createPluginManager();
        pm.addPluginsFrom(jarFile.toURI());
        Collection<GRirePlugin> c=new PluginManagerUtil(pm).getPlugins(GRirePlugin.class);
        System.out.println(c.size());
        return c;
    }
}

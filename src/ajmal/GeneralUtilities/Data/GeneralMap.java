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

package ajmal.GeneralUtilities.Data;

import ajmal.GeneralUtilities.GeneralStorers.GeneralStorer;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * Created with IntelliJ IDEA.
 * User: Lazaros
 * Date: 24/8/2013
 * Time: 1:32 μμ
 * To change this template use File | Settings | File Templates.
 */
public class GeneralMap<K,V> extends GeneralData implements Map<K,V>,Iterable{
    private boolean creation=false;
    private GeneralStorer storer;
    String name,origName;

    public GeneralMap(GeneralStorer storer,String name) throws Exception{
        this.name="generalmap."+name;
        origName=name;
        if (!storer.GetSet("GeneralMaps").contains(name)) throw new Exception("This map does not exist. Set up your descriptor first!");
        this.storer=storer;
    }

    public GeneralMap(GeneralStorer storer,String name, boolean create) throws Exception {
        this.name="generalmap."+name;
        origName=name;
        if (!create && !storer.GetSet("GeneralMaps").contains(name)) throw new Exception("This map does not exist. Set up your descriptor first!");
        if (create && storer.GetSet("GeneralMaps").contains(name)) throw new Exception("This map already exists!");
        if (create) storer.GetSet("GeneralMaps").add(name);
        creation=create;
        this.storer=storer;
    }


    @Override
    public int size() {
        return storer.GetMap(this.name).size();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isEmpty() {
        return storer.GetMap(this.name).isEmpty();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean containsKey(Object key) {
        return storer.GetMap(this.name).containsKey(key);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean containsValue(Object value) {
        return storer.GetMap(this.name).containsValue(value);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public V get(Object key) {
        Map<K,V> temp = storer.GetMap(this.name);
        return temp.get(key);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public V put(K key, V value) {
        Map<K,V> temp = storer.GetMap(this.name);
        return temp.put(key,value);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public V remove(Object key) {
        Map<K,V> temp = storer.GetMap(this.name);
        return temp.remove(key);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        Map<K,V> temp = storer.GetMap(this.name);
        temp.putAll(m);
    }

    @Override
    public void clear() {
        storer.GetMap(this.name).clear();
    }

    @Override
    public Set<K> keySet() {
        Map<K,V> temp = storer.GetMap(this.name);
        return temp.keySet();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<V> values() {
        Map<K,V> temp = storer.GetMap(this.name);
        return temp.values();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Map<K,V> temp = storer.GetMap(this.name);
        return temp.entrySet();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterator iterator() {
        return storer.GetMap(this.name).keySet().iterator();  //To change body of implemented methods use File | Settings | File Templates.
    }


    public static Set<String> getGeneralMaps(GeneralStorer storer) {
        return storer.GetSet("GeneralMaps");
    }

    public boolean forCreation() {
        return creation;
    }

    public void Delete() {
        this.storer.GetMap(this.name).clear();
        storer.GetSet("GeneralMaps").remove(origName);
    }
}

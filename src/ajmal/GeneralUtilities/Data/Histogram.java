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

import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;

/**
 * Author: Lazaros Tsochatzidis
 */
public class Histogram<T extends Number> extends GeneralData implements Iterable{
    private boolean creation=false;
    private GeneralStorer storer;
    String name,origName;

    public Histogram(GeneralStorer storer,String name) throws Exception{
        this.name="histogram."+name;
        origName=name;
        if (!storer.GetSet("Histograms").contains(name)) throw new Exception("This histogram does not exist. Set up your descriptor first!");
        this.storer=storer;
    }

    public Histogram(GeneralStorer storer,String name, boolean create) throws Exception {
        this.name="histogram."+name;
        origName=name;
        if (!create && !storer.GetSet("Histograms").contains(name)) throw new Exception("This histogram does not exist. Set up your descriptor first!");
        if (create && storer.GetSet("Histograms").contains(name)) throw new Exception("This histogram already exists!");
        if (create) storer.GetSet("Histograms").add(name);
        creation=create;
        this.storer=storer;
    }

    public float getValueAt(float pos) {
        return (Float)storer.GetSortedMap(this.name).get(pos);
    }

    public boolean forCreation() {
        return creation;
    }

    public void setValueAt(float pos, float value) {

        storer.GetSortedMap(this.name).remove(pos);
        storer.GetSortedMap(this.name).put(pos,value);
    }

    public static Set getHistograms(GeneralStorer storer) {
        return storer.GetSet("Histograms");
    }

    @Override
    public Iterator iterator() {
        return storer.GetSortedMap(this.name).keySet().iterator();
    }

    public void Delete() {
        this.storer.GetSortedMap(this.name).clear();
        storer.GetSet("Histograms").remove(origName);
    }
}

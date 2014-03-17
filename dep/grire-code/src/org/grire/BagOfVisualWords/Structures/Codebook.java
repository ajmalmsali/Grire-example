/*
 * Copyright (C) 2013 Lazaros Tsochatzidis
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.grire.BagOfVisualWords.Structures;

import org.grire.GeneralUtilities.GeneralStorers.GeneralStorer;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This is the core of the BOVW model. It is a lexicon of key-features selected from the Features structure.
 */
public class Codebook extends Structure implements Iterable  {
    String name,origName;
    GeneralStorer storer;

    public int size() {
        return storer.GetSortedMap(name).size();
    }

    boolean firstTime=false;
    boolean creation=false;
    
    public Codebook(GeneralStorer storer, String name) throws Exception {
        this.name="codebook."+name;
        origName=name;
        if (!storer.GetSet("Codebooks").contains(name)) throw new Exception("This codebook does not exist. Set up your descriptor first!");
        this.storer=storer;
    }

    public Codebook(GeneralStorer storer, String name, boolean create) throws Exception {
        this.name="codebook."+name;
        origName=name;
        if (!create && !storer.GetSet("Codebooks").contains(name)) throw new Exception("This codebook does not exist. Set up your descriptor first!");
        if (create && storer.GetSet("Codebooks").contains(name)) throw new Exception("This codebook already exists!");
        if (create) storer.GetSet("Codebooks").add(name);
        this.storer=storer;
        firstTime=create;
        creation=create;
    }

    /**
     * Returns the word in the provided position.
     * @param pos
     * @return
     */
    public float[] getCodebookWord(int pos) {
        return (float[]) storer.GetSortedMap(name).get(pos);
    }

    /**
     * Stores the codebook provided in form of array.
     * @param cb
     */
    public void storeCodebook(float[][] cb) {
        Map map = storer.GetSortedMap(name);
        map.clear();
        for (int i=0;i<cb.length;i++) {
            map.put(i, cb[i]);
            storer.insertionMade();
        }
    }

    public GeneralStorer getStorer() {
        return storer;
    }

    public boolean forCreation() {
        return creation;
    }

    public void delete() {
        storer.DeleteSortedMap(name);
        storer.GetSet("Codebooks").remove(origName);
    }

    /**
     * Returns the codebook in form of array.
     * @return
     */
    public float[][] toArray() {
        float[][] ret=new float[size()][];
        int i=0;
        final Map map = storer.GetSortedMap(name);
        for (Object o : this) {
            ret[i++] = (float[]) map.get(o);
        }
        return ret;
    }

    @Override
    public Iterator iterator() {
        return storer.GetSortedMap(name).keySet().iterator();
    }

    /**
     * Returns a set with the name of the existing codebooks in the storer.
     * @param storer
     * @return
     */
    public static Set getCodebooks(GeneralStorer storer) {
        return storer.GetSet("Codebooks");
    }

    public void setUsedToCreate(Index in) {
        storer.AddToMultimap("RelationsForward",name,in.getDBName());
    }

    public Set getUsedToCreate() {
        return storer.SearchInMultimap("RelationsForward",name);
    }

    public void setCreatedFrom(PoolFeatures cf) {
        storer.AddToMultimap("RelationsBackward",name,cf.getDBName());
    }

    public Set getCreatedFrom(){
        return storer.SearchInMultimap("RelationsBackward",name);
    }

    public String getDBName() {
        return name;
    }

    public String getName() {
        return origName;
    }

    public void setCreatedFrom(String s) {
        storer.AddToMultimap("RelationsBackward",name,s);
    }

    @Override
    public String toString() {
        return origName;
    }
}

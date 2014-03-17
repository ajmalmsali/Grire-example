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
import org.grire.GeneralUtilities.Interfaces.FeatureExtractor;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *  This structure is a collection of features extracted from an ImagePool. It pairs an id from the pool with a
 *  two-dimensional array of the extracted features and descriptors.
 */
public class PoolFeatures extends Structure implements Iterable{
    
    String name,origName;
    GeneralStorer storer;
    boolean firstTime=false, creation=false;

    public PoolFeatures(GeneralStorer storer, String name) throws Exception {
        this.name="features."+name;
        origName=name;
        if (!storer.GetSet("FeaturesExtracted").contains(name)) throw new Exception("These features do not exist. Set up your descriptor first!");
        this.storer=storer;
    }

    public GeneralStorer getStorer() {
        return storer;
    }

    public PoolFeatures(GeneralStorer storer, String name, boolean create) throws Exception {
        this.name="features."+name;
        origName=name;
        if (!create && !storer.GetSet("FeaturesExtracted").contains(name)) throw new Exception("These features do not exist. Set up your descriptor first!");
        if (create && storer.GetSet("FeaturesExtracted").contains(name)) throw new Exception("Features already exist!");
        if (create) storer.GetSet("FeaturesExtracted").add(name);
        this.storer=storer;
        firstTime=create;
        creation=create;
    }

    /**
     * Sets the image’s features under the provided id.
     * @param id
     * @param features
     */
    public void setImageFeatures(long id, float[][] features) {
        storer.GetMap(this.name).put(id, features);
        storer.SetLong(this.name+"Length",storer.GetLong(this.name+"Length")+features.length);
        storer.insertionMade();
    }

    /**
     * Gets the image’s features under the provided id in an array of Objects where each Object is a float[].
     * @param id
     * @return
     */
    public Object[] getImageFeatures(long id) {
        return (Object[]) storer.GetMap(this.name).get(id);
    }
    
    public int size() {
        return storer.GetMap(this.name).size();
    }

    public long totalFeatures() {
        return storer.GetLong(this.name+"Length");
    }
    
    public void delete() {
        storer.GetSet("FeaturesExtracted").remove(origName);
        storer.DeleteMap(this.name);
    }
    
    public boolean forCreation() {
        return creation;
    }

    @Override
    public String toString() {
        return origName;
    }

    @Override
    public Iterator iterator() {
        return new Iterator() {

            Iterator init=storer.GetMap(name).keySet().iterator();

            @Override
            synchronized public boolean hasNext() {
                return init.hasNext();
            }

            @Override
            synchronized public Object next() {
                return init.next();
            }

            @Override
            synchronized public void remove() {
                init.remove();
            }
        };

    }

    /**
     * Returns a set with the name of the existing features in the storer.
     * @param storer
     * @return
     */
    public static Set<String> getFeatures(GeneralStorer storer) {
        return storer.GetSet("FeaturesExtracted");
    }

    public void setUsedToCreate(Codebook cb) {
        storer.AddToMultimap("RelationsForward",name,cb.getDBName());
    }

    public Set getUsedToCreate() {
        return storer.SearchInMultimap("RelationsForward",name);
    }

    public void setCreatedFrom(ImagePool ic) {
        storer.AddToMultimap("RelationsBackward",name,ic.getDBName());
    }

    public void setCreatedFrom(FeatureExtractor p) {
        storer.AddToMultimap("RelationsBackward",name,p.toString());
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

    public void setUsedToCreate(Index index) {
        storer.AddToMultimap("RelationsForward",name,index.getDBName());
    }

    /**
     * Deletes the features under the provided id.
     * @param id
     * @throws Exception
     */
    public void deleteId(long id) throws Exception{
        Map map = storer.GetMap(this.name);
        if (!map.keySet().contains(id)) throw new Exception ("There is no entry with this ID:" + id);
        map.remove(id);
    }
}

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

import org.grire.BagOfVisualWords.Interfaces.VisualWordDescriptor;
import org.grire.GeneralUtilities.GeneralStorers.GeneralStorer;

import java.util.*;

/**
 * This is the final structure of the model. This structure stores the bags of each image in the pool along with the
 * DocumentFrequency of every word in the codebook.
 */
public class Index extends Structure implements Iterable {
    String name,origName;
    GeneralStorer storer;
    boolean firstTime=false, creation=false;
    int size=-1;

    
    public Index(GeneralStorer storer, String name) throws Exception {
        this.name="index."+name;
        origName=name;
        if (!storer.GetSet("IndicesCreated").contains(name)) throw new Exception("This index does not exist. Set up your descriptor first!");
        this.storer=storer;
    }

    public GeneralStorer getStorer() {
        return storer;
    }

    public Index(GeneralStorer storer, String name, boolean create) throws Exception {
        this.name="index."+name;
        origName=name;
        if (!create && !storer.GetSet("IndicesCreated").contains(name)) throw new Exception("This index does not exist. Set up your descriptor first!");
        if (create && storer.GetSet("IndicesCreated").contains(name)) throw new Exception("Index already exists!");
        if (create) storer.GetSet("IndicesCreated").add(name);
        this.storer=storer;
        firstTime=create;
        creation=create;
    }

    /**
     * Sets the bag of the image under the id provided.
     * @param id
     * @param descriptor
     */
    public void setImageDescriptor(long id, float[] descriptor) {
        storer.GetMap(this.name).put(id, descriptor);
        for (int i=0;i<descriptor.length;i++) {
            if (descriptor[i]!=0) {
                storer.AddToMultimap(this.name+"Inverted",i,id);
            }
        }
        storer.insertionMade();
    }

    public Set<Long> lookUp(float[] descriptor) {
        HashSet<Long> ret=new HashSet<>();
        for (int i=0;i<descriptor.length;i++) {
            if (descriptor[i]!=0)
                for (Object id: storer.SearchInMultimap(this.name+"Inverted",i))
                    ret.add((Long)id);
        }
        return ret;
    }

    /**
     * Gets the bag of the imge under the id provided.
     * @param id
     * @return
     */
    public float[] getImageDescriptor(long id) {
        return (float[]) storer.GetMap(this.name).get(id);
    }

    /**
     * Sets the document frequencies of all the codebook words.
     * @param dfs
     */
    public void setDocumentFrequencies(int[] dfs) {
        Map GetMap = storer.GetMap(this.name+"DF");
        for (int i=0;i<dfs.length;i++) {
            GetMap.put(i, dfs[i]);
            storer.insertionMade();
        }
    }

    /**
     * Gets the document frequencies of all the codebook words.
     * @return
     */
    public int[] getDocumentFrequencies() {
        Map GetMap = storer.GetMap(this.name+"DF");
        int[] dfs=new int[GetMap.size()];
        for (int i=0;i<dfs.length;i++) {
            dfs[i]=((Integer)GetMap.get(i)).intValue();
        }
        return dfs;
    }
    
    public void delete() {
        storer.DeleteMap(name);
        storer.GetSet("IndicesCreated").remove(origName);
        storer.DeleteMap(name+"DF");
    }
    
    public int size() {
        if (size==-1) size= storer.GetMap(this.name).size();
        return size;
    }

    @Override
    public Iterator iterator() {
        return storer.GetMap(this.name).keySet().iterator();
    }

    /**
     * Returns a set with the name of the existing indices in the storer.
     * @param storer
     * @return
     */
    public static Set getIndices(GeneralStorer storer){
        return storer.GetSet("IndicesCreated");
    }

    public void setCreatedFrom(VisualWordDescriptor cb, PoolFeatures cf) {
        storer.AddToMultimap("RelationsBackward",name,cb.toString());
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

    /**
     * Deletes the bag of the image under the provided id.
     * @param id
     * @throws Exception
     */
    public void deleteId(long id) throws Exception{
        Map map = storer.GetMap(this.name);
        if (!map.keySet().contains(id)) throw new Exception ("There is no entry with this ID");
        map.remove(id);
    }

    @Override
    public String toString() {
        return origName;
    }
}
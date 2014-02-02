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
package ajmal.BagOfVisualWords.Structures;

import ajmal.GeneralUtilities.GeneralStorers.GeneralStorer;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * An image pool is a collection of images. It pairs a unique id with the absolute path of an image and stores it in a
 * database. It provides all the necessary methods for adding and deleting an image from the pool.
 * @author Lazaros
 */
public class ImagePool extends Structure implements Iterable{

    String name,origName;
    GeneralStorer storer;
    boolean firstTime=false;
    boolean creation=false;
    
    public ImagePool(GeneralStorer storer, String name) throws Exception {
        this.name="collection."+name;
        this.origName=name;
        if (!storer.GetSet("ImageCollections").contains(name)) throw new Exception("Image collection: \""+ this.name +"\" not existing. Set up your descriptor first!");
        this.storer=storer;
    }

    public ImagePool(GeneralStorer storer, String name, boolean create) throws Exception {
        this.name="collection."+name;
        this.origName=name;
        
        if (!create && !storer.GetSet("ImageCollections").contains(name)) throw new Exception("Image collection not existing. Set up your descriptor first!");
        if (create && storer.GetSet("ImageCollections").contains(name)) throw new Exception("Image collection already exists!");
        if (create) storer.GetSet("ImageCollections").add(name);
        this.storer=storer;
        firstTime=create;
        creation=create;
    }

    /**
     * Adds a new image (its path) to the ImagePool.
     * @param path
     * @throws Exception
     */
    public void addImage(String path) throws Exception {
        if (firstTime) firstTime=false;
        storer.GetMap(this.name).put(storer.IncrementAndGetLong(name+"last"), path);
        firstTime=false;
    }

    /**
     * Returns the path of the image with the specified id.
     * @param id
     * @return
     * @throws Exception
     */
    public String getImage(long id) throws Exception {
        Map map = storer.GetMap(this.name);
        if (!map.keySet().contains(id)) throw new Exception ("There is no image with this ID");
        return (String) map.get(id);
    }

    /**
     * Deletes the path of the image with the specified id.
     * @param id
     * @throws Exception
     */
    public void deleteImage(long id) throws Exception {
        Map map = storer.GetMap(this.name);
        if (!map.keySet().contains(id)) throw new Exception ("There is no image with this ID");
        map.remove(id);
    }
    
    public void delete() {
        storer.DeleteLong(name+"last");
        storer.DeleteMap(name);
        storer.GetSet("ImageCollections").remove(origName);
    }

    @Override
    public Iterator iterator() {
            Set keySet = storer.GetMap(this.name).keySet();
            return keySet.iterator();
    }

    public GeneralStorer getStorer() {
        return storer;
    }

    public void setUsedToCreate(PoolFeatures feats) {
         storer.AddToMultimap("RelationsForward",name,feats.getDBName());
    }

    public Set getUsedToCreate() {
        return storer.SearchInMultimap("RelationsForward",name);
    }

    public String getName() {
        return origName;
    }

    public String getDBName() {
        return name;
    }

    /**
     * Returns a set with the names of the existing pools in the provided storer
     * @param storer
     * @return
     */
    public static Set<String> getPools(GeneralStorer storer) {
        return storer.GetSet("ImageCollections");
    }

    public int size() {
        return storer.GetMap(name).size();
    }

    @Override
    public String toString() {
        return origName;
    }
}

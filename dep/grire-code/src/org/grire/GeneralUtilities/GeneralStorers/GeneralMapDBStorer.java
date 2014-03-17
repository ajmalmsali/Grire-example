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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.grire.GeneralUtilities.GeneralStorers;



import org.grire.Helpers.mapdb.*;

import java.io.File;
import java.util.*;

/**
 *
 * @author Lazaros
 */
public class GeneralMapDBStorer implements GeneralStorer{

    private DB db;
    private String location;
    private int insertions=0;

    public static int CACHE_DISABLE=0;
    public static int CACHE_LRU=1;
    public static int CACHE_HARD_REF=2;
    public static int CACHE_SOFT_REF=3;
    public static int CACHE_WEAK_REF=4;
    
    public GeneralMapDBStorer(String path) {
        location=path;
        File f=new File(path);
        db = DBMaker.newFileDB(f)
                .closeOnJvmShutdown()
                .cacheDisable()
                .transactionDisable()
                .asyncWriteDisable()
                .randomAccessFileEnable()
                .make();
    }

    public GeneralMapDBStorer(String path, int cacheOption) {
        location=path;
        File f=new File(path);
        DBMaker dbmaker = DBMaker.newFileDB(f)
                .closeOnJvmShutdown()
                .transactionDisable()
                .asyncWriteDisable()
                .randomAccessFileEnable();
        switch (cacheOption){
            case 0:
                dbmaker.cacheDisable();
                break;
            case 1:
                dbmaker.cacheLRUEnable();
                break;
            case 2:
                dbmaker.cacheHardRefEnable();
                break;
            case 3:
                dbmaker.cacheSoftRefEnable();
                break;
            case 4:
                dbmaker.cacheWeakRefEnable();
                break;
        }
        db=dbmaker.make();
    }

    public GeneralMapDBStorer(String path, int cacheOption, int cacheSize) {
        location=path;
        File f=new File(path);
        DBMaker dbmaker = DBMaker.newFileDB(f)
                .closeOnJvmShutdown()
                .transactionDisable()
                .asyncWriteDisable()
                .randomAccessFileEnable();
        switch (cacheOption){
            case 0:
                dbmaker.cacheDisable();
                break;
            case 1:
                dbmaker.cacheLRUEnable();
                break;
            case 2:
                dbmaker.cacheHardRefEnable();
                break;
            case 3:
                dbmaker.cacheSoftRefEnable();
                break;
            case 4:
                dbmaker.cacheWeakRefEnable();
                break;
        }
        dbmaker.cacheSize(cacheSize);
        db=dbmaker.make();
    }

    @Override
    public synchronized void  AddToMultimap(String name, Object key, Object value) {
        //NavigableSet<Fun.Tuple2<Object,Object>> multiMap = db.createTreeSet(name).serializer(BTreeKeySerializer.TUPLE2).make();
        NavigableSet<Fun.Tuple2<Object,Object>> multiMap = db.getTreeSet(name);
        multiMap.add(Fun.t2(key,value));
    }

    @Override
    public synchronized Set SearchInMultimap(String name,Object key) {
        Set ret=new HashSet();
        NavigableSet<Fun.Tuple2<Object,Object>> multiMap = db.getTreeSet(name);
        for(Object l: Bind.findSecondaryKeys(multiMap, key)) {
            ret.add(l);
        }
        return ret;
    }

    @Override
    public synchronized SortedMap GetSortedMap(String name) {
        Set<Object> maps = db.getTreeSet("SortedMaps");
        if (!maps.contains(name))
            maps.add(name);
        return db.getTreeMap(name);
    }

    @Override
    public synchronized void DeleteSortedMap(String name) {
        Set<Object> maps = db.getTreeSet("SortedMaps");
        maps.remove(name);
        db.getTreeMap(name).clear();
    }

    @Override
    public synchronized Map GetMap(String name) {
        Set<Object> maps = db.getTreeSet("Maps");
        if (!maps.contains(name))
            maps.add(name);
        return db.getHashMap(name);
    }
    
    @Override
    public synchronized void DeleteMap(String name) {
        Set<Object> maps = db.getTreeSet("Maps");
        maps.remove(name);
        db.getHashMap(name).clear();
    }
    
    @Override
    public synchronized Set GetSet(String name) {
        Set<Object> sets = db.getTreeSet("Sets");
        if (sets.contains("name")) sets.add(name);
        return db.getHashSet(name);
    }
    
    @Override
    public synchronized void DeleteSet(String name) {
        Set<Object> sets = db.getTreeSet("Sets");
        sets.remove(name);
        db.getHashSet(name).clear();
    }
    
    @Override
    public synchronized long GetLong(String name){
        Set<Object> longs = db.getTreeSet("Longs");
        if (!longs.contains(name)) longs.add(name);
        return  db.getAtomicLong(name).longValue();
    }

    @Override
    public synchronized void SetLong(String name,long value){
        Set<Object> longs = db.getTreeSet("Longs");
        if (!longs.contains(name)) longs.add(name);
        db.createAtomicLong(name,value);
    }
    
    @Override
    public synchronized long IncrementAndGetLong(String name){
        Set<Object> longs = db.getTreeSet("Longs");
        if (longs.contains(name)) 
            return  db.getAtomicLong(name).incrementAndGet();
        else {
            db.createAtomicLong(name,0);
            longs.add(name);
            return 0;
        }

    }
    
    @Override
    public synchronized void DeleteLong(String name) {
        Set<Object> longs = db.getTreeSet("Longs");
        longs.remove(name);
    }
    
    @Override
    public synchronized String getLocation() {
        return location;
    }

    @Override
    public synchronized void setLocation(String location) {
        this.location = location;
    }
    
    @Override
    public synchronized void insertionMade() {
        if (++insertions>50) {
            db.commit();
            //db.compact();
            insertions=0;
        } 
    }
    
    @Override
    public void commit() {
        db.commit();
    }
    
    @Override
    public void close() {
        //db.compact();
        db.close();
    }


}


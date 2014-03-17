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
package org.grire.BagOfVisualWords.Functions;

import org.grire.BagOfVisualWords.Structures.ImagePool;
import org.grire.Helpers.FileBlacklists.Blacklist;
import org.grire.Helpers.Listeners.Listened;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a simple class that provides useful functions to add image in a pool from multiple folders.
 */
public class Importer extends Listened{

    ImagePool Collection;
    String[] Folders;
    Blacklist[] blacklists=new Blacklist[0];

    
    public Importer(String[] folders, ImagePool collection, Blacklist... blacklists) {
        Collection=collection;
        Folders=folders;
        this.blacklists=blacklists;
    }

    
    @Override
    public void run() {
        for (String folder:Folders){
            File[] listFiles = (new File(folder)).listFiles();
            int countFiles=listFiles.length,counter=0;
            big:
            for (File f : listFiles) {
                for (Blacklist b:blacklists)
                    if (b.isBlacklisted(f.getName()))
                        continue big;
                try {
                    System.out.println(f.getName());
                    Collection.addImage(f.getAbsolutePath());
                    counter++;
                    this.progressMade(counter, countFiles);
                }catch (Exception ex) {
                    Logger.getLogger(PoolFeatureExtractor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            Collection.getStorer().commit();
        }
    }
}

package org.test;

import org.grire.Helpers.mapdb.DB;
import org.grire.Helpers.mapdb.DBMaker;
import org.junit.Test;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Lazaros
 * Date: 2/11/2013
 * Time: 7:46 μμ
 * To change this template use File | Settings | File Templates.
 */
public class MapDBtest {

    @Test
    public void fill(){
        File file=new File("mapdbtest1");
        DB db = DBMaker.newFileDB(file)
                .closeOnJvmShutdown()
                .cacheDisable()
                .transactionDisable()
                .asyncWriteDisable()
                .randomAccessFileEnable()
                .make();
        for (int img=0;img<2000;img++){
            System.out.println(img);
            float[][] feats=new float[1000][];
            for (int f=0;f<1000;f++)
                feats[f]=new float[64];
            db.getHashMap("testmap").put(img,feats);
        }
    }
}

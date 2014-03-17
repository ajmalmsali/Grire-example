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
 * Copyright (C) 2012 Lazaros Tsochatzidis <ltsochat at ee.duth.gr>
 */


package org.grire.BagOfVisualWords;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.grire.BagOfVisualWords.Structures.Codebook;
import org.grire.BagOfVisualWords.Interfaces.VisualWordDescriptor;
import org.grire.GeneralUtilities.Interfaces.FeatureExtractor;
import org.grire.BagOfVisualWords.Interfaces.Stemmer;
import org.grire.Helpers.Listeners.Listened;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The purpose of these class is the creation of new descriptors using the
 * given utilities. The descriptor uses a parser, a stemmer and a codebook
 * to extract the vector of the image.
 * @author Lazaros Tsochatzidis <ltsochat at ee.duth.gr>
 */
@PluginImplementation
public class CustomDescriptor extends VisualWordDescriptor {
    Stemmer stemmer;
    FeatureExtractor parser;
    Codebook codebook;
    int codebookSize=-1;

    public CustomDescriptor() {
    }

    public CustomDescriptor(FeatureExtractor parser, Stemmer stemmer, Codebook codebook) {
        this.stemmer = stemmer;
        this.parser=parser;
        this.codebook=codebook;
        stemmer.setCodebook(codebook);
    }
    
    /**
     * extract the descriptor given the features of the image.
     * @return
     */
    @Override
    public float[] extract(File img) {
        try {
            if (stemmer==null || parser==null) throw new Exception("FeatureExtractor and stemmer not set. Set up the descriptor first.");
            float[][] features=parser.extract(img);
            return extract(features);
        } catch (Exception ex) {
            Logger.getLogger(CustomDescriptor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    @Override
    public float[] extract(Object[] features) {
        if (codebookSize==-1) codebookSize=stemmer.getCodebook().size();
        float[] ret = new float[codebookSize];
        try {
            if (stemmer==null || parser==null) throw new Exception("FeatureExtractor and stemmer not set. Set up the descriptor first.");
            for (Object f:features) {
                int a=stemmer.stemPoint((float[]) f);
                ret[a]++;
            }
            return ret;
        } catch (Exception ex) {
            Logger.getLogger(CustomDescriptor.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return ret;
        }
    }

    @Override
    public Codebook getCodebook() {
        return stemmer.getCodebook();
    }

    @Override
    public FeatureExtractor getParser() {
        return parser;
    }

    @Override
    public String toString() {
        return "CustomDescriptor{" +
                 parser +
                 stemmer +
                '}';
    }

    @Override
    public Listened setUp(Object... args) {
        return null;
    }

    @Override
    public Class[] getParameterTypes() {
        Class[] ret=new Class[3];
        ret[0]=FeatureExtractor.class;
        ret[1]=Stemmer.class;
        ret[2]=Codebook.class;
        return ret;
    }

    @Override
    public Class[] getSetUpParameterTypes() {
        return new Class[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{"FeatureExtractor","Stemmer","Codebook"};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getSetUpParameterNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getDefaultParameterValues() {
        return new String[]{"","",""};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getDefaultSetUpParameterValues() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean requiresSetUp() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
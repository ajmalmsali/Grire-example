package ajmal.GeneralUtilities;/* This file is part of the GRire project: https://code.google.com/p/grire/
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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import ajmal.GeneralUtilities.Interfaces.FeatureExtractor;
import ajmal.Helpers.Listeners.Listened;

@PluginImplementation
public class FeaturesInGrid extends FeatureExtractor {

    FeatureExtractor globalFeature;
    int scale,split;

    public FeaturesInGrid() {
    }

    public FeaturesInGrid(FeatureExtractor globalFeature, Float scale, Float split) {
        this.globalFeature = globalFeature;
        this.scale = scale.intValue();
        this.split = split.intValue();
    }

    @Override
    public float[][] extract(File img) {
        try {
            return extract(ImageIO.read(img));
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public float[][] extract(BufferedImage img) {
        BufferedImage[][] chunks;

        chunks = ScaleAndSplit(img);
        float[][] ips=new float[chunks.length*chunks[0].length][];
        float[] desc;
        int k=0;
        for (BufferedImage[] row : chunks){
            for (BufferedImage chunk : row) {
                float[][] data = globalFeature.extract(chunk);
                desc = new float[data[0].length];
                for (float[] d:data)     {
                    System.arraycopy(d, 0, desc, 0, d.length);
                    ips[k++]=desc;
                }
            }
        }
        return ips;
    }

    @Override
    public float[][] getPositions() {
        return new float[0][];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String toString() {
        return "FeaturesInGrid{" +
                 globalFeature +
                "," + scale +
                "," + split +
                '}';
    }

    protected BufferedImage[][] ScaleAndSplit(BufferedImage img){
        img = Scale(img);
        return Split(img);
    }

    protected BufferedImage[][] Split(BufferedImage img) {
        BufferedImage[][] chunks;
        int rows,cols;
        rows = cols = split;
        chunks = new BufferedImage[rows][cols];
        for (int x=0;x<rows;x++){
            for (int y=0;y<cols;y++){
                chunks[x][y] = new BufferedImage(split,split,img.getType());
                Graphics2D gr = chunks[x][y].createGraphics();
                gr.drawImage(img, 0, 0, split, split, split * y, split * x, split * y + split, split * x + split, null);
                gr.dispose();
            }
        }
        return chunks;
    }

    protected BufferedImage Scale(BufferedImage img){
        return getBufferedImageFromImage(img.getScaledInstance(scale, scale, Image.SCALE_SMOOTH));
    }

    protected BufferedImage getBufferedImageFromImage(Image img)
    {
        BufferedImage bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics2D gr = bufferedImage.createGraphics();
        gr.drawImage(img, 0, 0, null);
        gr.dispose();
        return bufferedImage;
    }

    @Override
    public Listened setUp(Object... args) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates
    }

    @Override
    public Class[] getParameterTypes() {
        return new Class[]{FeatureExtractor.class,Float.class,Float.class};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class[] getSetUpParameterTypes() {
        return new Class[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getParameterNames() {
        return new String[] {"Feature Extractor for the parts of grid","Side size after scaling","Chunks number (it will be squared)"};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getSetUpParameterNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getDefaultParameterValues() {
        return new String[] {"","400","16"};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getDefaultSetUpParameterValues() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean requiresSetUp() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public static int SPLIT4x4 = 4;
    public static int SPLIT8x8 = 8;
    public static int SPLIT16x16 = 16;

    public static int SCALE128 = 128;
    public static int SCALE400 = 400;
    public static int SCALE800 = 800;
}

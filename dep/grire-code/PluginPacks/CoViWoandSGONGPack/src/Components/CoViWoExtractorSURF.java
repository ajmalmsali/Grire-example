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

package Components;

import com.stromberglabs.jopensurf.SURFInterestPoint;
import com.stromberglabs.jopensurf.Surf;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.grire.GeneralUtilities.Interfaces.FeatureExtractor;
import org.grire.Helpers.Listeners.Listened;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lazaros
 */

@PluginImplementation
public class CoViWoExtractorSURF extends FeatureExtractor {

    float[][] positions;

    @Override
    public float[][] extract(File img) {
        try {
            return extract(ImageIO.read(img));
        } catch (IOException ex) {
            Logger.getLogger(CoViWoExtractorSURF.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public float[][] extract(BufferedImage img) {
        Surf surf=new Surf(img);
        final List<SURFInterestPoint> points = surf.getFreeOrientedInterestPoints();
        positions=new float[points.size()][2];
        float[][] ret=new float[points.size()][];
        int i=0;
        for (SURFInterestPoint p:points) {
            positions[i][0]=p.getX();
            positions[i][1]=p.getY();
            final float[] descriptor = p.getDescriptor();
            ret[i]=new float[descriptor.length];
            for (int j=0;j<ret[i].length;j++) {
                ret[i][j]=descriptor[j];
            }
            i++;
        }
        return ret;
    }

    @Override
    public float[][] getPositions() {
        return positions;
    }

    @Override
    public String toString() {
        return "CoViWoExtractorSURF";
    }

    @Override
    public Listened setUp(Object... args) throws Exception {
        return null;
    }

    @Override
    public Class[] getParameterTypes() {
        return new Class[]{};
    }

    @Override
    public Class[] getSetUpParameterTypes() {
        return new Class[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getParameterNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getSetUpParameterNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getDefaultParameterValues() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
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

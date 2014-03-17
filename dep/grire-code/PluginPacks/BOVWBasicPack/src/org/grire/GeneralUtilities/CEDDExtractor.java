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
 * Copyright (C) 2013 Lazaros Tsochatzidis <ltsochat at ee.duth.gr>
 */
package org.grire.GeneralUtilities;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.grire.GeneralUtilities.Interfaces.FeatureExtractor;
import org.grire.Helpers.Listeners.Listened;
import org.grire.Resources.CEDD;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Author: Lazaros Tsochatzidis
 */

@PluginImplementation
public class CEDDExtractor extends FeatureExtractor {

    CEDD cedd = new CEDD();

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
        cedd.extract(img);
        return new float[][] {Arrays.copyOf(cedd.data, cedd.data.length)};
    }

    @Override
    public float[][] getPositions() {
        return new float[0][];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String toString() {
        return "CEDDExtractor{}";
    }

    @Override
    public Listened setUp(Object... args) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class[] getParameterTypes() {
        return new Class[0];  //To change body of implemented methods use File | Settings | File Templates.
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

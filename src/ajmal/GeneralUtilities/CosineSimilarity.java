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

package ajmal.GeneralUtilities;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import ajmal.GeneralUtilities.Interfaces.SimilarityMeasure;
import ajmal.Helpers.Listeners.Listened;

/**
 *
 * @author Lazaros
 */
@PluginImplementation
public class CosineSimilarity extends SimilarityMeasure {
    public CosineSimilarity() {}

    public float calculate(float[] QueryVector, float[] Vector) {
        float ret=dotProduct(QueryVector, Vector)/(norm(Vector)*norm(QueryVector));
        return (Float.isNaN(ret) ? 0 : ret);
    }

    protected Float dotProduct(float[] a, float[] b) {
        Float ret=0f;
        for (int i=0;i<a.length;i++)
            ret+=a[i]*b[i];
        return ret;
    }
    
    protected Float norm(float[] a) {
        Float ret=0f;
        for (Float n: a) {
            ret+=n*n;
        }
        return (float) Math.sqrt(ret);
    }

    @Override
    public boolean smallerTheBetter() {
        return false;
    }

    @Override
    public String toString() {
        return "CosineSimilarity";
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

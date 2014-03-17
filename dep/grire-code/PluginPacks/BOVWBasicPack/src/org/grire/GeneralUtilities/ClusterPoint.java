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

package org.grire.GeneralUtilities;
import java.util.HashSet;
/**
 * A ClusterPoint represents a cluster and has the ability to include other
 * vectors that are classified to it. The ClusterPoint's location is the
 * center of the cluster.
 * @author Lazaros Tsochatzidis
 */
public class ClusterPoint{
    public int cluster;
    HashSet<Integer> members = new HashSet<Integer>();
    public float[] nextLocation,Location;
    public int membersNumber;

    public ClusterPoint(float[] loc) {
        this.nextLocation=new float[loc.length];
        Location=loc;
        membersNumber=0;
    }
    
    public float GetMovement() {
        float sum=0;
        for (int i=0;i<nextLocation.length;i++) {
            sum += (this.Location[i]-nextLocation[i])*(this.Location[i]-nextLocation[i]);
        }
        return sum;
    }
    
    @Override
    public String toString(){
        float ret=0;
        for (float d:this.Location) ret+=d;
        return String.valueOf(ret);
    }

    public float getEuclidianDistance(float[] p) {
        return (float) Math.sqrt(getSquaredEuclidianDistance(p));
    }
    
    public float getSquaredEuclidianDistance(float[] p) {
        float sum=0;
        for (int i=0;i<p.length;i++) {
            sum+=(p[i]-Location[i])*(p[i]-Location[i]);
        }
        return sum;
    }
    
    public void swapLocations() {
        Location=nextLocation;
    }
}

import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.grire.GeneralUtilities.Interfaces.ClusteringAlgorithm;
import org.grire.Helpers.Listeners.Listened;

import java.util.*;

/**
 *
 * @author Lazaros
 */
@PluginImplementation
public class CoViWoClusteringSGONG extends ClusteringAlgorithm {
    public int NumberOfEpochs=1000;
    public int ConsequentEpochsBeforeNeuronDelete=100;
    public int Nidle=150;                                       //Threshold of well-trained neuron
    
    public float e1min=0.005f;                                  //Max values for Learning Rates
    public float e1max=0.1f;
    public float rmax=400;
    
    public float m1=0.1f;                                       //Thresholds for adding or removing neuron
    public float m2=0.5f;
    
    public int MaximumAgeOfConnection=500;
    public int ConvergenceAcceleration=400;
    public int MaximumNumberOfNeurons=16;
    private int Features;
    
    private int CurrentTime=0;
    private boolean TerminationFlag = false;
    
    public float Idle=6000;
    
    private NeuronList neurons;
    public Set<float[]> InputPoints;
    public ArrayList<Point> Results;
    public int[] Classified;

    public CoViWoClusteringSGONG(Float numOfClasses, Float NumberOfEpochs, Float ConsequentEpochsBeforeNeuronDelete, Float Nidle,
                                 Float e1min, Float e1max, Float rmax, Float m1, Float m2, Float MaximumAgeOfConnection,
                                 Float ConvergenceAcceleration) {

        this.MaximumNumberOfNeurons=numOfClasses.intValue();
        this.NumberOfEpochs=NumberOfEpochs.intValue();
        this.ConsequentEpochsBeforeNeuronDelete=ConsequentEpochsBeforeNeuronDelete.intValue();
        this.Nidle=Nidle.intValue();
        this.e1min=e1min;
        this.e1max=e1max;
        this.m1=m1;
        this.m2=m2;
        this.MaximumAgeOfConnection=MaximumAgeOfConnection.intValue();
        this.ConvergenceAcceleration=ConvergenceAcceleration.intValue();
    }

    public CoViWoClusteringSGONG(Float numOfClasses){
        this.MaximumNumberOfNeurons=numOfClasses.intValue();
    }

    public CoViWoClusteringSGONG() {

    }

    public void RunSGONG () {
        this.Features=InputPoints.iterator().next().length;
        neurons = new NeuronList(this.e1max,this.rmax,this.MaximumNumberOfNeurons,this.Features);
        neurons.Initialize(InputPoints);
        inputIterator=InputPoints.iterator();
        for (int i=0;i<this.NumberOfEpochs;i++) {
            RunEpoch(i);
            neurons.endOfEpoch();
            if (TerminationFlag) {
                break;
            }
            this.progressMade(100 * i / NumberOfEpochs, 100);
        }
        neurons.endOfEpoch();
        int i,min=0;
        for (float[] pLoc : InputPoints){
            Point p=new Point(pLoc);
            for (i=0;i<neurons.list.size();i++){
                if (neurons.list.get(i).getDistance(p) > neurons.list.get(min).getDistance(p))
                    min=i;
                i++;
            }
            neurons.list.get(min).Nepoch++;
            neurons.list.get(min).classified.add(p);
        }
        for (int k=0;k<neurons.list.size();k++){
            if (neurons.list.get(k).Nepoch==0)
                neurons.Delete(k);
        }
        this.progressMade(100, 100);
        this.actionCompleted();
    }
    
    /*
     * Runs one epoch (as many itterations as defined) and applies the 3 criteria.
     */
    private void RunEpoch(int CurrentEpoch) {
        do{
        }while (RunIteration()==false);
        
        //STEP 7: Check the learning counter of each neuron to determine if everything is well-trained.
        if (neurons.FindMinCounter()>Nidle) { 
            this.TerminationFlag = true; return;
        }
        
        //STEP 8: Disconnect neurons with old connections.
        neurons.disconnectOlds(this.MaximumAgeOfConnection);
        
        if (CurrentEpoch<this.ConvergenceAcceleration) {

            /*
             * Criterio 1:
             * Neurons that have not been winners for too long (longer than ConsequentEpochsBeforeNeuronDelete)
             * are considered inactive and are abstracted.
             * Neighboring neurons are connected to the closest neuron of the currently deleted.
             */ 
            if (this.neurons.list.size() > 2){
                int[] inactive = neurons.FindInactiveNeurons(this.ConsequentEpochsBeforeNeuronDelete);
                neurons.Delete(inactive);
            }
            /*
             * Criterio 2:
             * A new neuron needs to be inserted between the neuron with the maximum AE2
             * (accumulated error if neuron did not exist) and his neighbor with the maximum AE2
             */
            if ( neurons.size() < this.MaximumNumberOfNeurons ) neurons.AddNear(neurons.FindMostLackingNeuron(m1));

            
            /*
             * Criterio 3:
             * The scope of this function is to remove the classes that
             * are closer to their neighboring classes, while the isolated
             * are retained.The neuron with the minimum average distance
             * is removed if this quantity is less than the threshold m2.
             */
            if (this.neurons.list.size() > 2){
            int minimum = neurons.FindMinimumAE2dN(m2);
                neurons.Delete(minimum);
            }
        }  
    }
    
    
    /*
     * Runs one iteration (one random data vector) and performs the 6 steps.
     */
    Iterator inputIterator=null;
    private boolean RunIteration(){
        
        this.CurrentTime++;
        //STEP 1: Choose next random vector
        Point current = new Point((float[])inputIterator.next());
        
        //STEP 2: Find the two winner neurons
        int[] winners = neurons.FindWinners(current);
        
        //STEP 3: Calculation of Accumulated Errors and Counters of winners
        Neuron w1,w2;
        w1 = neurons.get(winners[0]);
        w2 = neurons.get(winners[1]);
        
        w1.AE1 += w1.getDistance(current);
        w1.AE2 += w2.getDistance(current);
        w1.N++;
        w1.Nepoch++;
        
        //STEP 4: Calculate learing rates
        if (w1.N<=Nidle){
            w1.e2 = w1.e1/w1.r;
            w1.e1=e1max+e1min-e1min*(float)Math.pow(e1max/e1min, (float) w1.N/Nidle);
            w1.r = rmax+1-rmax*(float)Math.pow((float)1/rmax, (float) w1.N/Nidle);
        } else {
            w1.e1 = e1min;
            w1.e2 = 0;
            w1.r = 1;
        }
        
        //STEP 5: Moving winner and neighbors
        ArrayList<Neuron> nei = neurons.FindNeighbors(winners[0]);
        w1.move(current, w1.e1);
        for (Neuron n : nei) n.move(current, n.e2);
        //STEP 6: Reconnect winners (age=0) and advance one period for neighbors.
        w1.connect(winners[1]);
        w2.connect(winners[0]);
        for (Neuron n : nei) {
            n.advance(winners[0]);
            w1.advance(neurons.indexOf(n));
        }
        neurons.endOfItter();
        return inputIterator.hasNext();
    }
    
    public float[][] getRawData(){
        float[][] ret = new float[neurons.size()][Features];
        int i=0;
        for (Neuron n : neurons.list){
            System.arraycopy(n.getLocation(), 0, ret[i], 0, Features);
            i++;
        }
        return ret;
    }
    
    public LinkedList<Point> GetClassifiedToNeuron(int i) {
        return neurons.list.get(i).classified;
    }


    @Override
    public void run() {
        try {
            if (InputPoints==null) throw new Exception("Input not set!");
            this.RunSGONG();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setFeatures(Set features) {
        this.InputPoints=features;
    }

    public void setNumClusters(int numClusters) {
        this.MaximumNumberOfNeurons=numClusters;
    }

    @Override
    public float[][] getMedians() throws Exception {
        float[][] ret=new float[neurons.size()][];
        int i=0;
        for (Object o:neurons) {
            Neuron n=(Neuron)o;
            float[] location = n.getLocation();
            ret[i]=new float[location.length];
            for (int j=0;j<location.length;j++) {
                ret[i][j]=location[j];
            }
            i++;
        }
        return ret;
    }

    @Override
    public Listened setUp(Object... args) throws Exception {
        return null;
    }

    @Override
    public Class[] getParameterTypes() {
        return new Class[]{Float.class,Float.class,Float.class,Float.class,Float.class,Float.class,Float.class
        ,Float.class,Float.class,Float.class,Float.class};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class[] getSetUpParameterTypes() {
        return new Class[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{"Number of classes", "NumberOfEpochs","ConsequentEpochsBeforeNeuronDelete","Nidle",
                "e1min", "e1max","rmax", "m1", "m2", "MaximumAgeOfConnection","ConvergenceAcceleration"};
    }
    @Override
    public String[] getSetUpParameterNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getDefaultParameterValues() {
        return new String[]{"","1000","100","150","0.005","0.1","400","0.1","0.5","500","400"};  //To change body of implemented methods use File | Settings | File Templates.
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
class Neuron extends Point {
    /* 
     * AE1 = accumulated error of neuron
     * AE2 = error if neuron didn't exist
     * 
     * e1,e2,r =  learning rates for winner and winner's neighbor
     * 
     * N = number of vectors classified in epoch
     * 
     * cons = consequent epochs without winning
     */
    
    public float AE1,AE2,e1,e2,r;
    public int N,cons,maxCons,Nepoch,LastNepoch;
    public LinkedList<Point> classified;
    
    public ArrayList<Integer> connections;
    
    public Neuron(float loc[], float e1max, int CurrentNumberOfNeurons){
        super(loc);
        AE1=AE2=0;
        e2=e1max;
        e1=e1max;
        r=1;
        connections = new ArrayList<Integer>();
        classified = new LinkedList<Point>();
        for (int i=0;i<CurrentNumberOfNeurons;i++) connections.add(-1);
        cons=N=Nepoch=LastNepoch=0;
    }
    
    /*
     * Moves a neuron towards a data point according to the
     * learning rates.
     */
    public void move(Point ip, float e){
        float[] iploc = ip.getLocation();
        for (int i=0;i<this.getLocation().length;i++){
            this.Location[i]+= e*(iploc[i]-this.Location[i]);
        }
    }
    
    public void connect(int i){
        connections.set(i, 0);
    }
    
    public void disconnect(int i){
        connections.set(i, -1);
    }
    
    public void advance(int i){
        connections.set(i, connections.get(i)+1);
    }
    
    public void setWinner(){
        cons=0;
    }

    public void ResetForNewItter() {
    }

    public void disconnectOlds(int MaxAge) {
        for (int i=0;i<this.connections.size();i++){
            if (connections.get(i)>=MaxAge)  
                this.disconnect(i);
        }
    }

    public void ResetForNewEpoch() {
        LastNepoch=Nepoch;
        Nepoch=0;
        cons++;
        AE1=AE2=0;
    }

    public String toString(){
        /* String value = "";
        for (float f : this.Location){
            value += String.valueOf(f) + " ";
        }
        return value; */
        return String.valueOf(Nepoch);
    }
}

class NeuronList implements Iterable{
    private float e1max;
    private int features;
   public ArrayList<Neuron> list;
    public NeuronList(float e1max,float rmax,int max, int feat){
        this.e1max = e1max;
        this.features = feat;
        list = new ArrayList<Neuron>();
    }
    
    /*
     * Add two initial neurons with random locations
     */
    public void Initialize(Set points){
        Random gen = new Random();

        Iterator iterator = points.iterator();
        float[] loc = (float[]) iterator.next();
        float [] loc2 = (float[]) iterator.next();

        Neuron n1 = new Neuron(loc,e1max,2);
        Neuron n2 = new Neuron(loc2,e1max,2);

        n1.connect(1);
        n2.connect(0);
        list.add(n1); list.add(n2);
    }
    /*
     * Finds the two neurons that best classify the given data point.
     * 
     * In Array:
     * Index = 0 -> Winner 
     * Index = 1 -> Second Winner
     */
    public int[] FindWinners(Point ip){
        int i=0,w1,w2;
        float[] dists = new float[list.size()];
        for (Neuron n : list){
            dists[i]=n.getDistance(ip);
            i++;
        }
        w1 = MinOfArray(dists);
        dists[w1]=Float.MAX_VALUE;
        w2 = MinOfArray(dists);
        
        int[] ret = new int[2];
        ret[0]=w1;
        ret[1]=w2;
        list.get(w1).setWinner();
        return ret;
    }
    
    
    /*
     * Finds all neurons that are connected with the specified neuron
     */
    public ArrayList<Neuron> FindNeighbors(int n){
        Neuron neur = list.get(n);
        ArrayList<Neuron> ret = new ArrayList<>();
        int i=0;
        for (int age : neur.connections) {
            if (age>-1) ret.add(list.get(i));
            i++;
        }
        return ret;
    }
    
    public int FindMinCounter(){
        int min = Integer.MAX_VALUE;
        for (Neuron n : list){
            if(n.N<min) min=n.N;
        }
        return min;
    }
    
    public Neuron get(int i){
        return list.get(i);
    }
    
    public int indexOf(Neuron n){
        return list.indexOf(n);
    }
    
    public void endOfItter(){
        for (Neuron n : list) {
            n.ResetForNewItter();
        }
    }
    
    public void endOfEpoch() {
        for (Neuron n : list) {
            n.ResetForNewEpoch();
        }
    }
    
    public void disconnectOlds(int MaxAge){
        for (Neuron n : list) {
            n.disconnectOlds(MaxAge);
        }
    }
    
    
    /*
     * Deletion of a neuron includes deletion of all the connections to the neuron from other neurons
     * and the connection of everey neighboring neuron to the closest of it.
     */
    public void Delete(int[] array){
        for (int d : array) {
            int closest = this.FindClosestNeighbor(d);
            int i=0;
            for (Neuron n : list){
                if ((n.connections.get(d) > -1) && (i!=closest)) {
                    n.connect(closest);
                    list.get(closest).connect(i);
                }
                i++;
            }
            for (Neuron n : list) n.connections.remove(d);
            list.remove(d);
        }
    }
    
    public void Delete(int a){
        if (a>-1) {
            int[] aa = new int[1];
            aa[0]=a;
            this.Delete(aa);
        }
    }
    
    public int[] FindInactiveNeurons (int MaxCons){
        int i=0;
        ArrayList<Integer> tempList = new ArrayList<Integer>();
        for (Neuron n : list){
            if (n.cons>MaxCons) 
                tempList.add(i);
            i++;
        }
        int[] ret = new int[tempList.size()];
        int j=0;
        for (Integer r : tempList) {
            ret[j] = r;
            j++;
        }
        if (ret.length!=0) System.out.println("[1] Deleted Inactive: " + ret.length);
        return (int[]) ret;
    }
    
    public int FindClosestNeighbor(int d){
        ArrayList<Neuron> neurs = this.FindNeighbors(d);
        float[] dists = new float[neurs.size()];
        return MinOfArray(dists);
    }
    
    /*
     * Search the list for the most important neuron (biggest AE2) and checks if
     * it surpasses the threshold of neuron addition.
     * Returns:
     * -1 -> if there is no neuron beyond threshold.
     * neuron index -> if suitable neuron was found.
     */
    public int FindMostLackingNeuron(float m1){
        int MaxAE1=0,i=0;
        for (Neuron n : list){
            if (n.AE1>list.get(MaxAE1).AE1) MaxAE1 = i;
            i++;
        }
        float AE2 = list.get(MaxAE1).AE2;
        int N = list.get(MaxAE1).LastNepoch;
        if (AE2/N < m1*AverageDistance())  return -1;
        else return MaxAE1;
    }

    public int FindMinimumAE2dN(float m2) {
        int MinAE2=0,i=0;
        for (Neuron n : list) {
            if (n.AE2/n.LastNepoch < list.get(MinAE2).AE2/ list.get(MinAE2).LastNepoch) 
                MinAE2 = i;
            i++;
        }
        Neuron min = list.get(MinAE2);
        if (min.LastNepoch!=0) {
            //if (min.AE2/min.Nepoch<=m2*MaxAE2()) return MinAE2;
            System.out.println(min.AE2/min.LastNepoch + " "+m2*MaxAE2());
            if (min.AE2/min.LastNepoch <= m2*MaxAE2()) {
                System.out.println("[3] Deleted AE2");
                return MinAE2;
            }
            else return -1;
        }else{
            return -1;
        }
    }
    
    /*
     * Adds a neuron between the given neuron (first) and his neighbor with the biggest AE1 (NeiWithBiggestAE1)
     */
    public void AddNear(int n){
        if (n>-1) {
            Neuron first = list.get(n);
            ArrayList<Neuron> nei = this.FindNeighbors(n);
            Neuron NeiWithBiggestAE1 = nei.get(0);
            for (Neuron ne : nei) {
                if (ne.AE1>NeiWithBiggestAE1.AE1) 
                    NeiWithBiggestAE1=ne;
            }
            float[] loc = new float[features];
            for (int i=0;i<features;i++){
                loc[i] = ( first.getLocation()[i] + NeiWithBiggestAE1.getLocation()[i] )/2;
            }

            Neuron NewNeuron = new Neuron(loc,e1max,list.size()+1);
            for (Neuron ne : list){
                ne.connections.add(-1);
            }
            list.add(NewNeuron);

            int second = list.indexOf(NeiWithBiggestAE1);
            first.disconnect(second);
            NeiWithBiggestAE1.disconnect(n);

            first.connect(list.size()-1);
            NewNeuron.connect(n);

            NeiWithBiggestAE1.connect(list.size()-1);
            NewNeuron.connect(second);

            first.N=0;
            NeiWithBiggestAE1.N=0;
            System.out.println("[2] Added between "+n+" and "+second);
        }
    }
    
    public float AverageDistance(){
        float sum=0;
        for (int i=0;i<list.size()-1;i++){
            for (int j=i+1;j<list.size();j++){
                sum += list.get(i).getDistance( (Point) list.get(j));
            }
        }
        sum = 2.0f * sum / ( list.size() * (list.size()-1) );
        return sum;
    }
    
    public float MaxAE2(){
        int MaxAE2=0,i=0;
        float temp;
        for (Neuron n : list) {
            temp = n.AE2==0 ? 0 : n.AE2/n.Nepoch ;
            if (temp > list.get(MaxAE2).AE2/ list.get(MaxAE2).Nepoch) MaxAE2 = i;
            i++;
        }
        return list.get(MaxAE2).AE2/list.get(MaxAE2).Nepoch;
    }
    
    private int MinOfArray(float[] a){
        int i=0,min=0;
        for (float d : a){
            if (d<a[min]) min=i;
            i++;
        }
        return min;
    }

    public int size() {
        return list.size();
    }
    
    public boolean debug() {
        boolean flag = false;
        for (Neuron n : list){
            for (int i=0;i<n.connections.size();i++){
                
            }
            return flag;
        }
        return flag;
    }

    @Override
    public Iterator iterator() {
        return this.list.iterator();
    }
}

class Point {

    float[] Location;

    public Point(float[] loc) {
        Location=loc;
    }

    public float[] getLocation() {
        return Location;
    }

    public float getDistance(Point current) {
        float ret=0;
        float[] otherLoc=current.getLocation();
        for (int i=0;i<otherLoc.length;i++) {
            ret+=(Location[i]-otherLoc[i]) *  (Location[i]-otherLoc[i]);
        }
        return ret;
    }
}
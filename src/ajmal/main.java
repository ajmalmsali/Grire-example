package ajmal;

import ajmal.BagOfVisualWords.BOVWSystem;
import ajmal.BagOfVisualWords.CustomDescriptor;
import ajmal.BagOfVisualWords.EuclidianStemmer;
import ajmal.BagOfVisualWords.SMARTModel;
import ajmal.BagOfVisualWords.Functions.*;
import ajmal.BagOfVisualWords.Functions.QueryPerformer.setItem;
import ajmal.BagOfVisualWords.Interfaces.VisualWordDescriptor;
import ajmal.BagOfVisualWords.Interfaces.WeightingScheme;
import ajmal.BagOfVisualWords.Structures.Codebook;
import ajmal.BagOfVisualWords.Structures.ImagePool;
import ajmal.BagOfVisualWords.Structures.Index;
import ajmal.BagOfVisualWords.Structures.PoolFeatures;
import ajmal.GeneralUtilities.CosineSimilarity;
import ajmal.GeneralUtilities.EuclidianSimilarity;
import ajmal.GeneralUtilities.JOpenSURFParser;
import ajmal.GeneralUtilities.KMeans;
import ajmal.GeneralUtilities.GeneralStorers.GeneralMapDBStorer;
import ajmal.GeneralUtilities.GeneralStorers.GeneralStorer;
import ajmal.GeneralUtilities.Interfaces.ClusteringAlgorithm;
import ajmal.GeneralUtilities.Interfaces.FeatureExtractor;
import ajmal.GeneralUtilities.Interfaces.SimilarityMeasure;
import ajmal.Helpers.Listeners.Listened;
import ajmal.Helpers.Listeners.ProgressMadeListener;
import argo.format.JsonFormatter;
import argo.format.PrettyJsonFormatter;
import argo.jdom.JsonRootNode;
import static argo.jdom.JsonNodeFactories.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.TreeSet;

public class main {
	
	public static void main(String[] args) throws Exception {
		
		final JsonFormatter JSON_FORMATTER = new PrettyJsonFormatter();
		
		final long t1 = System.currentTimeMillis();
		
		String action		=args[0].toLowerCase();
		String imagePoolName=args[1].toLowerCase();
		String dbFolder		=args[2].toLowerCase();
		String location		=args[3];
	
		String threaded;
		Boolean multiThreaded;
		Float threshold;
		if(args.length==5)
			threshold=Float.parseFloat(args[4]);
		else
			threshold=1f;
		
		
		if(args.length==6)
		{
			threaded	=args[5].toLowerCase();
			
			if(threaded.contentEquals("multi"))
			{
				multiThreaded=true;
			}
			else
				multiThreaded=false;
		}
		else
			multiThreaded=false;
		
		Boolean newPool=true;
		Boolean newColl=true;
		if(action.contentEquals("create"))
		{
			newPool=true;
			newColl=true;
		}
		else if(action.contentEquals("search"))
		{
			newPool=false;
			newColl=false;
		}
		else if(action.contentEquals("merge"))
		{
			newPool=false;
			newColl=true;
		}
		
		
		String[] imagesFolder=new String[] {location};
		String[] queryImageFile=new String[] {location};
		
		GeneralStorer storer=new GeneralMapDBStorer(dbFolder+imagePoolName);
		 
		//Create a new image pool and import the images from two folders.
		ImagePool imagePool;
		 
		imagePool = new ImagePool(storer,imagePoolName,newPool);
		 
		 if(newColl==true){
			 Importer importer=new Importer(
			 imagesFolder,
			 imagePool);
			 importer.run();
		 }
		 
		 //Extract the features of the pool using
		 //the SURF local feature extractor and descriptor.
		 PoolFeatures poolFeatures=new PoolFeatures(storer,imagePoolName+"_SURF",newPool);
		 FeatureExtractor extractor=new JOpenSURFParser();
		 
		 if(newColl==true){
			 
		PoolFeatureExtractor pfe=new PoolFeatureExtractor(imagePool,
		 extractor,
		 poolFeatures,
		 multiThreaded);
		 
		 //Add a listener that prints the progress at console.
		 //The same listener can be used to all the Function classes of GRire
		 //that implements the Listened class.
		 
		 pfe.addProgressListener(new ProgressMadeListener() {
		 @Override
		 public void OnProgressMade(int progress, int total, String message) {
		 System.out.println(message);
		 }
		 });
		 pfe.run();
		 
		 }
		 
		//Create a codebook using KMeans with 40% of the set as training set
		 //and producing 400 classes.
		 		 
		 Codebook cb=new Codebook(storer,imagePoolName+"_400",newColl);
		 
		 if(newColl==true)
		 {	 
			 ClusteringAlgorithm kmeans=new KMeans(400f,threshold);
			 		 
			 ClusteringCodebookFactory ccf=new ClusteringCodebookFactory(
			 poolFeatures,
			 kmeans,
			 cb);
			 ccf.setPercentageOfData(0.4f);
			 ccf.run();		 
		 }
		 
		//Create the Index of the database using a Custom Descriptor
		 //with SURF features(of course) and Euclidian Stemmer.
		 		 
		 Index in=new Index(storer,imagePoolName+"_SURF_400",newPool);
		 VisualWordDescriptor desc=new CustomDescriptor(
		 extractor,
		 new EuclidianStemmer(),
		 cb);
		 
		 
		 if(newColl==true)
		 {
	 
			 IndexFactory indexFactory=new IndexFactory(poolFeatures,
			 desc,
			 in,
			 multiThreaded);
			 indexFactory.run();
		 
		 }
		 
		 final long t6a = System.currentTimeMillis();
		//Create a complete BOVW System with these components.
		 BOVWSystem system=new BOVWSystem(imagePool,
		 desc,
		 in);
		 
		 
		 if(newPool==false&&newColl==false)
		 {
		//Initialize a Query Performer over the system.
		 //Use the weighting scheme smart with notation ntc
		 //for query and indexed vector.
		 //and Cosine similarity to compare the vectors.
		 WeightingScheme scheme=new SMARTModel("ntc:ntc");
		 SimilarityMeasure sim=new CosineSimilarity();
		 QueryPerformer qp=new QueryPerformer(system,scheme,sim);
		 
		 
		 
		//Make a simple query to the database.
		 TreeSet<QueryPerformer.setItem> results = qp.NewQuery(new File(queryImageFile[0]),1);
		 
		 String filename;
		 long itemid=results.first().id;
		 //System.out.println(results.first().score);
		 double score =results.first().score;
		 
		 
		 filename=new File(system.getCollection().getImage(itemid)).getName();
		 
		 		
		 //TreeSet<QueryPerformer.setItem> results2 = qp.NewQuery(new File("/home/ajmalmsali/ajmal-img/FWD_feb-page3.png"),1);
		 //System.out.println("Res"+results2.toString());
		//Make a multiple query from file and output a TREC file
		 //with 100 images per query.
		 //This function returns a Listened object so it can run asynchronously
		 
		 
		 //final long t3 = System.currentTimeMillis();
		 
		//System.out.println("[INFO] Feature extraction took:	" + ((t2 - t1) / 1000.0) + " secs");
		 final long f1 = System.currentTimeMillis();
		 
		 final double time=((f1 - t1) / 1000.0);
		 
		 String scoreS=String.valueOf(score);
		 String timeS=String.valueOf(time);
		 
		 //System.out.println("Result for Query on file:"+queryImageFile[0]);
		 //System.out.println(filename+" score:"+score);
		 JsonRootNode json = object(
			        field("id", string(filename)),
			        field("score", string(scoreS)),
			        field("time", number(timeS)),
			        field("query",string(queryImageFile[0]) )
			);
		 
		 String jsonText = JSON_FORMATTER.format(json);
		 
		 System.out.println(jsonText);
		 //System.out.println(" :: "+results.first().score);
		 }
		 else
		 {
			 final long f1 = System.currentTimeMillis();
			 System.out.println("[INFO] FullService took:	" + ((f1 - t1) / 1000.0) + " secs");
		 }
	}

}

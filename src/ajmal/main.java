package ajmal;

import ajmal.BagOfVisualWords.BOVWSystem;
import ajmal.BagOfVisualWords.Functions.*;
import ajmal.BagOfVisualWords.Interfaces.VisualWordDescriptor;
import ajmal.BagOfVisualWords.Interfaces.WeightingScheme;
import ajmal.BagOfVisualWords.Structures.Codebook;
import ajmal.BagOfVisualWords.Structures.ImagePool;
import ajmal.BagOfVisualWords.Structures.Index;
import ajmal.BagOfVisualWords.Structures.PoolFeatures;
import ajmal.GeneralUtilities.GeneralStorers.GeneralMapDBStorer;
import ajmal.GeneralUtilities.GeneralStorers.GeneralStorer;
import ajmal.GeneralUtilities.Interfaces.ClusteringAlgorithm;
import ajmal.GeneralUtilities.Interfaces.FeatureExtractor;
import ajmal.GeneralUtilities.Interfaces.SimilarityMeasure;
import ajmal.Helpers.Listeners.Listened;
import ajmal.Helpers.Listeners.ProgressMadeListener;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.TreeSet;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Ajmal M Sali"+args.length+args[0]+args[1]+args[2]);
		GeneralStorer storer=new GeneralMapDBStorer("/home/ajmalmsali/ajmal");
		 
		//Create a new image pool and import the images from two folders.
		 ImagePool imagePool;
		 imagePool = new ImagePool(storer,"UKBench",true);
		 Importer importer=new Importer(
		 new String[]{
		 "c:/folder_with_images1",
		 "c:/folder_with_images2"},
		 imagePool);
		 importer.run();
		 
		//Extract the features of the pool using
		 //the SURF local feature extractor and descriptor.
		 PoolFeatures poolFeatures=new PoolFeatures(storer,"UKBench_SURF",true);
		 FeatureExtractor extractor=new JOpenSURFParser();
		 PoolFeatureExtractor pfe=new PoolFeatureExtractor(imagePool,
		 extractor,
		 poolFeatures);
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
		 
		//Create a codebook using KMeans with 40% of the set as training set
		 //and producing 400 classes.
		 Codebook cb=new Codebook(storer,"SURF_400",true);
		 ClusteringAlgorithm kmeans=new KMeans(400d);
		 ClusteringCodebookFactory ccf=new ClusteringCodebookFactory(
		 poolFeatures,
		 kmeans,
		 cb);
		 ccf.setPercentageOfData(0.4f);
		 ccf.run();
		 
		//Create the Index of the database using a Custom Descriptor
		 //with SURF features(of course) and Euclidian Stemmer.
		 Index in=new Index(storer,"UKBench_SURF_400",true);
		 VisualWordDescriptor desc=new CustomDescriptor(
		 extractor,
		 new EuclidianStemmer(),
		 cb);
		 IndexFactory indexFactory=new IndexFactory(poolFeatures,
		 desc,
		 in);
		 indexFactory.run();
		 
		//Create a complete BOVW System with these components.
		 BOVWSystem system=new BOVWSystem(imagePool,
		 desc,
		 in);
		 
		//Initialize a Query Performer over the system.
		 //Use the weighting scheme smart with notation ntc
		 //for query and indexed vector.
		 //and Cosine similarity to compare the vectors.
		 WeightingScheme scheme=new SMARTModel("ntc:ntc");
		 SimilarityMeasure sim=new CosineSimilarity();
		 QueryPerformer qp=new QueryPerformer(system,scheme,sim);
		 
		//Make a simple query to the database.
		 TreeSet<QueryPerformer.setItem> results = qp.NewQuery(new File("c:/queryimage.jpg"));
		 
		//Make a multiple query from file and output a TREC file
		 //with 100 images per query.
		 //This function returns a Listened object so it can run asynchronously
		 Listened listened = qp.NewTRECQuery("TestExperiment",
		 new FileInputStream("c:/queries.txt"),
		 new FileOutputStream("c:/trecreults.txt"),
		 100);
		 listened.run();
	}

}

import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.misc.InputMappedClassifier;
import weka.classifiers.trees.J48;
import weka.core.*;
import weka.core.converters.*;
import weka.core.stopwords.MultiStopwords;
import weka.core.stopwords.Rainbow;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.instance.Resample;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.*;
import java.util.*;

/**
 * This class builds a model for the given input data.
 * 
 * This code uses the features and algorithm from the WEKA Library
 *  
 * The weka.jar is a part of Weka package downloaded from: http://www.cs.waikato.ac.nz/ml/weka/downloading.html
 * 
 * @author Dipesh Nainani
 */

public class DocumentClassificationModel {

	public static void main(String[] args) throws Exception {

		String dirname = args[0];
		CleanFiles.cleanData(dirname);
		TextDirectoryLoader loader = new TextDirectoryLoader();
		loader.setDirectory(new File(
				System.getProperty("user.dir")+"/Cleaned_Data"));
		Instances rawData = loader.getDataSet();
		
		StringToWordVector filter = new StringToWordVector();
		Rainbow r = new Rainbow(); // Built-in stop word list
		System.out.println(rawData.numAttributes());

	
		MultiStopwords m = new MultiStopwords();
		filter.setStopwordsHandler(r);
		filter.setOutputWordCounts(true);
		filter.setTFTransform(true);
		filter.setIDFTransform(true);
		filter.setMinTermFreq(20);
		filter.setLowerCaseTokens(true);
		
		filter.setInputFormat(rawData);
		System.out.println(Arrays.toString(filter.getOptions()));
		Instances convertedData = Filter.useFilter(rawData, filter);

		System.out.println(convertedData.numAttributes());
		
		// This block samples the data to training and test sets in the ratio 60:40
		
		Resample sample_test = new Resample();
		sample_test.setSampleSizePercent(60);
		sample_test.setNoReplacement(true);
		sample_test.setInputFormat(convertedData);
		sample_test.setInvertSelection(true);
		Instances test = Filter.useFilter(convertedData, sample_test);

		
		Resample sample_train = new Resample();
		sample_train.setSampleSizePercent(60);
		sample_train.setNoReplacement(true);
		sample_train.setInputFormat(convertedData);
		Instances train = Filter.useFilter(convertedData, sample_train);		
		System.out.println(train.numAttributes());
		
		AttributeSelection selector = new AttributeSelection();
		InfoGainAttributeEval evaluator = new InfoGainAttributeEval();
		Ranker ranker = new Ranker();
		ranker.setNumToSelect(0);
		selector.setEvaluator(evaluator);
		selector.setSearch(ranker);
		selector.setInputFormat(train);
		
		System.out.println(train.numAttributes());
		NaiveBayes model = new NaiveBayes();	
		model.buildClassifier(train);
//
//		Classifier ibk = new IBk(3);
//		ibk.buildClassifier(train);
//		
		// Builds a support vector machine classifier on training data
//		Classifier svm = new LibSVM();
//		svm.buildClassifier(train);
		
		weka.core.SerializationHelper.write("1.model", model);
//		Classifier cls = (Classifier) weka.core.SerializationHelper.read("1.model");
		
//		Classifier j48 = new J48();
//		j48.buildClassifier(train);

		Evaluation eval = new Evaluation(train);
		eval.evaluateModel(model, test);
		System.out.println(eval.toSummaryString());
		System.out.println(eval.toMatrixString());
		System.out.println(eval.toClassDetailsString());

	}

}
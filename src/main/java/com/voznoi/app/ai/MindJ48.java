package com.voznoi.app.ai;

import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.neighboursearch.NearestNeighbourSearch;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MindJ48 {

    private J48 classifier;
    private DataSource dataSource;
    private Instance firstInstance;

    public void init(String filePath) throws Exception {
        File file = new File(filePath);
        ArffLoader arffLoader = new ArffLoader();
        arffLoader.setFile(file);
        dataSource = new DataSource(arffLoader);
        Instances dataSet = dataSource.getDataSet();
        dataSet.setClassIndex(dataSet.numAttributes() - 1);
        firstInstance = dataSet.firstInstance();

        classifier = new J48();
        classifier.buildClassifier(dataSet);

        System.out.println("Classifier is built");
    }

    public int getResult(int[] row) throws Exception {
        Instance instance = (Instance) firstInstance.copy();
        for (int i = 0; i < row.length; i++) {
            instance.setValue(i, String.valueOf(row[i]));
        }
        return (int) classifier.classifyInstance(instance);
    }

}

package com.voznoi.app.ai;

import weka.classifiers.lazy.IBk;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CheckResult {
    public void init() throws Exception{
        File file = new File("./data/gameResult.aarf");
        ArffLoader arffLoader = new ArffLoader();
        arffLoader.setFile(file);
        DataSource dataSource = new DataSource(arffLoader);
        Instances dataSet = dataSource.getDataSet();
        dataSet.setClassIndex(dataSet.numAttributes()-1);

        IBk classifier;// = new IBk(3);
        classifier = new IBk();
        classifier.buildClassifier(dataSet);


        System.out.println(classifier);

    }

}

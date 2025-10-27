package com.voznoi.app.ai;

import weka.classifiers.lazy.IBk;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.neighboursearch.LinearNNSearch;
import weka.core.neighboursearch.NearestNeighbourSearch;
import weka.core.pmml.jaxbbindings.NearestNeighborModel;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.DoubleToIntFunction;
import java.util.stream.Collectors;

public class CheckResult {

    private IBk classifier;// = new IBk(3);
    private DataSource dataSource;
    private Instance firstInstance;

    public void init() throws Exception {
        File file = new File("./data/gameResult.aarf");
        ArffLoader arffLoader = new ArffLoader();
        arffLoader.setFile(file);
        dataSource = new DataSource(arffLoader);
        Instances dataSet = dataSource.getDataSet();
        dataSet.setClassIndex(dataSet.numAttributes() - 1);
        firstInstance = dataSet.firstInstance();

        classifier = new IBk();
        classifier.buildClassifier(dataSet);

        System.out.println("Classifier is built");
    }

    public List<Result> getResult(int[] row,int desiredCell, int count) throws Exception {
        Instance instance = (Instance) firstInstance.copy();
        for (int i = 0; i < row.length; i++) {
            instance.setValue(i, String.valueOf(row[i]));
        }
        //double value = classifier.classifyInstance(instance);
        NearestNeighbourSearch algorithm = classifier.getNearestNeighbourSearchAlgorithm();
        Instances instances = algorithm.kNearestNeighbours(instance, count);
        List<Result> resultList = new ArrayList<>();
        for (int i = 0; i < instances.size(); i++) {
            Instance instance1 = instances.get(i);
            int[] row1 = Arrays.stream(instance1.toDoubleArray()).mapToInt(d -> (int) d).toArray();
            double distance = algorithm.getDistances()[i];
            Result result = new Result(distance, row1, instance1, desiredCell);
            resultList.add(result);
        }

        return resultList;
    }

    public List<Result> getResult(int[] row,int desiredCell, int count, int desiredResult) throws Exception {
        int[] rowCopy = new int[10];
        Arrays.copyOf(row, 9);
        rowCopy[9] = desiredResult;
        return getResult(row,desiredCell, count);
    }

    public List<Result> getPossibleResults(int[] board, int xo, int count) throws Exception {
        List<Result> resultList = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            if (board[i] == 0) {
                int[] boardCopy = Arrays.copyOf(board, 9);
                boardCopy[i] = xo;
                List<Result> results = getResult(boardCopy,i, xo, count);
                for (Result result : results) {
                    if (result.getResult() == xo) {
                        resultList.add(result);
                    }
                }
            }
        }
        return resultList;
    }

    public List<Result> getPossibleTopResults(int[] board, int xo, int count) throws Exception {
        List<Result> possibleResults = getPossibleResults(board, xo, count);
        List<Result> sorted = possibleResults.stream().sorted(Comparator.comparingDouble(Result::getDistance)).collect(Collectors.toList());
        List<Result> topList = new ArrayList<>();
        if (sorted.size() > 0) {
            double lowest = sorted.get(0).getDistance();
            for (Result result : sorted) {
                if (result.getDistance() == lowest) {
                    topList.add(result);
                } else {
                    break;
                }
            }
        }
        return topList;
    }


}

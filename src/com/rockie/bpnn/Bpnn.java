package com.rockie.bpnn;

import java.io.*;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Bpnn {

    private double[] mInput;
    private double[] mHidden;
    private double[] mOutput;
    private double[] mTarget;
    private double[] mHidDelta;
    private double[] mOutDelta;

    private double[][] mInputHidWeights;
    private double[][] mHidOutputWeights;
    private double[][] mInputHidPreWeights;
    private double[][] mHidOutputPreWeights;

    private double mLearnRate;
    private double mMomentum;

    private Random mRandom;
    private CyclicBarrier mBarrier;
    private ExecutorService mExecutorService;

    private int mThreadInt;

    private static class ForwardCalculateThread implements Runnable {
        private CyclicBarrier barrier;
        private int from, to;
        private Bpnn bp;
        private double[] layer0, layer1;
        private double[][] weights;

        public ForwardCalculateThread(CyclicBarrier barrier, Bpnn bp, int from, int to,
                                      double[] layer0, double[] layer1, double[][] weights) {
            this.barrier = barrier;
            this.bp = bp;
            this.from = from;
            this.to = to;
            this.layer0 = layer0;
            this.layer1 = layer1;
            this.weights = weights;
        }

        @Override
        public void run() {
            layer0[0] = 1.0;
            for (int j = from; j < to; j++) {
                double sum = 0;
                for (int i = 0; i < layer0.length; i++) {
                    sum += weights[i][j] * layer0[i];
                }
                if (Double.isNaN(sum) || Double.isInfinite(sum)) {
                    layer1[j] = 1.0;
                } else {
                    layer1[j] = bp.activeFuction(sum);
                }
            }

            if (barrier == null) {
                return;
            }
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    public Bpnn(int inputSize, int hiddenSize, int outputSize) {
        this(inputSize, hiddenSize, outputSize, 0.25, 0.9);
    }

    public Bpnn(int inputSize, int hiddenSize, int outputSize, double learnRate, double momentum) {
        mThreadInt = 8;
        mBarrier = new CyclicBarrier(mThreadInt);
        mExecutorService = Executors.newFixedThreadPool(mThreadInt - 1);

        mInput = new double[inputSize + 1];
        mHidden = new double[hiddenSize + 1];
        mOutput = new double[outputSize + 1];
        mTarget = new double[outputSize + 1];

        mHidDelta = new double[hiddenSize + 1];
        mOutDelta = new double[outputSize + 1];

        mInputHidWeights = new double[inputSize + 1][hiddenSize + 1];
        mHidOutputWeights = new double[hiddenSize + 1][outputSize + 1];
        mInputHidPreWeights = new double[inputSize + 1][hiddenSize + 1];
        mHidOutputPreWeights = new double[hiddenSize + 1][outputSize + 1];

        mLearnRate = learnRate;
        mMomentum = momentum;

        mRandom = new Random(19850224);
        randomizeWeights(mInputHidWeights);
        randomizeWeights(mHidOutputWeights);
    }

    public void train(double[] trainData, double[]target) {
        loadArray(mInput, trainData);
        loadArray(mTarget, target);
        forward();
        adjustDelta();
        adjustWeight();
    }

    public double[] test(double[] input) {
        loadArray(mInput, input);
        forward();
        return getNetworkOutput();
    }

    public void saveToFile(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file);
        writeInt(mInput.length, fos);
        fos.write(',');
        writeInt(mHidden.length, fos);
        fos.write(',');
        writeInt(mOutput.length, fos);
        fos.write(',');
        writeDouble(mLearnRate, fos);
        fos.write(',');
        writeDouble(mMomentum, fos);
        fos.write("\n".getBytes());
        int inputLen = mInput.length;
        int hiddenLen = mHidden.length;
        int outLen = mOutput.length;

        for (int i = 0; i < inputLen; i++) {
            for (int j = 0; j < hiddenLen; j++) {
                writeDouble(mInputHidWeights[i][j], fos);
                fos.write(',');
            }
            fos.write("\n".getBytes());
        }
        for (int i = 0; i < hiddenLen; i++) {
            for (int j = 0; j < outLen; j++) {
                writeDouble(mHidOutputWeights[i][j], fos);
                fos.write(',');
            }
            fos.write("\n".getBytes());
        }
        for (int i = 0; i < inputLen; i++) {
            for (int j = 0; j < hiddenLen; j++) {
                writeDouble(mInputHidPreWeights[i][j], fos);
                fos.write(',');
            }
            fos.write("\n".getBytes());
        }
        for (int i = 0; i < hiddenLen; i++) {
            for (int j = 0; j < outLen; j++) {
                writeDouble(mHidOutputPreWeights[i][j], fos);
                fos.write(',');
            }
            fos.write("\n".getBytes());
        }
    }

    public void loadFromFile(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        int parsePhase = 0;
        int i=0, j=0,k=0;
        int inputSize=0, hiddenSize=0, outputSize=0;
        while (true) {
            String temp = br.readLine();
            if (temp == null) {
                break;
            }

            if (parsePhase == 0) {
                String[] params = temp.split(",");
                inputSize = Integer.parseInt(params[0]) ;
                hiddenSize = Integer.parseInt(params[1] ) ;
                outputSize = Integer.parseInt(params[2]) ;
                mLearnRate = Double.parseDouble(params[3]);
                mMomentum = Double.parseDouble(params[4]);
                mInput = new double[inputSize];
                mHidden = new double[hiddenSize];
                mOutput = new double[outputSize];
                mTarget = new double[outputSize];

                mHidDelta = new double[hiddenSize];
                mOutDelta = new double[outputSize];

                mInputHidWeights = new double[inputSize][hiddenSize];
                mHidOutputWeights = new double[hiddenSize][outputSize];

                mInputHidPreWeights = new double[inputSize][hiddenSize];
                mHidOutputPreWeights = new double[hiddenSize][outputSize];
                parsePhase++;
                i = 0;
                j = 0;
                k = 0;
            } else if (parsePhase == 1) {
                String[] params = temp.split(",");
                for (j =0 ; j < hiddenSize; j++) {
                    mInputHidWeights[i][j] = Double.parseDouble(params[j]);
                }
                i++;
                if (i == inputSize) {
                    parsePhase++;
                    i = 0;
                }
            } else if(parsePhase == 2) {
                //parse hidOptWeights
                String[] params = temp.split(",");
                for (j =0; j < outputSize; j++) {
                    mHidOutputWeights[i][j] = Double.parseDouble(params[j]);
                }
                i++;
                if (i == hiddenSize) {
                    parsePhase++;
                    i = 0;
                }
            } else if(parsePhase == 3) {
                //parse iptHidPrevUptWeights
                String[] params = temp.split(",");
                for (j =0; j < hiddenSize; j++) {
                    mInputHidPreWeights[i][j] = Double.parseDouble(params[j]);
                }
                i++;
                if (i == inputSize) {
                    parsePhase++;
                    i = 0;
                }
            } else if(parsePhase == 4) {
                //parse hidOptPrevUptWeights
                String[] params = temp.split(",");
                for (j =0; j < outputSize; j++) {
                    mHidOutputPreWeights[i][j] = Double.parseDouble(params[j]);
                }
                i++;
                if (i == hiddenSize) {
                    parsePhase++;
                    i = 0;
                }
            }
        }
    }

    private void randomizeWeights(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                double real = mRandom.nextDouble();
                matrix[i][j] = mRandom.nextDouble() > 0.5 ? real : -real;
            }
        }
    }

    private void loadArray(double[] dest, double[] src) {
        if (src.length != dest.length - 1) {
            throw new IllegalArgumentException("Size Do Not Match!!");
        }
        System.arraycopy(src, 0, dest, 1, src.length);
    }

    private void forward() {
        forward(mInput, mHidden, mInputHidWeights);
        forward(mHidden, mOutput, mHidOutputWeights);
    }

    private void forward(double[] layer0, double[] layer1, double[][] weights) {
        int radius = (layer1.length - 1) / (mThreadInt);
        int from = 1;

        for (int i = 0; i < mThreadInt - 1; i++) {
            ForwardCalculateThread t = new ForwardCalculateThread(mBarrier, this,
                    from, from + radius, layer0, layer1, weights);
            from += radius;
            mExecutorService.execute(t);
        }
        ForwardCalculateThread t = new ForwardCalculateThread(mBarrier, this, from, layer1.length,
                layer0, layer1, weights);
        t.run();
    }

    private void adjustOutputDelta() {
        for (int i = 1; i < mOutDelta.length; i++) {
            double o = mOutput[i];
            mOutDelta[i] = o * (1d - o) * (mTarget[i] - o);
        }
    }

    private void adjustHiddenDelta() {
        for (int j = 1; j < mHidDelta.length; j++) {
            double o = mHidden[j];
            double sum = 0;
            for (int k = 1;  k < mOutDelta.length; k++) {
                sum += mHidOutputWeights[j][k] * mOutDelta[k];
            }
            mHidDelta[j] = o * (1d - o) * sum;
        }
    }

    private void adjustDelta() {
        adjustOutputDelta();
        adjustHiddenDelta();
    }

    private void adjustWeight(double[] delta, double[] layer, double[][] weight, double[][] preWeight) {
        layer[0] = 1;
/*
        for (int i = 1; i < delta.length; i++) {
            for (int j = 0; j < layer.length; j++) {
                double newVal = mMomentum * preWeight[j][i] + mLearnRate * delta[i] * layer[j];
                weight[j][i] += newVal;
                preWeight[j][i] += newVal;
            }
        }*/

        for (int i = 1; i < layer.length; i++) {
            for (int j = 0; j < delta.length; j++) {
                double change = delta[j] * layer[i];
                weight[i][j] += mLearnRate * change + mMomentum * preWeight[i][j];
                preWeight[i][j] = change;
            }
        }
    }

    private void adjustWeight() {
        adjustWeight(mOutDelta, mHidden, mHidOutputWeights, mHidOutputPreWeights);
        adjustWeight(mHidDelta, mInput, mInputHidWeights, mInputHidPreWeights);
    }

    private double activeFuction(double x) {
        return sigmoid(x);
    }

    private double sigmoid(double x) {
        return 1d / (1d + Math.exp(-x));
    }

    private double[] getNetworkOutput() {
        int len = mOutput.length;
        double[] temp = new double[len - 1];
        for (int i = 1; i != len; i++)
            temp[i - 1] = mOutput[i];
        return temp;
    }

    private void writeDouble(double eta2, OutputStream os) throws IOException {
        String d = String.format("%f", eta2);
        os.write(d.getBytes());
    }

    private void writeInt(int i, OutputStream os) throws IOException {
        String str = String.format("%d", i);
        os.write(str.getBytes());
    }
}

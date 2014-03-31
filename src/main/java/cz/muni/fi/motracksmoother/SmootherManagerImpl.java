package cz.muni.fi.motracksmoother;

import cz.muni.fi.motracksmoother.misc.JointType;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Implementation of SmootherManager interface.
 * 
 * @author Tomas Smetanka
 * @version 1.0
 * @since 1.0
 */
public class SmootherManagerImpl implements SmootherManager {

    private double priStateEstimate;                    // x^-
    private double aMatrix;                             // A
    private double bMatrix;                             // B
    private double hMatrix;                             // H
    private double predictedMeasurement;                // x^
    private double actualMeasurement;                   // z
    private double controlInput;                        // u
    private double priEstErrCovar;                      // P-
    private double postEstErrCovar;                     // P
    private double measurementNoiseCovar;               // R
    private double processNoiseCovar;                   // Q
    private double kalmanGain;                          // K

    public void setInputs(double measurementNoiseCovariance) {

        aMatrix = 1;
        bMatrix = 1;
        hMatrix = 1;
        predictedMeasurement = 0;
        controlInput = 0;
        priEstErrCovar = 1;
        postEstErrCovar = 1;
        measurementNoiseCovar = measurementNoiseCovariance;
        processNoiseCovar = 0.0001;

    }

    public void setFirstPredictedMeasurement(double firstPosition) {

        predictedMeasurement = firstPosition;

    }

    public void predict() {

        priStateEstimate = (aMatrix * predictedMeasurement) + (bMatrix * controlInput);

        priEstErrCovar = (aMatrix * postEstErrCovar * transpose(aMatrix)) + processNoiseCovar;

    }

    public double correct(double z) {

        predict();

        actualMeasurement = z;

        kalmanGain = (priEstErrCovar * transpose(hMatrix)) / ((hMatrix * priEstErrCovar * transpose(hMatrix)) + measurementNoiseCovar);

        predictedMeasurement = priStateEstimate + (kalmanGain * (actualMeasurement - (hMatrix * priStateEstimate)));

        postEstErrCovar = (1 - (kalmanGain * hMatrix)) * priEstErrCovar;

        return predictedMeasurement;
    }

    public double transpose(double matrix) {

        return matrix;

    }

    public void kalman(Skeleton skeleton, double measurementNoiseCovariance) {


        for (int i = 0; i < 3; i++) {

            TreeMap<JointType, ArrayList<Float>> tempSkeletonPositions = new TreeMap<JointType, ArrayList<Float>>();
            TreeMap<JointType, ArrayList<Float>> tempSkeletonCleanedPositions = new TreeMap<JointType, ArrayList<Float>>();

            switch (i) {
                case 0:
                    tempSkeletonCleanedPositions.putAll(skeleton.getxPositions());
                    break;
                case 1:
                    tempSkeletonCleanedPositions.putAll(skeleton.getyPositions());
                    break;
                case 2:
                    tempSkeletonCleanedPositions.putAll(skeleton.getzPositions());
                    break;
            }

            for (Map.Entry entry : tempSkeletonCleanedPositions.entrySet()) {

                setInputs(measurementNoiseCovariance);

                ArrayList<Float> tempAxisPositions = new ArrayList<Float>();

                boolean isFirst = true;

                for (Float pos : tempSkeletonCleanedPositions.get((JointType) entry.getKey())) {

                    if (isFirst) {
                        setFirstPredictedMeasurement(pos);
                        isFirst = false;
                    }

                    pos = (float) correct(pos);

                    tempAxisPositions.add(pos);
                }

                tempSkeletonPositions.put((JointType) entry.getKey(), tempAxisPositions);

            }

            switch (i) {
                case 0:
                    skeleton.setxPositionsCleaned(tempSkeletonPositions);
                    break;
                case 1:
                    skeleton.setyPositionsCleaned(tempSkeletonPositions);
                    break;
                case 2:
                    skeleton.setzPositionsCleaned(tempSkeletonPositions);
                    break;
            }

        }

    }
}

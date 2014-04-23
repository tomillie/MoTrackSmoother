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

    private double priStateEstimate;                    // \hat{x}^{-}
    private double aMatrix;                             // A
    private double bMatrix;                             // B
    private double hMatrix;                             // H
    private double predictedMeasurement;                // \hat{x}
    private double actualMeasurement;                   // z
    private double controlInput;                        // u
    private double priEstErrCovar;                      // P^{-}
    private double postEstErrCovar;                     // P
    private double measurementNoiseCovar;               // R
    private double processNoiseCovar;                   // Q
    private double kalmanGain;                          // K

    public void setInputs(double measurementNoiseCovariance) {
        
        // following matrices are set to be 1, 
        // because we implement one dimensional Kalman filter,
        // which does not operate with matrices
        aMatrix = 1;
        bMatrix = 1;
        hMatrix = 1;
        
        // no control input is needed
        controlInput = 0;
        
        // if we would have set P to 0, that would mean there was no noise
        // and all following \hat{x} would be 0
        // therefore we can use any value different from 0
        postEstErrCovar = 1;
        
        // the value is taken from application, set by user
        measurementNoiseCovar = measurementNoiseCovariance;
        
        // some very small number, close to 0
        processNoiseCovar = 0.0001;

    }

    public void setFirstPredictedMeasurement(double firstPosition) {

        // sets initial value of first position to avoid initial noiseness
        // we can do so, because we know the values befor processing
        predictedMeasurement = firstPosition;

    }

    public void predict() {

        // first part of \hat{x}^{-}, the prediction
        // taken from last computed \hat{x}
        priStateEstimate = (aMatrix * predictedMeasurement) + (bMatrix * controlInput);

        // P^{-} computes mostly from P
        priEstErrCovar = (aMatrix * postEstErrCovar * transpose(aMatrix)) + processNoiseCovar;

    }

    public double correct(double z) {

        predict();

        actualMeasurement = z;

        // first part of correction, computes Kalman gain
        // the bigger R is, the smaller K is, therefore K have smaller weight and vice versa
        kalmanGain = (priEstErrCovar * transpose(hMatrix)) / ((hMatrix * priEstErrCovar * transpose(hMatrix)) + measurementNoiseCovar);

        // final correction of the value to be filtered
        predictedMeasurement = priStateEstimate + (kalmanGain * (actualMeasurement - (hMatrix * priStateEstimate)));

        // last part is update of P to be used in next step
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

            // gets positions from Skeleton entity into temporary TreeMap
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

                // sets initial parameters
                setInputs(measurementNoiseCovariance);

                ArrayList<Float> tempAxisPositions = new ArrayList<Float>();

                boolean isFirst = true;

                for (Float pos : tempSkeletonCleanedPositions.get((JointType) entry.getKey())) {
                    
                    // if the measurement is first in its sequence, 
                    // sets \hat{x} to first measured value
                    if (isFirst) {
                        setFirstPredictedMeasurement(pos);
                        isFirst = false;
                    }

                    // applies Kalman filter
                    pos = (float) correct(pos);

                    tempAxisPositions.add(pos);
                }

                tempSkeletonPositions.put((JointType) entry.getKey(), tempAxisPositions);

            }

            // sets cleaned positions of Skeleton entity
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

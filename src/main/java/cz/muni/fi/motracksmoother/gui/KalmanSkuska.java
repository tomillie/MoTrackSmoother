/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.motracksmoother.gui;

import java.util.ArrayList;

/**
 *
 * 
 * @author Tomáš
 */
public class KalmanSkuska {

    private double prioriStateEstimate;                 // x^-
    private double aMatrix;                             // A
    private double bMatrix;                             // B
    private double hMatrix;                             // H
    private double predictedMeasurement;                // x^
    private double actualMeasurement;                   // z
    private double controlInput;                        // u
    private double prioriEstimateErrorCovariance;       // P-
    private double posterioriEstimateErrorCovariance;   // P
    private double measurementNoiseCovariance;          // R
    private double processNoiseCovariance;              // Q
    private double kalmanGain;                          // K
    
    public void setInputs() {
        
        aMatrix = 1;
        bMatrix = 1;
        hMatrix = 1;
        predictedMeasurement = 0;               // nastavit na prvu nameranu hodnotu
        controlInput = 0;
        prioriEstimateErrorCovariance = 1;
        posterioriEstimateErrorCovariance = 1;  // to iste ako prioriEstimateErrorCovariance
//        measurementNoiseCovariance = 0.01;
//        processNoiseCovariance = 0.001;
        measurementNoiseCovariance = 0.1;
        processNoiseCovariance = 0;
        
    }
    
    public void predict() {
        
        prioriStateEstimate = (aMatrix * predictedMeasurement) + (bMatrix * controlInput);
        
        prioriEstimateErrorCovariance = (aMatrix * posterioriEstimateErrorCovariance * transpose(aMatrix)) + processNoiseCovariance;
        
    }
    
    public double correct(double z) {
        
        predict();
        
        actualMeasurement = z;
        
        kalmanGain = (prioriEstimateErrorCovariance * transpose(hMatrix)) / ((hMatrix * prioriEstimateErrorCovariance * transpose(hMatrix)) + measurementNoiseCovariance);

        predictedMeasurement = prioriStateEstimate + (kalmanGain * (actualMeasurement - (hMatrix * prioriStateEstimate)));
        
        posterioriEstimateErrorCovariance = (1 - (kalmanGain * hMatrix)) * prioriEstimateErrorCovariance;
        
        return predictedMeasurement;
    }
    
    public double transpose(double matrix) {
       
        return matrix;
        
    }
    
    
    public static void main(String[] args) {
       
        KalmanSkuska kalman = new KalmanSkuska();
        
        ArrayList<Double> list = new ArrayList<Double>();
        list.add(Double.valueOf(0.390));
        list.add(Double.valueOf(0.500));
        list.add(Double.valueOf(0.480));
        list.add(Double.valueOf(0.290));
        list.add(Double.valueOf(0.250));
        list.add(Double.valueOf(0.320));
        list.add(Double.valueOf(0.340));
        list.add(Double.valueOf(0.480));
        list.add(Double.valueOf(0.410));
        list.add(Double.valueOf(0.450));
        
        kalman.setInputs();
        
        for (Double z : list) {
            System.out.println(z);
            System.out.println(kalman.correct(z));
//            System.out.println(P + "/" + Q + "/" + R + "/" + X + "/" + K);
            System.out.println("-------------------------------------------");
        }
        
        
    }

}

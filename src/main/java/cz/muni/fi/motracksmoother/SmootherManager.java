package cz.muni.fi.motracksmoother;

/**
 * Provides smoothing by application of filters to suppress estimation errors.
 * 
 * @author Tomáš
 * @version 1.0
 * @since 1.0
 */
public interface SmootherManager {

    /**
     * Sets some necessary inputs.
     * 
     * @param measurementNoiseCovariance is the Measurement Noise (Error) Covariance given by user
     * @version 1.0
     * @since 1.0
     */
    void setInputs(double measurementNoiseCovariance);

    /**
     * Prediction phase of Kalman filtering.
     * 
     * @version 1.0
     * @since 1.0
     */
    void predict();

    /**
     * Correction phase of Kalman filtering.
     * 
     * @param z is measured value, which is going to be filtered
     * @return filtered value
     * @version 1.0
     * @since 1.0
     */
    double correct(double z);

    /**
     * Simple transposing of a matrix, which in this case ordinary number.
     * 
     * @param matrix number to be returned
     * @return the same number given as the parameter
     * @version 1.0
     * @since 1.0
     */
    double transpose(double matrix);

    /**
     * Processes Skeleton entity, gets data from it and uses Kalman formulas to filtering.
     * 
     * @param skeleton is the entity to read data from
     * @param measurementNoiseCovariance is the Measurement Noise (Error) Covariance given by user
     * @version 1.0
     * @since 1.0
     */
    void kalman(Skeleton skeleton, double measurementNoiseCovariance);
    
}
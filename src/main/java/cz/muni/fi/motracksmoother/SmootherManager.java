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
     * @version 1.1
     * @since 1.0
     */
    void setInputs(double measurementNoiseCovariance);

    /**
     * Prediction phase of Kalman filtering.
     * <pre>
     * \hat{x}_{k}^{-} = A\hat{x}_{k - 1} + Bu_{k - 1} 
     * 
     * P_{k}^{-} = AP_{k - 1}A^{T} + Q 
     * </pre>
     * 
     * @version 1.0
     * @since 1.0
     */
    void predict();

    /**
     * Correction phase of Kalman filtering.
     * 
     * <pre>
     * K_{k} =  P_{k}^{-}H^{T}(HP_{k}^{-}H^{T} + R)^{-1}
     * 
     * \hat{x}_{k} = x_{k}^{-} + K_{k}(z_{k} - H\hat{x}_{k}^{-}) 
     * 
     * P_{k} = (1 - K_{k}H)P_{k}^{-} 
     * </pre>
     * 
     * @param z is measured value, which is going to be filtered
     * @return filtered value
     * @version 1.0
     * @since 1.0
     */
    double correct(double z);

    /**
     * Simple transpose of a matrix, which in this case is ordinary number.
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
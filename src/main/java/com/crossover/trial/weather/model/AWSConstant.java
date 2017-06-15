/**
 * 
 */
package com.crossover.trial.weather.model;

/**
 * Useful class to group domain constants.
 * @author hernan.tenjo
 */
public final class AWSConstant {
    /** earth radius in KM */
    public static final double R = 6372.8;
    /** Amount of milliseconds in 24h*/
    public static final long MILLIS_IN_A_DAY = 86400000;
    

    public static final String DATAPOINT_ERROR_MSG = "Invalid mean value for ";
    public static final int DATAPOINT_WIND_MEAN_MIN = 0;
    
    public static final int DATAPOINT_TEMPERATURE_MEAN_MIN = -50;
    public static final int DATAPOINT_TEMPERATURE_MEAN_MAX = 100;
    
    public static final int DATAPOINT_HUMIDITY_MEAN_MIN = 0;
    public static final int DATAPOINT_HUMIDITY_MEAN_MAX = 100;
    
    public static final int DATAPOINT_PRESSURE_MEAN_MIN = 650;
    public static final int DATAPOINT_PRESSURE_MEAN_MAX = 800;
    
    public static final int DATAPOINT_CLOUDCOVER_MEAN_MIN = 0;
    public static final int DATAPOINT_CLOUDCOVER_MEAN_MAX = 100;
    
    public static final int DATAPOINT_PRECIPITATION_MEAN_MIN = 0;
    public static final int DATAPOINT_PRECIPITATION_MEAN_MAX = 100;
    
}

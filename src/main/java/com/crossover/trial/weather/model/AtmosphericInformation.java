package com.crossover.trial.weather.model;

/**
 * encapsulates sensor information for a particular location
 */
public class AtmosphericInformation {
    /** temperature in degrees celsius */
    private DataPoint temperature;

    /** wind speed in km/h */
    private DataPoint wind;

    /** humidity in percent */
    private DataPoint humidity;

    /** precipitation in cm */
    private DataPoint precipitation;

    /** pressure in mmHg */
    private DataPoint pressure;

    /** cloud cover percent from 0 - 100 (integer) */
    private DataPoint cloudCover;

    /** the last time this data was updated, in milliseconds since UTC epoch */
    private long lastUpdateTime;

    /**
     * 
     * @param temperature
     * @param wind
     * @param humidity
     * @param percipitation
     * @param pressure
     * @param cloudCover
     */
    protected AtmosphericInformation(DataPoint temperature, DataPoint wind, 
    		DataPoint humidity, DataPoint percipitation, DataPoint pressure, DataPoint cloudCover) {
        this.temperature = temperature;
        this.wind = wind;
        this.humidity = humidity;
        this.precipitation = percipitation;
        this.pressure = pressure;
        this.cloudCover = cloudCover;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public DataPoint getTemperature() {
        return temperature;
    }
    public void setTemperature(DataPoint temperature) {
        this.temperature = temperature;
        this.updateInformation();
    }
    public DataPoint getWind() {
        return wind;
    }
    public void setWind(DataPoint wind) {
        this.wind = wind;
        this.updateInformation();
    }
    public DataPoint getHumidity() {
        return humidity;
    }
    public void setHumidity(DataPoint humidity) {
        this.humidity = humidity;
        this.updateInformation();
    }
    public DataPoint getPrecipitation() {
        return precipitation;
    }
    public void setPrecipitation(DataPoint precipitation) {
        this.precipitation = precipitation;
        this.updateInformation();
    }
    public DataPoint getPressure() {
        return pressure;
    }
    public void setPressure(DataPoint pressure) {
        this.pressure = pressure;
        this.updateInformation();
    }
    public DataPoint getCloudCover() {
        return cloudCover;
    }
    public void setCloudCover(DataPoint cloudCover) {
        this.cloudCover = cloudCover;
        this.updateInformation();
    }
    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }
    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

	public boolean hasInformation() {
		if (this.getCloudCover() != null || this.getHumidity() != null 
				|| this.getPrecipitation() != null || this.getPressure() != null 
				|| this.getTemperature() != null || this.getWind() != null) {
			return true;
		}
			
		return false;
	}
	
	public boolean isUpdatedInLastDay(){
		return this.getLastUpdateTime() > System.currentTimeMillis() - AWSConstant.MILLIS_IN_A_DAY;
	}
	
	private void updateInformation(){
		setLastUpdateTime(System.currentTimeMillis());
	}

    public static class Builder {
        private DataPoint temperature;
        private DataPoint wind;
        private DataPoint humidity;
        private DataPoint precipitation;
        private DataPoint pressure;
        private DataPoint cloudCover;
        
        public Builder() { }
        
        public Builder withTemperature(DataPoint temperature){
        	this.temperature = temperature;
        	return this;
        }
        
        public Builder withWind(DataPoint wind){
        	this.wind = wind;
        	return this;
        }
        
        public Builder withHumidity(DataPoint humidity){
        	this.humidity = humidity;
        	return this;
        }
        
        public Builder withPrecipitation(DataPoint precipitation){
        	this.precipitation = precipitation;
        	return this;
        }
        
        public Builder withPressure(DataPoint pressure){
        	this.pressure = pressure;
        	return this;
        }
        
        public Builder withCloudCover(DataPoint cloudCover){
        	this.cloudCover = cloudCover;
        	return this;
        }
        
        public AtmosphericInformation build(){
        	return new AtmosphericInformation(
        			this.temperature, 
        			this.wind, 
        			this.humidity, 
        			this.precipitation, 
        			this.pressure, 
        			this.cloudCover);
        }
    }
}

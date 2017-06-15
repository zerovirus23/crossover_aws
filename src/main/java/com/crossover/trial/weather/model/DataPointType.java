package com.crossover.trial.weather.model;

import static com.crossover.trial.weather.model.AWSConstant.DATAPOINT_CLOUDCOVER_MEAN_MAX;
import static com.crossover.trial.weather.model.AWSConstant.DATAPOINT_CLOUDCOVER_MEAN_MIN;
import static com.crossover.trial.weather.model.AWSConstant.DATAPOINT_ERROR_MSG;
import static com.crossover.trial.weather.model.AWSConstant.DATAPOINT_HUMIDITY_MEAN_MAX;
import static com.crossover.trial.weather.model.AWSConstant.DATAPOINT_HUMIDITY_MEAN_MIN;
import static com.crossover.trial.weather.model.AWSConstant.DATAPOINT_PRECIPITATION_MEAN_MAX;
import static com.crossover.trial.weather.model.AWSConstant.DATAPOINT_PRECIPITATION_MEAN_MIN;
import static com.crossover.trial.weather.model.AWSConstant.DATAPOINT_PRESSURE_MEAN_MAX;
import static com.crossover.trial.weather.model.AWSConstant.DATAPOINT_PRESSURE_MEAN_MIN;
import static com.crossover.trial.weather.model.AWSConstant.DATAPOINT_TEMPERATURE_MEAN_MAX;
import static com.crossover.trial.weather.model.AWSConstant.DATAPOINT_TEMPERATURE_MEAN_MIN;
import static com.crossover.trial.weather.model.AWSConstant.DATAPOINT_WIND_MEAN_MIN;

import java.util.function.BiConsumer;

import com.crossover.trial.weather.exception.WeatherException;

/**
 * The various types of data points we can collect.
 * @author code test administrator
 */
public enum DataPointType {
	WIND() {
		@Override
		public BiConsumer<AtmosphericInformation, DataPoint> getDataPointUpdater() {
			return (atmosphericInfo, dataPoint) -> {
				if (dataPoint.getMean() >= DATAPOINT_WIND_MEAN_MIN) {
					atmosphericInfo.setWind(dataPoint);
				} else {
					throw new WeatherException(DATAPOINT_ERROR_MSG + this.name());
				}
			};
		}
	},
	TEMPERATURE() {
		@Override
		public BiConsumer<AtmosphericInformation, DataPoint> getDataPointUpdater() throws WeatherException {
			return (atmosphericInfo, info) -> {
				if (info.getMean() >= DATAPOINT_TEMPERATURE_MEAN_MIN
						&& info.getMean() < DATAPOINT_TEMPERATURE_MEAN_MAX) {
					atmosphericInfo.setTemperature(info);
				} else {
					throw new WeatherException(DATAPOINT_ERROR_MSG + this.name());
				}
			};
		}
	},
	HUMIDTY() {
		@Override
		public BiConsumer<AtmosphericInformation, DataPoint> getDataPointUpdater() throws WeatherException {
			return (atmosphericInfo, info) -> {
				if (info.getMean() >= DATAPOINT_HUMIDITY_MEAN_MIN && info.getMean() < DATAPOINT_HUMIDITY_MEAN_MAX) {
					atmosphericInfo.setHumidity(info);
				} else {
					throw new WeatherException(DATAPOINT_ERROR_MSG + this.name());
				}
			};
		}
	},
	PRESSURE() {
		@Override
		public BiConsumer<AtmosphericInformation, DataPoint> getDataPointUpdater() throws WeatherException {
			return (atmosphericInfo, info) -> {
				if (info.getMean() >= DATAPOINT_PRESSURE_MEAN_MIN && info.getMean() < DATAPOINT_PRESSURE_MEAN_MAX) {
					atmosphericInfo.setPressure(info);
				} else {
					throw new WeatherException(DATAPOINT_ERROR_MSG + this.name());
				}
			};
		}
	},
	CLOUDCOVER() {
		@Override
		public BiConsumer<AtmosphericInformation, DataPoint> getDataPointUpdater() throws WeatherException {
			return (atmosphericInfo, info) -> {
				if (info.getMean() >= DATAPOINT_CLOUDCOVER_MEAN_MIN && info.getMean() < DATAPOINT_CLOUDCOVER_MEAN_MAX) {
					atmosphericInfo.setCloudCover(info);
				} else {
					throw new WeatherException(DATAPOINT_ERROR_MSG + this.name());
				}
			};
		}
	},
	PRECIPITATION() {
		@Override
		public BiConsumer<AtmosphericInformation, DataPoint> getDataPointUpdater() throws WeatherException {
			return (atmosphericInfo, info) -> {
				if (info.getMean() >= DATAPOINT_PRECIPITATION_MEAN_MIN && info.getMean() < DATAPOINT_PRECIPITATION_MEAN_MAX) {
					atmosphericInfo.setPrecipitation(info);
				} else {
					throw new WeatherException(DATAPOINT_ERROR_MSG + this.name());
				}
			};
		}
	};

	/**
	 * For complex cases this could be made with a class hierarchy or maybe a
	 * factory, but for now I'll let the behavior here for simplicity and
	 * avoiding create more classes.
	 * 
	 * @param atmosphericInfo Object to be updated with the new info.
	 * @param info Object with the info to be processed.
	 * @throws WeatherException if there is some validation error
	 */
	public abstract BiConsumer<AtmosphericInformation, DataPoint> getDataPointUpdater();
}

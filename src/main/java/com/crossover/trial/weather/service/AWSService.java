package com.crossover.trial.weather.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.model.AirportData;
import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.model.DataPoint;
import com.crossover.trial.weather.model.DataPointType;

public interface AWSService {
	/**
	 * Search the airport information.
	 * @param iataCode Unique code of the airport.
	 * @return The airport information if exist.
	 */
	AirportData findAirport(String iataCode);
	
	/**
     * Add a new known airport.
     * @param iataCode 3 letter code
     * @param latitude in degrees
     * @param longitude in degrees
     * @return the added airport
     */
	void addAirport(String iataCode, double latitude, double longitude);
	
	/**
	 * Gets all airport codes registered.
	 * @return All codes registered.
	 */
	Collection<String> listAllAirportCodes();
	
	/**
	 * Delete a registered airport.
	 * @param iataCode
	 */
	void deleteAirport(String iataCode);
	
	/**
	 * Delete all registered airports.
	 */
	void deleteAllAirportsRegistered();
	
	/**
	 * Find all airports close to a given airport and a radius.
	 * @param airportData Base point to locate near airports.
	 * @param radius Distance from base point to search.
	 * @return All airports that are available with the given conditions.
	 */
	List<AirportData> findAirportsCloseTo(AirportData airportData, double radius);
	
	/**
	 * Get the frequencies statistics.
	 * @return
	 */
	Map<String, Double> getAirportRequestFrequencies();
	
	/**
	 * Get the radius statistics.
	 * @return
	 */
	int[] getRadiusRequestFrequencies();
	
	/**
	 * Update atmospheric information in the given airport.
	 * @param iataCode Code of the object to be updated with the new info.
	 * @param point Object with the info to be processed
	 * @param type Point information type
	 */
	void updateAtmosphericInfo(String iataCode, DataPoint point, DataPointType type) throws WeatherException;
	
	
	/**
	 * Get all atmospheric info registered close to an airport.
	 * @param iataCode airport code.
	 * @param radius distance to search.
	 * @return all atmospheric info that satisfy the given conditions.
	 */
	List<AtmosphericInformation> findAtmosphericInfoCloseToAirport(String iataCode, double radius);

	/**
	 * Get an specific atmospheric condition for a given airport.
	 * @param iataCode Airport code.
	 * @return The atmospheric information for the given airport.
	 */
	AtmosphericInformation findAtmosphericInfo(String iataCode);
	
	/**
	 * Identify the amount of conditions updated in the last day.
	 * @return
	 */
	long countAtmosphericInfoFromLastDay();
}

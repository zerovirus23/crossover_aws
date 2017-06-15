/**
 * 
 */
package com.crossover.trial.weather.persistence;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.crossover.trial.weather.model.AirportData;

/**
 * Class that represent a repository (File, Database, Remote Folder, etc) and
 * helps to decouple the logic of the WS from its data services.
 * 
 * @author hernan.tenjo
 */
public class AirportRepository {
	/** This is a basic, eager implementation of Singleton. */
	private static final AirportRepository repo = new AirportRepository();
	/** all known airports */
	private Map<String, AirportData> airportData = new ConcurrentHashMap<>();

	private AirportRepository() {
	}

	public static AirportRepository getInstance() {
		return repo;
	}

	public void add(AirportData airport) {
		this.airportData.put(airport.getIata(), airport);
	}

	public AirportData find(String id) {
		return this.airportData.get(id);
	}

	public Collection<AirportData> listAll() {
		return this.airportData.values();
	}

	public Collection<String> listAllCodes() {
		return this.airportData.keySet();
	}

	public void delete(String iataCode) {
		this.airportData.remove(iataCode);
	}

	public void deleteAll() {
		this.airportData.clear();
	}
}

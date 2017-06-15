/**
 * 
 */
package com.crossover.trial.weather.persistence;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.crossover.trial.weather.model.AtmosphericInformation;

/**
 * Class that represent a repository (File, Database, Remote Folder, etc)
 * and helps to decouple the logic of the WS from its data services.
 * @author hernan.tenjo
 */
public class AtmosphericRepository {
	/** This is a basic, eager implementation of Singleton. */
	private static final AtmosphericRepository repo = new AtmosphericRepository();
	/** Atmospheric conditions by airport */
    private Map<String, AtmosphericInformation> atmosphericInfo = new ConcurrentHashMap<>();
	
	private AtmosphericRepository(){
	}
	
	public static AtmosphericRepository getInstance(){
		return repo;
	}
	
	/**
	 * Add new {@link AtmosphericInformation} to the repository
	 * @param iataCode The airport code for the given information
	 * @param info Multiple atmospheric conditions for the related airport
	 */
	public void add(String iataCode, AtmosphericInformation info){
		this.atmosphericInfo.put(iataCode, info);
	}
	
	public AtmosphericInformation find(String iataCode){
		return this.atmosphericInfo.get(iataCode);
	}
	
	/**
	 * This operation is not required right now because all are objects in the same VM, but if
	 * we change the implementation of the repository, then when we update the object in out context
	 * should be necessary to update the instance in the repo.
	 * @param iataCode
	 * @param info
	 */
	public void update(String iataCode, AtmosphericInformation info) {
		this.add(iataCode, info);
	}
	
	public void delete(String iataCode) {
		this.atmosphericInfo.remove(iataCode);
	}
	
	public void deleteAll(){
		this.atmosphericInfo.clear();
	}
	
	public long countUpdatedFromLastDay(){
		return atmosphericInfo.values()
				.stream()
				.filter(info -> info.hasInformation())
				.filter(info -> info.isUpdatedInLastDay())
				.count();
	}
}

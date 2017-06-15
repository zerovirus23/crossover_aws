package com.crossover.trial.weather.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.model.AWSConstant;
import com.crossover.trial.weather.model.AirportData;
import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.model.DataPoint;
import com.crossover.trial.weather.model.DataPointType;
import com.crossover.trial.weather.persistence.AirportRepository;
import com.crossover.trial.weather.persistence.AtmosphericRepository;

/**
 * Class that represent a repository service (File, Database, Remote Folder, etc)
 * and helps to decouple the logic of the WS from its data services.
 * @author hernan.tenjo
 */
public class AWSServiceImpl implements AWSService {
	private static final AirportRepository airportRepo = AirportRepository.getInstance();
	private static final AtmosphericRepository atmosphericRepo = AtmosphericRepository.getInstance();
	//Map<IataCode, Counter> 
    private static Map<String, Integer> requestFrequency = new ConcurrentHashMap<>();
    //Map<Radius, Counter>
    private static Map<Double, Integer> radiusFreq = new ConcurrentHashMap<>();
    
    public AWSServiceImpl() {
	}
    
	/* ======================== AIRPORT SERVICES ========================= */
	/*
	 * (non-Javadoc)
	 * @see com.crossover.trial.weather.service.AWSService#deleteRegisteredAirports()
	 */
	@Override
	public void deleteAllAirportsRegistered(){
		airportRepo.deleteAll();
		atmosphericRepo.deleteAll();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.crossover.trial.weather.service.AWSService#deleteAirport(java.lang.String)
	 */
	@Override
	public void deleteAirport(String iataCode) {
		airportRepo.delete(iataCode);
		atmosphericRepo.delete(iataCode);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.crossover.trial.weather.service.AWSService#addAirport(java.lang.String, double, double)
	 */
	@Override
    public void addAirport(String iataCode, double latitude, double longitude) {
        AirportData airport = new AirportData();
        airport.setIata(iataCode);
        airport.setLatitude(latitude);
        airport.setLongitude(longitude);
        airportRepo.add(airport);
        atmosphericRepo.add(iataCode, new AtmosphericInformation.Builder().build());
    }
    
	/*
	 * (non-Javadoc)
	 * @see com.crossover.trial.weather.service.AWSService#findAirport(java.lang.String)
	 */
	@Override
    public AirportData findAirport(String iataCode){
    	return airportRepo.find(iataCode);
    }
    
	/*
	 * (non-Javadoc)
	 * @see com.crossover.trial.weather.service.AWSService#findAirportsCloseTo(com.crossover.trial.weather.model.AirportData, double)
	 */
	@Override
	public List<AirportData> findAirportsCloseTo(AirportData airportData, double radius) {
		if(airportData == null){
			return new ArrayList<>();
		}
		
		return airportRepo.listAll()
				.stream()
				.filter(item -> calculateDistanceBetween(item, airportData) <= radius)
				.collect(Collectors.toList());
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.crossover.trial.weather.service.AWSService#listAllAirportCodes()
	 */
	@Override
	public Collection<String> listAllAirportCodes() {
		return airportRepo.listAllCodes();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.crossover.trial.weather.service.AWSService#getAirportRequestFrequencies()
	 */
	@Override
	public Map<String, Double> getAirportRequestFrequencies() {
		Map<String, Double> freq = new HashMap<>();
        int requestSize = requestFrequency.size();
        
        for (String iataCode : listAllAirportCodes()) {
            double frac = requestSize == 0 ? 0 : (double)requestFrequency.getOrDefault(iataCode, 0) / requestSize;
            freq.put(iataCode, frac);
        }
        
		return freq;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.crossover.trial.weather.service.AWSService#getRadiusRequestFrequencies()
	 */
	@Override
	public int[] getRadiusRequestFrequencies() {
		//TO_THINK: I'm not sure what's the purpose of this algorithm? 
		//So I'll let it as it is for now to don't brake the contract with the clients
		int m = radiusFreq.keySet().stream()
                .max(Double::compare)
                .orElse(1000.0).intValue() + 1;

        int[] hist = new int[m];
        
        for (Map.Entry<Double, Integer> e : radiusFreq.entrySet()) {
            int i = e.getKey().intValue() % 10;
            hist[i] += e.getValue();
        }
        
        return hist;
	}
	
	/* ======================== ATMOSPHERIC SERVICES ========================= */
	/*
	 * (non-Javadoc)
	 * @see com.crossover.trial.weather.service.AWSService#findAtmosphericInfo(java.lang.String)
	 */
	@Override
    public AtmosphericInformation findAtmosphericInfo(String iataCode) {
    	return atmosphericRepo.find(iataCode);
    }
	
	/*
	 * (non-Javadoc)
	 * @see com.crossover.trial.weather.service.AWSService#countAtmosphericInfoFromLastDay()
	 */
	@Override
	public long countAtmosphericInfoFromLastDay() {
		return atmosphericRepo.countUpdatedFromLastDay();
	}
    
	/*
	 * (non-Javadoc)
	 * @see com.crossover.trial.weather.service.AWSService#findAtmosphericInfoCloseToAirport(java.lang.String, double)
	 */
	@Override
    public List<AtmosphericInformation> findAtmosphericInfoCloseToAirport(String iataCode, double radius) {
    	updateRequestFrequency(iataCode, radius);
        
        if (radius <= 0) {
        	List<AtmosphericInformation> atmosphericValues = new ArrayList<>();
        	AtmosphericInformation info = findAtmosphericInfo(iataCode);
        	
        	if(info != null && info.hasInformation()) {
        		atmosphericValues.add(info);
        	}
        	
        	return atmosphericValues;
        } else {
            AirportData ad = findAirport(iataCode);
            return findAirportsCloseTo(ad, radius)
            		.stream()
            		.map(airport -> findAtmosphericInfo(airport.getIata()))
            		.filter(atmosphericInfo -> Objects.nonNull(atmosphericInfo))
            		.filter(atmosphericInfo -> atmosphericInfo.hasInformation())
            		.collect(Collectors.toList());
        }
    }
	
	/*
	 * (non-Javadoc)
	 * @see com.crossover.trial.weather.service.AWSService#
	 * updateAtmosphericInfo(java.lang.String, com.crossover.trial.weather.model.DataPoint)
	 */
	@Override
	public void updateAtmosphericInfo(String iataCode, DataPoint point, DataPointType type) throws WeatherException {
		AtmosphericInformation atmosphericInformation = atmosphericRepo.find(iataCode);
		type.getDataPointUpdater().accept(atmosphericInformation, point);
		atmosphericRepo.update(iataCode, atmosphericInformation);
	}
	
	/* ======================== INTERNAL SERVICES ========================= */
	/**
     * Haver sine distance between two airports.
     * @param ad1 airport 1
     * @param ad2 airport 2
     * @return the distance in KM
     */
    private double calculateDistanceBetween(AirportData ad1, AirportData ad2) {
        double deltaLat = Math.toRadians(ad2.getLatitude() - ad1.getLatitude());
        double deltaLon = Math.toRadians(ad2.getLongitude() - ad1.getLongitude());
        double a =  Math.pow(Math.sin(deltaLat / 2), 2) + Math.pow(Math.sin(deltaLon / 2), 2)
                * Math.cos(ad1.getLatitude()) * Math.cos(ad2.getLatitude());
        double c = 2 * Math.asin(Math.sqrt(a));
        return AWSConstant.R * c;
    }
    
    /**
     * Records information about how often requests are made
     * @param iata an code
     * @param radius query radius
     */
    private void updateRequestFrequency(String iata, Double radius) {
        requestFrequency.put(iata, requestFrequency.getOrDefault(iata, 0) + 1);
        radiusFreq.put(radius, radiusFreq.getOrDefault(radius, 0) + 1);
    }
}

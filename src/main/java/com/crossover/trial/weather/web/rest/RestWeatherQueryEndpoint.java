package com.crossover.trial.weather.web.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.service.AWSService;
import com.crossover.trial.weather.service.AWSServiceImpl;
import com.crossover.trial.weather.web.WeatherQueryEndpoint;
import com.google.gson.Gson;

/**
 * The Weather App REST endpoint allows clients to query, update and check health stats. Currently, all data is
 * held in memory. The end point deploys to a single container
 * @author code test administrator
 */
@Path(RestWeatherQueryEndpoint.END_POINT_URL)
public class RestWeatherQueryEndpoint implements WeatherQueryEndpoint {
	public static final String END_POINT_URL = "/query";
	private static final String DATA_SIZE_PARAM = "datasize";
	private static final String AIRPORT_FREQ_PARAM = "iata_freq";
	private static final String RADIUS_FREQ_PARAM = "radius_freq";
	//This should be improved to use DependencyInjection
	private final AWSService service = new AWSServiceImpl();

    /** Shared GSON JSON to object factory */
    private final Gson gson = new Gson();
    
    /**
     * Retrieve service health including total size of valid data points and request frequency information.
     * @return health stats for the service as a string
     */
    @Override
    public String ping() {
        Map<String, Object> retval = new HashMap<>();
        retval.put(DATA_SIZE_PARAM, service.countAtmosphericInfoFromLastDay());
        retval.put(AIRPORT_FREQ_PARAM, service.getAirportRequestFrequencies());        
        retval.put(RADIUS_FREQ_PARAM, service.getRadiusRequestFrequencies());
        return gson.toJson(retval);
    }

    /**
     * Given a query in json format {'iata': CODE, 'radius': km} extracts the requested airport information and
     * return a list of matching atmosphere information.
     * @param iata the iataCode
     * @param radiusString the radius in km
     * @return a list of atmospheric information
     */
    @Override
    public Response weather(String iata, String radiusString) {
        double radius = StringUtils.isBlank(radiusString) ? 0 : Double.valueOf(radiusString);
        List<AtmosphericInformation> retval = service.findAtmosphericInfoCloseToAirport(iata, radius);
        return Response.status(Response.Status.OK).entity(retval).build();
    }    
}
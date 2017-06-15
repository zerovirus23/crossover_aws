package com.crossover.trial.weather.web.rest;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.model.DataPoint;
import com.crossover.trial.weather.model.DataPointType;
import com.crossover.trial.weather.service.AWSService;
import com.crossover.trial.weather.service.AWSServiceImpl;
import com.crossover.trial.weather.web.WeatherCollectorEndpoint;
import com.google.gson.Gson;

/**
 * A REST implementation of the WeatherCollector API. Accessible only to airport weather collection
 * sites via secure VPN.
 * @author code test administrator
 */
@Path(RestWeatherCollectorEndpoint.END_POINT_URL)
public class RestWeatherCollectorEndpoint implements WeatherCollectorEndpoint {
    public static final String END_POINT_URL = "/collect";
    private static final String READY_MSG = "ready";
	//This should be improved to use DependencyInjection
	private final AWSService service = new AWSServiceImpl();
    /** shared GSON JSON to object factory */
    public final static Gson gson = new Gson();

    /*
     * (non-Javadoc)
     * @see com.crossover.trial.weather.web.WeatherCollectorEndpoint#ping()
     */
    @Override
    public Response ping() {
        return Response.status(Response.Status.OK).entity(READY_MSG).build();
    }

    /*
     * (non-Javadoc)
     * @see com.crossover.trial.weather.web.WeatherCollectorEndpoint#getAirports()
     */
    @Override
    public Response getAirports() {
        return Response
        		.status(Response.Status.OK)
        		.entity(service.listAllAirportCodes())
        		.build();
    }

    /*
     * (non-Javadoc)
     * @see com.crossover.trial.weather.web.WeatherCollectorEndpoint#getAirport(java.lang.String)
     */
    @Override
    public Response getAirport(String iata) {
        return Response
        		.status(Response.Status.OK)
        		.entity(service.findAirport(iata))
        		.build();
    }

    /*
     * (non-Javadoc)
     * @see com.crossover.trial.weather.web.WeatherCollectorEndpoint#
     * addAirport(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Response addAirport(String iata, String latString, String longString) {
    	double latitude = Double.valueOf(latString);
    	double longitude = Double.valueOf(longString);
    	service.addAirport(iata, latitude, longitude);
        return Response.status(Response.Status.OK).build();
    }

    /*
     * (non-Javadoc)
     * @see com.crossover.trial.weather.web.WeatherCollectorEndpoint#
     * updateWeather(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Response updateWeather(String iataCode, String pointType, String datapointJson) {
        try {
        	DataPointType dataPointType = DataPointType.valueOf(pointType.toUpperCase());
        	DataPoint dataPoint = gson.fromJson(datapointJson, DataPoint.class);
            service.updateAtmosphericInfo(iataCode, dataPoint, dataPointType);
        	return Response.status(Response.Status.OK).build();
        } catch (WeatherException e) {
        	return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
        	return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.crossover.trial.weather.web.WeatherCollectorEndpoint#deleteAirport(java.lang.String)
     */
    @Override
    public Response deleteAirport(String iata) {
    	service.deleteAirport(iata);
        return Response.status(Response.Status.OK).build();
    }

    /*
     * (non-Javadoc)
     * @see com.crossover.trial.weather.web.WeatherCollectorEndpoint#exit()
     */
    @Override
    public Response exit() {
        System.exit(0);
        return Response.noContent().build();
    }
}

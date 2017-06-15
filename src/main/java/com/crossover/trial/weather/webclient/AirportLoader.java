package com.crossover.trial.weather.webclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.crossover.trial.weather.model.AirportData;

/**
 * A simple airport loader which reads a file from disk and sends entries to the
 * webservice.
 * 
 * @author code test administrator
 */
public class AirportLoader {
	private static final int IATA_INDEX = 4;
	private static final int LATITUDE_INDEX = 6;
	private static final int LONGITUDE_INDEX = 7;
	private static final String DEFAULT_SEPARATOR = ",";
	private static final String QUOTES = "\"";
	private static final String IATA_PARAM = "iata";
	private static final String LATITUDE_PARAM = "lat";
	private static final String LONGITUDE_PARAM = "long";
	private static final String COLLECT_ENDPOINT = "http://localhost:9090/collect/airport/{iata}/{lat}/{long}";
	/** end point to supply updates */
	private WebTarget collect;

	/**
	 * Default constructor.
	 */
	public AirportLoader() {
		Client client = ClientBuilder.newClient();
		collect = client.target(COLLECT_ENDPOINT);
	}

	/**
	 * Execute the massive load of airports.
	 */
	public void upload(InputStream airportDataStream) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(airportDataStream))) {
			String line = null;
			AirportData data;

			while ((line = reader.readLine()) != null) {
				// 1. Build the airport
				data = buildAirportFromInfo(line);
				// 2. Call the proper end point to load airports
				Response clientResponse = collect.resolveTemplate(IATA_PARAM, data.getIata())
						.resolveTemplate(LATITUDE_PARAM, data.getLatitude())
						.resolveTemplate(LONGITUDE_PARAM, data.getLongitude()).request().post(null, Response.class);

				if (clientResponse.getStatus() != 200) {

				}
			}
		}
	}

	/**
	 * There are some frameworks that make this process easier and map directly
	 * to objects, but for this purpose I'm going to keep it simple.
	 * 
	 * @param line
	 * @return
	 */
	private AirportData buildAirportFromInfo(String line) {
		// 1. Parse information -> each line should have exactly N positions
		// with info
		String[] airportInfo = StringUtils.split(line, DEFAULT_SEPARATOR);
		// 2. Extract the required information (there is no business rules
		// regarding invalid values, so set them to 0).
		String iataCode = airportInfo[IATA_INDEX];
		iataCode = StringUtils.replace(iataCode, QUOTES, StringUtils.EMPTY);
		iataCode = StringUtils.trim(iataCode);
		double latitude = NumberUtils.isNumber(airportInfo[LATITUDE_INDEX])
				? Double.parseDouble(airportInfo[LATITUDE_INDEX]) : 0;
		double longitude = NumberUtils.isNumber(airportInfo[LONGITUDE_INDEX])
				? Double.parseDouble(airportInfo[LONGITUDE_INDEX]) : 0;
		// 3. Build the Airport
		return new AirportData(iataCode, latitude, longitude);
	}

	/**
	 * Default main method (entry point to the loader).
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException {
		if (args.length == 0) {
			System.err.println("The file name is a required parameter...");
			System.exit(1);
		}

		File airportDataFile = new File(args[0]);

		if (!airportDataFile.exists() || airportDataFile.length() == 0) {
			System.err.println(airportDataFile + " is not a valid input...");
			System.exit(1);
		}

		AirportLoader al = new AirportLoader();
		al.upload(new FileInputStream(airportDataFile));
		System.exit(0);
	}
}

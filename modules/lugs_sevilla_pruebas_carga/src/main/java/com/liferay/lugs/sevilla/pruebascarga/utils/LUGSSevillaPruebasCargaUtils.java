
package com.liferay.lugs.sevilla.pruebascarga.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.lugs.sevilla.pruebascarga.beans.Asteroid;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * Utils class
 * 
 * @author LUG Spain Sevilla
 */
@Component(immediate = true, service = LUGSSevillaPruebasCargaUtils.class)
public class LUGSSevillaPruebasCargaUtils {

	private static final Log LOG =
		LogFactoryUtil.getLog(LUGSSevillaPruebasCargaUtils.class.getName());

	// Set proxy host (if it's necessary)
	private static final String PROXY_HOST = null;

	// Set proxy port (if it's necessary)
	private static final Integer PROXY_PORT = null;

	@Reference
	JSONFactory jsonFactory;

	/**
	 * Method to get Asteroids from Api Rest
	 * 
	 * @param useCache
	 *            Boolean, true to use cache. Otherwise false
	 * @param singleVMPool
	 *            PortalCache<Serializable, Serializable>
	 * @return List<Asteroid>
	 */
	public List<Asteroid> getAsteroidsDataFromAPI(
		Boolean useCache,
		PortalCache<Serializable, Serializable> singleVMPool) {

		List<Asteroid> asteroids = null;

		if (useCache) {
			asteroids = (List<Asteroid>) singleVMPool.get("asteroids");
		}

		if (!useCache || asteroids == null) {
			JSONArray asteroidsJSON =
				getJsonArray("http://www.asterank.com/api/mpc");

			asteroids = getAsteroids(asteroidsJSON);

			if (useCache) {
				singleVMPool.put(
					"asteroids", new ArrayList<Asteroid>(asteroids));
			}
		}

		return asteroids;
	}

	/**
	 * Auxiliar method to get JSON Array returned by an url through Api Rest
	 * Call
	 * 
	 * @param url
	 *            String
	 * @return JSONArray
	 */
	private JSONArray getJsonArray(String url) {

		JSONArray respuesta = null;

		HttpGet httpGet = new HttpGet(url);

		try {

			CloseableHttpClient httpClient = HttpClients.createDefault();

			CloseableHttpResponse httpResponse = doRequest(httpGet, httpClient);

			// Response Code
			int statusCode = httpResponse.getStatusLine().getStatusCode();

			LOG.debug("Response status code: " + statusCode);

			// Something was wrong
			if (statusCode != 200) {

				httpClient.close();

				LOG.error("Failed : HTTP error code : " + statusCode);
			}

			// Good response
			else {

				String entityContent = getEntityContent(httpResponse);

				LOG.debug("Response content: " + entityContent);

				respuesta = JSONFactoryUtil.createJSONArray(entityContent);

				httpClient.close();
			}
		}
		catch (RuntimeException e) {
			LOG.error("RuntimeException error at getJsonArray: ", e);
		}
		catch (ClientProtocolException e) {
			LOG.error("ClientProtocolException error at getJsonArray: ", e);
		}
		catch (IOException e) {
			LOG.error("IOException error at getJsonArray: ", e);
		}
		catch (Exception e) {
			LOG.error("Exception error at getJsonArray: ", e);
		}

		return respuesta;

	}

	/**
	 * Auxiliar method to do request to HttpGet, using httClient
	 * 
	 * @param httpGet
	 *            HttpGet
	 * @param httpClient
	 *            CloseableHttpClient
	 * @return CloseableHttpResponse
	 * @throws ClientProtocolException,
	 *             an error happened
	 * @throws IOException,
	 *             an error happened
	 */
	private CloseableHttpResponse doRequest(
		HttpGet httpGet, CloseableHttpClient httpClient)
		throws ClientProtocolException, IOException {

		// HTTP Request timeouts
		RequestConfig defaultRequestConfig =
			RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(
				10000).setConnectionRequestTimeout(
					10000).setStaleConnectionCheckEnabled(Boolean.TRUE).build();

		LOG.debug("Proxy host: " + PROXY_HOST);
		LOG.debug("Proxy port: " + PROXY_PORT);

		// PROXY Settings
		if (Validator.isNotNull(PROXY_HOST) &&
			Validator.isNotNull(PROXY_PORT)) {

			RequestConfig requestConfig =
				RequestConfig.copy(defaultRequestConfig).setProxy(
					new HttpHost(PROXY_HOST, PROXY_PORT)).build();

			httpGet.setConfig(requestConfig);
		}

		// HTTP Request execute
		return httpClient.execute(httpGet);

	}

	/**
	 * Auxiliar method to get Entity Content, through httpResponse
	 * 
	 * @param httpResponse
	 *            CloseableHttpResponse
	 * @return String, response
	 * @throws IllegalStateException,
	 *             an error happened
	 * @throws IOException,
	 *             an error happened
	 */
	private String getEntityContent(CloseableHttpResponse httpResponse)
		throws IllegalStateException, IOException {

		BufferedReader reader = new BufferedReader(
			new InputStreamReader(httpResponse.getEntity().getContent()));

		StringBuffer resp = new StringBuffer();
		String inputLine;

		while ((inputLine = reader.readLine()) != null) {
			resp.append(inputLine);
		}

		reader.close();

		return resp.toString();
	}

	/**
	 * Auxiliar method to parse JSONArray to List<Asteroid>
	 * 
	 * @param asteroidsJSON
	 *            JSONArray
	 * @return List<Asteroid>
	 */
	private List<Asteroid> getAsteroids(JSONArray asteroidsJSON) {

		List<Asteroid> asteroids = new ArrayList<Asteroid>();

		if (asteroidsJSON != null) {
			JSONObject asteroidJSON = null;
			for (int i = 0; i < asteroidsJSON.length(); i++) {
				asteroidJSON = asteroidsJSON.getJSONObject(i);
				if (asteroidJSON != null) {
					asteroids.add(parseJSONToAsteroid(asteroidJSON));
				}

			}

		}

		return asteroids;
	}

	/**
	 * Auxiliar method to parse JSONObject to Asteroid
	 * 
	 * @param asteroidJSON
	 *            JSONObject
	 * @return Asteroid
	 */
	private Asteroid parseJSONToAsteroid(JSONObject asteroidJSON) {

		Asteroid asteroid = null;

		if (asteroidJSON != null) {
			asteroid = new Asteroid();

			if (asteroidJSON.has("rms")) {
				asteroid.setRms(asteroidJSON.getString("rms"));
			}

			if (asteroidJSON.has("epoch")) {
				asteroid.setEpoch(asteroidJSON.getString("epoch"));
			}

			if (asteroidJSON.has("readable_des")) {
				asteroid.setReadable_des(
					asteroidJSON.getString("readable_des"));
			}

			if (asteroidJSON.has("H")) {
				asteroid.setH(asteroidJSON.getString("H"));
			}

			if (asteroidJSON.has("num_obs")) {
				asteroid.setNum_obs(asteroidJSON.getString("num_obs"));
			}

			if (asteroidJSON.has("ref")) {
				asteroid.setRef(asteroidJSON.getString("ref"));
			}

			if (asteroidJSON.has("G")) {
				asteroid.setG(asteroidJSON.getString("G"));
			}

			if (asteroidJSON.has("last_obs")) {
				asteroid.setLast_obs(asteroidJSON.getString("last_obs"));
			}

			if (asteroidJSON.has("comp")) {
				asteroid.setComp(asteroidJSON.getString("comp"));
			}

			if (asteroidJSON.has("M")) {
				asteroid.setM(asteroidJSON.getString("M"));
			}

			if (asteroidJSON.has("U")) {
				asteroid.setU(asteroidJSON.getString("U"));
			}

			if (asteroidJSON.has("e")) {
				asteroid.setE(asteroidJSON.getString("e"));
			}

			if (asteroidJSON.has("a")) {
				asteroid.setA(asteroidJSON.getString("a"));
			}

			if (asteroidJSON.has("om")) {
				asteroid.setOm(asteroidJSON.getString("om"));
			}

			if (asteroidJSON.has("pert_p")) {
				asteroid.setPert_p(asteroidJSON.getString("pert_p"));
			}

			if (asteroidJSON.has("d")) {
				asteroid.setD(asteroidJSON.getString("d"));
			}

			if (asteroidJSON.has("i")) {
				asteroid.setI(asteroidJSON.getString("i"));
			}

			if (asteroidJSON.has("des")) {
				asteroid.setDes(asteroidJSON.getString("des"));
			}

			if (asteroidJSON.has("flags")) {
				asteroid.setFlags(asteroidJSON.getString("flags"));
			}

			if (asteroidJSON.has("num_opp")) {
				asteroid.setNum_opp(asteroidJSON.getString("num_opp"));
			}

			if (asteroidJSON.has("w")) {
				asteroid.setW(asteroidJSON.getString("w"));
			}

			if (asteroidJSON.has("pert_c")) {
				asteroid.setPert_c(asteroidJSON.getString("pert_c"));
			}

		}

		return asteroid;
	}
}

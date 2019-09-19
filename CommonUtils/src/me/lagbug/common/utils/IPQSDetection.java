package me.lagbug.common.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class IPQSDetection {

	private String apiKey;

	public IPQSDetection(String apiKey) {
		this.apiKey = apiKey;
	}

	public JSONObject getResponse(String ip) {
		try {
			StringBuilder response = new StringBuilder();
			URL website = new URL("https://www.ipqualityscore.com/api/json/ip/"
					+ apiKey + "/" + ip
					+ "?strictness=1&allow_public_access_points=true");
			URLConnection connection = website.openConnection();
			String url = "";

			connection.setConnectTimeout(5000);
			connection.setRequestProperty("User-Agent", "Java-VPNDetection Library");

			try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				while ((url = in.readLine()) != null) {
					response.append(url);
				}

				in.close();
			}
			
			return (JSONObject) new JSONParser().parse(response.toString());
		} catch (Exception e) {
			return null;
		}

	}

}

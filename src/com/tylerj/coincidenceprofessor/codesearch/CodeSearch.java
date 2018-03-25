package com.tylerj.coincidenceprofessor.codesearch;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Represents a Code Search API Client.
 */
public class CodeSearch {
    private static final String CODE_SEARCH_ENDPOINT = "https://searchcode.com/api/codesearch_I/";
    private static final String USER_AGENT = "Mozilla/5.0";

    /**
     * Gets a List of source code matches to another source code file.
     * @param s The string to search.
     * @param languageId The id of the programing language you wish to use.
     * @param locationId The id of the location of the source code.
     * @return A list of source code matches.
     */
    public static ArrayList<String> getSimilarSourceCode(String s, int languageId, int locationId) {
        ArrayList<String> params = new ArrayList<>();
        params.add("q");
        params.add("lan");
        params.add("src");
        params.add("per_page");

        ArrayList<String> values = new ArrayList<>();
        values.add(URLEncoder.encode(s));
        values.add(String.valueOf(languageId));
        values.add(String.valueOf(locationId));
        values.add("100");

        String url = getUrlWithParameters(CODE_SEARCH_ENDPOINT, params, values);

        String response = getHTTPRequest(url);

        return getJSONFromString(response);
    }

    /**
     * Sends a HTTP GET request to a give URL.
     * @param url The url that the HTTP GET request uses.
     * @return The contents of the response of the HTTP GET request.
     */
    private static String getHTTPRequest(String url) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);

        request.addHeader("User-Agent", USER_AGENT);

        try {
            HttpResponse response = httpClient.execute(request);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer result = new StringBuffer();
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }

            return result.toString();

        } catch (IOException e) {
            e.printStackTrace();

            System.out.println("Error sending get request.");
        }

        return null;
    }

    /**
     * Gets a URL from a base url, parameters, and values.
     * @param base The base url i.e the endpoint.
     * @param parameters The API parameters.
     * @param values The API parameters values.
     * @return The string representing the final url.
     */
    private static String getUrlWithParameters(String base, ArrayList<String> parameters, ArrayList<String> values) {
        StringBuilder finalUrl = new StringBuilder(base + "?");

        for (int i = 0; i < parameters.size(); i++) {
            finalUrl.append("&" + parameters.get(i) + "=" + values.get(i));
        }

        return finalUrl.toString();
    }

    /**
     * Retrieves a list of source code files from a JSON string.
     * @param s1 A JSON string.
     * @return A list of source code files.
     */
    private static ArrayList<String> getJSONFromString(String s1) {
        JSONParser jsonParser = new JSONParser();

        try {
            Object object = jsonParser.parse(s1);

            JSONObject jsonObject = (JSONObject) object;

            JSONArray results = (JSONArray) jsonObject.get("results");

            ArrayList<String> files = new ArrayList<>();

            for (Object o : results) {
                JSONObject object1 = (JSONObject) o;
                JSONObject lines = (JSONObject) object1.get("lines");

                System.out.println("Object Name: " + ((JSONObject) o).toJSONString());

                StringBuilder stringBuilder = new StringBuilder();

                for (Object key : lines.keySet()) {
                    String keyStr = (String) key;
                    Object keyValue = lines.get(keyStr);

                    System.out.println("key: " + keyStr + " value: " + keyValue);

                    stringBuilder.append(keyValue + "\n");
                }

                files.add(stringBuilder.toString());
            }

            return files;

        } catch (ParseException e) {
            e.printStackTrace();

            System.out.println("Error converting String to JSON Object.");
        }

        return new ArrayList<>();
    }
}

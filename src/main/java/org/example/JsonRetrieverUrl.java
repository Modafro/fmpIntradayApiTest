package org.example;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Logger;

public class JsonRetrieverUrl {
    private static final Logger logger = Logger.getLogger(JsonRetrieverUrl.class.getName());

    public static String getJsonResponse(JsonApiInformation jsonApiInformation) throws Exception {
        try {
            URL url = new URL(jsonApiInformation.getUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);

            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            }
            StringBuilder jsonResponse = new StringBuilder();
            Scanner scanner = new Scanner(url.openStream());

            while (scanner.hasNext()) {
                jsonResponse.append(scanner.nextLine());
            }

            scanner.close();

            return jsonResponse.toString();
        } catch (IOException | RuntimeException e) {
            throw new Exception(e);
        }
    }
}
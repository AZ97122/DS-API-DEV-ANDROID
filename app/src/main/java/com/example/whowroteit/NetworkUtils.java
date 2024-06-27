package com.example.whowroteit;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    // URL de base pour l'API Books
    private static final String BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
    // Paramètre pour la chaîne de recherche
    private static final String QUERY_PARAM = "q";
    // Paramètre qui limite les résultats de la recherche
    private static final String MAX_RESULTS = "maxResults";
    // Paramètre pour filtrer par type de publication
    private static final String PRINT_TYPE = "printType";

    /**
     * Méthode statique pour obtenir des informations sur un livre
     * @param queryString terme de recherche
     * @return JSON de réponse sous forme de chaîne
     */
    static String getBookInfo(String queryString) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJSONString = null;

        try {
            // Construction de l'URI de la requête
            Uri builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, queryString)
                    .appendQueryParameter(MAX_RESULTS, "10")
                    .appendQueryParameter(PRINT_TYPE, "books")
                    .build();

            // Conversion de l'URI en URL
            URL requestURL = new URL(builtURI.toString());

            // Ouverture de la connexion HTTP
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Récupère le InputStream
            InputStream inputStream = urlConnection.getInputStream();

            // Crée un lecteur tamponné à partir de ce flux d'entrée
            reader = new BufferedReader(new InputStreamReader(inputStream));

            // Utilisez un StringBuilder pour contenir la réponse entrante
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }

            // Vérifie la chaîne pour voir s'il existe un contenu de réponse existant
            if (builder.length() == 0) {
                return null;
            }

            bookJSONString = builder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Écrit la réponse JSON finale dans le journal
        Log.d(LOG_TAG, bookJSONString);
        return bookJSONString;
    }
}

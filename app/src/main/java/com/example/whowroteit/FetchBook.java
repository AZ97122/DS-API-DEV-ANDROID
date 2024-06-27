package com.example.whowroteit;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class FetchBook extends AsyncTask<String, Void, String> {
    private WeakReference<TextView> mTitleText;
    private WeakReference<TextView> mAuthorText;

    FetchBook(TextView titleText, TextView authorText) {
        this.mTitleText = new WeakReference<>(titleText);
        this.mAuthorText = new WeakReference<>(authorText);
    }

    @Override
    protected String doInBackground(String... strings) {
        return NetworkUtils.getBookInfo(strings[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            // Convertit la réponse en objet JSON
            JSONObject jsonObject = new JSONObject(s);
            // Récupère le JSONArray des éléments du livre
            JSONArray itemsArray = jsonObject.getJSONArray("items");

            // Initialise les champs de l'itérateur et des résultats
            int i = 0;
            String title = null;
            String authors = null;

            // Recherche les résultats dans le tableau des éléments
            while (i < itemsArray.length() && (authors == null && title == null)) {
                // Récupère les informations sur l'élément actuel
                JSONObject book = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                try {
                    title = volumeInfo.getString("title");
                    authors = volumeInfo.getString("authors");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Passe à l'élément suivant
                i++;
            }

            // Si les deux sont trouvés, affiche le résultat
            if (title != null && authors != null) {
                mTitleText.get().setText(title);
                mAuthorText.get().setText(authors);
            } else {
                // Si aucun n'est trouvé, mettez à jour l'interface utilisateur pour afficher les résultats ayant échoué
                mTitleText.get().setText(R.string.no_results);
                mAuthorText.get().setText("");
            }
        } catch (JSONException e) {
            // Si onPostExecute ne reçoit pas une chaîne JSON appropriée
            mTitleText.get().setText(R.string.no_results);
            mAuthorText.get().setText("");
            e.printStackTrace();
        }
        super.onPostExecute(s);
    }
}

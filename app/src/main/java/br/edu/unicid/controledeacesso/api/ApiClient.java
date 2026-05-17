package br.edu.unicid.controledeacesso.api;

import android.content.Context;
import android.content.SharedPreferences;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class ApiClient {

    private static final String PREFS_NAME = "app_config";
    private static final String KEY_SERVER_IP = "server_ip";

    private static ApiService instance;
    private static String lastBaseUrl;

    /** Retorna a URL base salva (ou o padrão compilado). */
    public static String getBaseUrl(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_SERVER_IP, Constants.BASE_URL);
    }

    /** Salva um novo IP e invalida o cliente atual para ser recriado. */
    public static void setBaseUrl(Context ctx, String url) {
        // garante que termina com "/"
        if (!url.endsWith("/")) url = url + "/";
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_SERVER_IP, url).apply();
        instance = null; // força recriação com nova URL
    }

    public static ApiService get(Context ctx) {
        String baseUrl = getBaseUrl(ctx);
        if (instance == null || !baseUrl.equals(lastBaseUrl)) {
            lastBaseUrl = baseUrl;
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build();

            instance = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService.class);
        }
        return instance;
    }

    /** Sobrecarga sem Context — usa a última instância criada (para callbacks). */
    public static ApiService get() {
        if (instance == null) {
            throw new IllegalStateException("Chame ApiClient.get(context) pelo menos uma vez antes.");
        }
        return instance;
    }
}

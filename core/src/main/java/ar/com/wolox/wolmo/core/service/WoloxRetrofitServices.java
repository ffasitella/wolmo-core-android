package ar.com.wolox.wolmo.core.service;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.util.HashMap;
import java.util.Map;

import ar.com.wolox.wolmo.core.service.interceptor.SecuredRequestInterceptor;
import ar.com.wolox.wolmo.core.service.serializer.GsonBuilder;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public abstract class WoloxRetrofitServices {

    private Retrofit mRetrofit;
    private Retrofit mApiaryRetrofit;
    private Map<Class, Object> mServices;

    public void init() {
        mServices = new HashMap<>();
        Gson gson = GsonBuilder.getBasicGsonBuilder().create();
        OkHttpClient client = new SecuredRequestInterceptor();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client.interceptors().add(interceptor);
        mRetrofit = new Retrofit.Builder()
                .baseUrl(getApiEndpoint())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
        mApiaryRetrofit = new Retrofit.Builder()
                .baseUrl(getApiaryEndpoint())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
    }

    public abstract String getApiEndpoint();

    public abstract String getApiaryEndpoint();

    public <T> T getApiaryService(Class<T> clazz) {
        T service = (T) mServices.get(clazz);
        if (service != null) return service;
        service = mApiaryRetrofit.create(clazz);
        mServices.put(clazz, service);
        return service;
    }

    public <T> T getService(Class<T> clazz) {
        T service = (T) mServices.get(clazz);
        if (service != null) return service;
        service = mRetrofit.create(clazz);
        mServices.put(clazz, service);
        return service;
    }

    /* Override this class and define the services like this:
     *
     * public static UserService user() {
     *  return getService(UserService.class);
     * }
     *
     */
}
/**
 * A HTTP plugin for Cordova / Phonegap
 */
package com.synconset;

import com.github.kevinsawicki.http.HttpRequest;
import org.apache.cordova.CallbackContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class CordovaHttp {
    protected static final String TAG = "CordovaHTTP";
    protected static final String CHARSET = "UTF-8";

    private static AtomicBoolean sslPinning = new AtomicBoolean(false);
    private static AtomicBoolean acceptAllCerts = new AtomicBoolean(false);

    private String urlString;
    private JSONObject params;
    private Map<?, ?> paramsMap;
    private Map<String, String> headers;
    private CallbackContext callbackContext;

    public CordovaHttp(String urlString, JSONObject params, Map<String, String> headers, CallbackContext callbackContext) throws JSONException {
        this.urlString = urlString;
        this.params = params;
        this.paramsMap = getMapFromJSONObject(params);
        this.headers = headers;
        this.callbackContext = callbackContext;
    }

    public static void enableSSLPinning(boolean enable) {
        sslPinning.set(enable);
        if (enable) {
            acceptAllCerts.set(false);
        }
    }

    public static void acceptAllCerts(boolean accept) {
        acceptAllCerts.set(accept);
        if (accept) {
            sslPinning.set(false);
        }
    }

    protected String getUrlString() {
        return this.urlString;
    }

    protected JSONObject getParams() {
        return this.params;
    }

    protected Map<?, ?> getParamsMap() {
        return this.paramsMap;
    }

    protected Map<String, String> getHeaders() {
        return this.headers;
    }

    protected CallbackContext getCallbackContext() {
        return this.callbackContext;
    }

    protected HttpRequest setupSecurity(HttpRequest request) {
        if (acceptAllCerts.get()) {
            request.trustAllCerts();
            request.trustAllHosts();
        }
        if (sslPinning.get()) {
            request.pinToCerts();
        }
        return request;
    }

    protected void respondWithError(int status, String msg) {
        try {
            JSONObject response = new JSONObject();
            response.put("status", status);
            response.put("error", msg);
            this.callbackContext.error(response);
        } catch (JSONException e) {
            this.callbackContext.error(msg);
        }
    }

    protected void respondWithError(String msg) {
        this.respondWithError(500, msg);
    }

    private HashMap<String, Object> getMapFromJSONObject(JSONObject object) throws JSONException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        Iterator<?> i = object.keys();

        while (i.hasNext()) {
            String key = (String) i.next();
            map.put(key, object.get(key));
        }
        return map;
    }
}

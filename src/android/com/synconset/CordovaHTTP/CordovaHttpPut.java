/**
 * A HTTP plugin for Cordova / Phonegap
 */
package com.synconset;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;
import org.apache.cordova.CallbackContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.Map;

public class CordovaHttpPut extends CordovaHttp implements Runnable {
    public CordovaHttpPut(String urlString, JSONObject params, Map<String, String> headers, CallbackContext callbackContext) throws JSONException {
        super(urlString, params, headers, callbackContext);
    }

    @Override
    public void run() {
        try {
            HttpRequest request = HttpRequest.put(this.getUrlString());
            this.setupSecurity(request);
            request.acceptCharset(CHARSET);
            request.headers(this.getHeaders());
            if (this.getHeaders().containsValue("application/json")
                    || this.getHeaders().containsValue("Application/Json")
                    || this.getHeaders().containsValue("Application/JSON")) {
                request.send(this.getParams().toString());
            } else {
                request.form(this.getParamsMap());
            }
            int code = request.code();
            String body = request.body(CHARSET);
            JSONObject response = new JSONObject();
            response.put("status", code);
            if (code >= 200 && code < 300) {
                response.put("data", body);
                this.getCallbackContext().success(response);
            } else {
                response.put("error", body);
                this.getCallbackContext().error(response);
            }
        } catch (JSONException e) {
            this.respondWithError("There was an error generating the response");
        } catch (HttpRequestException e) {
            if (e.getCause() instanceof UnknownHostException) {
                this.respondWithError(0, "The host could not be resolved");
            } else {
                this.respondWithError("There was an error with the request");
            }
        }
    }
}

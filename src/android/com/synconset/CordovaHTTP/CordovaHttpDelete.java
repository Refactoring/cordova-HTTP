/**
 * A HTTP plugin for Cordova / Phonegap
 */
package com.synconset;

import java.net.UnknownHostException;
import java.util.Map;

import org.apache.cordova.CallbackContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;
 
public class CordovaHttpDelete extends CordovaHttp implements Runnable {
    public CordovaHttpDelete(String urlString, Map<?, ?> params, Map<String, String> headers, String jsonData, CallbackContext callbackContext) {
        super(urlString, params, headers, jsonData, callbackContext);
    }
    
    @Override
    public void run() {
        try {
            HttpRequest request = HttpRequest.delete(this.getUrlString());
            this.setupSecurity(request);
            request.acceptCharset(CHARSET);
            request.headers(this.getHeaders());
            if(null == this.getJSONData() || this.getJSONData().equals("")){
              request.form(this.getParams());
            }else{
              request.send(this.getJSONData());
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
        }  catch (HttpRequestException e) {
            if (e.getCause() instanceof UnknownHostException) {
                this.respondWithError(0, "The host could not be resolved");
            } else {
                this.respondWithError("There was an error with the request");
            }
        }
    }
}
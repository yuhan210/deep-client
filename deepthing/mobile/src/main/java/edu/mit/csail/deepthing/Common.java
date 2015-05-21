package edu.mit.csail.deepthing;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by tiffany on 5/21/15.
 */
public class Common {

    /**
     * Turns the response of an HTTP request into a JSON Object.
     *
     * @param response
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public static JSONObject getJsonFromResponse(HttpResponse response) throws IOException, JSONException {
        HttpEntity entity = response.getEntity();
        String entityContent = EntityUtils.toString(entity);
        return new JSONObject(entityContent);
    }
}

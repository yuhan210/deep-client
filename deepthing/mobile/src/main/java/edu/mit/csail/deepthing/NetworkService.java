package edu.mit.csail.deepthing;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by tiffany on 5/21/15.
 */
public class NetworkService {

    private static final String TAG = "NetworkService";
    private static final String SERVER_URL = "http://lenin01.cloudapp.net:5000";
    private static final int PORT = 5000;

    // HttpClient used for the requests.
    private final DefaultHttpClient mHttpClient;

    public NetworkService(){
        mHttpClient = new DefaultHttpClient();
    }

    public JSONObject CreateRequest(String img_path) throws JSONException {
        JSONObject requestObject = new JSONObject();
        Bitmap bitmap = BitmapFactory.decodeFile(img_path);
        String bitmapBase64 = Utility.getBase64(bitmap, Bitmap.CompressFormat.JPEG, 70);
        // should use userid for scaling
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        requestObject.put("image", bitmapBase64);
        requestObject.put("filename", timeStamp + ".jpg");

        return requestObject;
    }

    public JSONObject Post(JSONObject requestObj) {

        HttpPost httpPost = new HttpPost(SERVER_URL + "/android_post");

        if(requestObj != null) {
            String jsonObjectString = requestObj.toString();

            try {
                httpPost.setEntity(new StringEntity(jsonObjectString, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "Unable to encode JSON data", e);
                    //throw new RestClientException("Unable to encode the JSON Data", e, jsonObjectString);
            }
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
        }
        return executeHttpMethod(httpPost);
    }

    /**
     * This method executes the httpMethod and checks the response. It also evaluates the Status field
     * sent by the Web service.
     *
     * @param httpMethod
     * @return
     */
    private JSONObject executeHttpMethod(final HttpRequestBase httpMethod){

        JSONObject resultJson = null;
        try {
            HttpResponse httpResponse = mHttpClient.execute(httpMethod);
            int httpStatusCode = httpResponse.getStatusLine().getStatusCode();
            if(httpStatusCode != HttpStatus.SC_OK) {
                checkHttpStatus(httpMethod, httpStatusCode);
            }
            resultJson = Common.getJsonFromResponse(httpResponse);

        } catch(IOException e) {
            Log.e(TAG, "Unable to execute HTTP Method.", e);
        } catch (JSONException e) {
            Log.e(TAG, "There was an error decoding the JSON Object.", e);
         } finally {
            httpMethod.abort();
        }
        return resultJson;
    }

    /**
     * Checks the HttpStatus code of the Response and throws an appropriate exception, the upper
     * layer is able to catch the exception and notify the user.
     *
     * @param httpMethod
     * @param httpStatusCode
     *
     */
    private void checkHttpStatus(HttpRequestBase httpMethod, int httpStatusCode) {
        String requestedPath = httpMethod.getURI().toString();
        if(httpStatusCode == HttpStatus.SC_FORBIDDEN) {
            Log.e(TAG, "Access denied.");
           // throw new AccessDeniedException(requestedPath);
        }
        if(httpStatusCode == HttpStatus.SC_NOT_FOUND) {
            Log.e(TAG, "Resource not found.");
           // throw new ResourceNotFoundException(requestedPath);
        }
        if(httpStatusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            Log.e(TAG, "Internal Server Error.");
          //  throw new InternalServerErrorException(requestedPath);
        }
    }


}

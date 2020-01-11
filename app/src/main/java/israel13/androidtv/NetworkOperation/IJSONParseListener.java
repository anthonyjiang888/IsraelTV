
package israel13.androidtv.NetworkOperation;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Interface for responses
 * 
 * @author DearDhruv
 */
public interface IJSONParseListener {

	// Interface methods for JSON Responses

	/**
	 * Invoked when any network failure or JSON parsing failure.
	 * 
	 * @param error
	 * @param requestCode
	 */
	void ErrorResponse(VolleyError error, int requestCode);

	/**
	 * Invoked when successful response and successful JSON parsing completed.
	 * 
	 * @param response
	 * @param requestCode
	 */
	void SuccessResponse(JSONObject response, int requestCode);

	void SuccessResponseArray(JSONArray response, int requestCode);


	void SuccessResponseRaw(String response, int requestCode);

}

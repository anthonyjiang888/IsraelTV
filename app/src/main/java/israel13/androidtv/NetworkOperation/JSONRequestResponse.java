package israel13.androidtv.NetworkOperation;

/**
 * Created by sudipta on 28/6/16.
 */

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import israel13.androidtv.R;

/**
 * Common class for requesting network query or uploading file.
 *
 * @author DearDhruv
 */
public class JSONRequestResponse {

	static int i = 0;

	public JSONRequestResponse(Context cntx) {
		mContext = cntx;
	}

	@SuppressWarnings("unused")
	private final Context mContext;
	private int reqCode;
	private IJSONParseListener listner;

	private boolean isFile = false;
	private String file_path = "", key = "";

	public void getResponse(int method, String url, final int requestCode, IJSONParseListener mParseListener, Boolean uploadAsBytedata) {
		getResponse(method, url, requestCode, mParseListener, null, uploadAsBytedata);
	}

	public void getResponse(int method, String url, final int requestCode, IJSONParseListener mParseListener, final Bundle paramslist, Boolean uploadAsBytedata) {
		this.listner = mParseListener;
		this.reqCode = requestCode;

		if (method == Request.Method.GET) {
			url = url + "?";
			for (String key : paramslist.keySet()) {
				Object value = paramslist.get(key);
				if (value != null && value instanceof String) {
					try {
						value = URLEncoder.encode((String) value, "UTF-8");
					} catch (Exception ex){}
				}
				url += key + "=" + value + "&";
			}
			url = url.substring(0, url.length() - 1);
		}

		if (!isFile) {

			StringRequest strRequest = new StringRequest(method, url, new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					if (listner != null) {
						try {
							JSONObject jObj = new JSONObject(response);
							listner.SuccessResponse(jObj, reqCode);
						} catch (Exception e) {
							try {
								JSONArray jObj = new JSONArray(response);
								listner.SuccessResponseArray(jObj, reqCode);
							} catch (Exception ee) {
								listner.SuccessResponseRaw(response, reqCode);
							}
						}
					}
				}
			},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							System.out.println("error:::::" + error.toString());
							if (listner != null) {
								listner.ErrorResponse(error, reqCode);
							}

							try {
								String responseBody = new String(error.networkResponse.data, "utf-8");
								JSONObject jsonObject = new JSONObject(responseBody);
							} catch (JSONException e) {
								//Handle a malformed json response
								e.printStackTrace();
							} catch (UnsupportedEncodingException errorr) {
								errorr.printStackTrace();

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}) {

				@Override
				public Map<String, String> getHeaders() {
					String userAgent = "";
					String appname = "";
					String version_name = "";
                    int version_code = 0;
					try {

						userAgent = System.getProperty("http.agent");

						PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
						version_name = pInfo.versionName;
                        version_code = pInfo.versionCode;
						appname = mContext.getResources().getString(R.string.app_name);
					} catch (Exception e) {
						e.printStackTrace();
					}

					String versionInfo = " android.tv-version." + version_name + "-build-" + String.valueOf(version_code);
					userAgent = userAgent + versionInfo;

					Map<String, String> headers = new HashMap<String, String>();
					headers.put("User-agent", userAgent);

					// Add cookie in header
					if (paramslist.containsKey("cookiekey")) {
						String cookieKey = (String) paramslist.get("cookiekey");
						headers.put("x-fingerprint", cookieKey);
						paramslist.remove("cookiekey");
					}

					System.out.println("USER AGENT:::" + userAgent);
					return headers;
				}

				@Override
				protected Map<String, String> getParams() {
					Map<String, String> params = new HashMap<String, String>();
					for (String key : paramslist.keySet()) {
						Object value = paramslist.get(key);
						params.put(key, value.toString());
					}
					return params;
				}
			};

			strRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
			strRequest.setShouldCache(false);
			MyVolley.getRequestQueue().getCache().clear();
			MyVolley.getRequestQueue().add(strRequest);
		} else {

			if (uploadAsBytedata) {


				VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(method, url, new Response.Listener<NetworkResponse>() {
					@Override
					public void onResponse(NetworkResponse response) {

						try {
							String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
							JSONObject jObj = new JSONObject(json);
							listner.SuccessResponse(jObj, reqCode);
						} catch (Exception e) {

						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (listner != null) {
							listner.ErrorResponse(error, reqCode);
						}
					}
				}) {
					@Override
					protected Map<String, String> getParams() {
						Map<String, String> params = new HashMap<>();
						for (String key : paramslist.keySet()) {
							Object value = paramslist.get(key);
							params.put(key, value.toString());
						}


						return params;
					}

					@Override
					protected Map<String, DataPart> getByteData() {
						Map<String, DataPart> params = new HashMap<>();
						// file name could found file base or direct access from real path
						// for now just get bitmap data from ImageView
						File mFile = new File(file_path);
						byte[] data = null;
						try {
							data = readFile(mFile);
						} catch (Exception e) {

						}
						String fileName = file_path.substring(file_path.lastIndexOf('/') + 1);
						params.put(key, new DataPart(fileName, data, "image/jpeg"));


						return params;
					}
				};

				MyVolley.getRequestQueue().add(multipartRequest);

			} else {


				Response.Listener<String> sListener = new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						if (listner != null) {
							try {
								listner.SuccessResponse(new JSONObject(response), reqCode);
							} catch (Exception e) {

							}
						}
					}
				};

				Response.ErrorListener eListener = new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (listner != null) {
							listner.ErrorResponse(error, reqCode);
						}
					}
				};


				if (file_path != null) {
					File mFile = new File(file_path);
					MultipartRequest multipartRequest = new MultipartRequest(url, mFile, key, sListener, eListener, paramslist);
					multipartRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
					MyVolley.getRequestQueue().add(multipartRequest);
				} else {
					throw new NullPointerException("File path is null");
				}
			}
		}
	}

	/**
	 * @return the isFile
	 */
	public boolean isFile() {
		return isFile;
	}

	/**
	 * @param isFile the File to set
	 */
	public void setFile(String param, String path) {
		if (path != null && param != null) {
			key = param;
			file_path = path;
			this.isFile = true;
		}
	}

	static byte[] readFile(File file) throws IOException {
		// Open file
		RandomAccessFile f = new RandomAccessFile(file, "r");
		try {
			// Get and check length
			long longlength = f.length();
			int length = (int) longlength;
			if (length != longlength)
				throw new IOException("File size >= 2 GB");
			// Read file and return data
			byte[] data = new byte[length];
			f.readFully(data);
			return data;
		} finally {
			f.close();
		}
	}

}
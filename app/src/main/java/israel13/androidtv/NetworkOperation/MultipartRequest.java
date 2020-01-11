package israel13.androidtv.NetworkOperation;

/**
 * Created by sudipta on 28/6/16.
 */
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class MultipartRequest extends Request<String> {


    private HttpEntity mHttpEntity;
    private Response.Listener mListener;



    public MultipartRequest(String url, File file, String filename, Response.Listener<String> listener, Response.ErrorListener errorListener, Bundle paramlist) {
        super(Method.POST, url, errorListener);
        mListener = listener;
        mHttpEntity = buildMultipartEntity(file,filename,paramlist);
    }


    private HttpEntity buildMultipartEntity(File file, String filename, Bundle paramlist) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        FileBody fileBody = new FileBody(file);
        builder.addPart(filename, fileBody);


        for (String key : paramlist.keySet()) {
            Object value = paramlist.get(key);
            builder.addTextBody(key, value.toString());
        }

        return builder.build();
    }

    @Override
    public String getBodyContentType() {
        return mHttpEntity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            mHttpEntity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            try {
                String jsonString = new String(response.data,
                        HttpHeaderParser.parseCharset(response.headers));
                return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            } catch (Exception je) {
                return Response.error(new ParseError(je));
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }
}
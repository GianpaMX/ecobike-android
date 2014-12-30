package mx.softux.ecobike;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by gianpa on 12/30/14.
 */
public class CacheUtilities {
    private static final String TAG = CacheUtilities.class.getSimpleName();

    public static final String CHARSET_NAME = "UTF-8";

    Context context;

    public CacheUtilities(Context context) {
        this.context = context;
    }

    public File getCacheFile(String filename) {
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null)
            cacheDir = context.getCacheDir();

        return new File(cacheDir, filename);
    }

    public JSONObject readJsonObject(File file) {
        JSONObject jsonObject = null;
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));

            BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, CHARSET_NAME));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            streamReader.close();

            String jsonString = responseStrBuilder.toString();
            Log.d(TAG, "Read from " + file.getAbsolutePath());
            Log.d(TAG, jsonString);
            jsonObject = new JSONObject(jsonString);
        } catch (Exception e) {
            Log.e(TAG, "Reading file", e);
        }

        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Closing file", e);
        }
        return jsonObject;
    }

    public void writeJsonObject(File file, JSONObject jsonObject) {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out, CHARSET_NAME);

            String jsonString = jsonObject.toString();
            Log.d(TAG, "Write to " + file.getAbsolutePath());
            Log.d(TAG, jsonString);
            outputStreamWriter.write(jsonString);
            outputStreamWriter.close();
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }

        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Closing file", e);
        }
    }
}

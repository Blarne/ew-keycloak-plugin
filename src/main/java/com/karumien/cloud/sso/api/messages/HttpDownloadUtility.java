package com.karumien.cloud.sso.api.messages;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A utility that downloads a file from a URL.
 * 
 * @author www.codejava.net
 *
 */
public class HttpDownloadUtility {

    private static final int BUFFER_SIZE = 4096;

    /**
     * Downloads a file from a URL
     * 
     * @param fileURL
     *            HTTP URL of the file to be downloaded
     * @param saveDir
     *            path of the directory to save the file
     * @throws IOException
     */
    public static String request(String method, String fileURL, String data, String saveDir, String newFileName) throws IOException {

        URL url = new URL(fileURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        if ("POST".contentEquals(method)) {
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            
            conn.setRequestMethod(method);
         
            if (data!=null) {
                OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                out.write(data);
                out.close();
            }
        }
        
        int responseCode = conn.getResponseCode();
        
        // always check HTTP response code first
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IllegalStateException("No file to download. Server replied HTTP code: " + responseCode);
        }

        String fileName = "";
        String disposition = conn.getHeaderField("Content-Disposition");

        if (disposition != null) {
            // extracts file name from header field
            int index = disposition.indexOf("filename=");
            if (index > 0) {
                fileName = disposition.substring(index + 10, disposition.length() - 1);
            }
        } else {
            // extracts file name from URL
            fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
        }

        // opens input stream from the HTTP connection
        InputStream inputStream = conn.getInputStream();
        OutputStream outputStream;

        if (newFileName != null) {
            fileName = newFileName;
            String saveFilePath = saveDir + File.separator + fileName;

            // opens an output stream to save into file
            outputStream = new FileOutputStream(saveFilePath);

        } else {
            outputStream = new ByteArrayOutputStream();
        }

        int bytesRead = -1;
        byte[] buffer = new byte[BUFFER_SIZE];
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        outputStream.close();
        inputStream.close();

        conn.disconnect();

        return newFileName != null ? null : new String(((ByteArrayOutputStream) outputStream).toByteArray());

    }
}
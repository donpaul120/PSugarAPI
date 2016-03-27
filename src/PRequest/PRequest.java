/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Paulex Open Source Project
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package PRequest;



import Interfaces.ResponseHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Okeke Paul
 *
 * Created by paulex on 25/03/16.
 */
public class PRequest {


    public static void get(){

    }

    public static void post(String url, HashMap<String, String> postData, ResponseHandler handler){
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(outputStream));
            bf.write(buildParams(postData));
            bf.flush(); bf.close();
            outputStream.close();
            outputStream.close();//It is really important we close the output stream and also flush the writer
            urlConnection.connect();
            handler.onSuccessful(handleResponse(urlConnection));
        }catch (IOException ex){
            ex.printStackTrace();
        }finally {
            if(urlConnection!=null){
                urlConnection.disconnect();
            }
        }
    }

    public static String handleResponse(HttpURLConnection urlConnection) throws IOException{
        int status = urlConnection.getResponseCode();
        if(status!=200){
            //we should manage the response here.. in the future..
            //no time :(
        }
        return getResponseData(urlConnection);
    }

    public static String getResponseData(HttpURLConnection urlConnection) throws IOException{
        BufferedReader bReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        String line;
        while((line=bReader.readLine())!=null){
            line += newLine;
            builder.append(line);
        }
        bReader.close();
        return  builder.toString();
    }

    /**
     * We use this method to build our Parameters for post request
     * @param data the post data
     * @return String
     */
    private static String buildParams(HashMap<String, String> data){
        StringBuilder builder = new StringBuilder();
        boolean first = true;// use to track when we need to append the &
        for(Map.Entry<String, String> entry : data.entrySet()){
            if(first) first = false; else builder.append("&");
            try {
                builder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                builder.append("=");
                builder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }catch (UnsupportedEncodingException uex){
                uex.printStackTrace();
            }
        }
//        System.out.println(builder.toString());
//        System.err.println(builder.toString());
        return builder.toString();
    }
}

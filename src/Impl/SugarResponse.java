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

package Impl;

import Interfaces.ResponseHandler;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * @author Okeke Paul
 * <p>
 *     This class is used to build a response data.
 *     An instance of this class is required by the sugarAPI on every
 *     request. The SugarResponse also provides other helper methods to convert
 *     the response to easy and accessible data.
 * </p>
 * Created by paulex on 26/03/16.
 */
public class SugarResponse implements Callable<String>, ResponseHandler{

    private String responseData;
    private JsonParser parser = new JsonParser();

//    //We use this to restrict the type of conversionTypes

    private void setData(String responseData){
        this.responseData = responseData;
    }

    @Deprecated
    public String getResponseData() {
        return responseData;
    }

    /**
     * Use this method to get the raw JsonObject responseData
     * @return {JsonObject}
     */
    public JsonObject getJsonResponseData(){
        JsonObject object = null;
        try{
            JsonParser parser = new JsonParser();
            object = parser.parse(this.responseData).getAsJsonObject();
        }catch (Exception ex){
            ex.printStackTrace();
            System.out.println("Invalid Json Response : ");
            System.err.println(this.responseData);
        }
        return object;
    }

    /**
     *
     * @param conversionType {enum|ConversionType} use to specify what type of response is been expected from sugar
     *               e.g for get_entry its reasonable to expect a single object, thus
     *               this method helps to easily re-order the keys and value for quick retrieval
     * @return {Object|JsonObject|JsonArray}
     */
    public Object getJsonObjectResponse(ConversionType conversionType){
        JsonObject responseObject = parser.parse(this.responseData).getAsJsonObject();
        if(conversionType==ConversionType.DEFAULT) {
            return responseObject;
        }else if(conversionType==ConversionType.SINGLE){
            return  makeJsonObject(responseObject);
        }else if(conversionType==ConversionType.LIST){
            return makeListJsonObject(responseObject);
        }else{
            throw new IllegalArgumentException("Parameter conversionType must be either of SugarResponse.DEFAULT," +
                    " SugarResponse.SINGLE or SugarResponse.LIST");
        }
    }

    /**
     * Internally used to convert sugar get_entry response json data to a more retrievable
     * Object.. The returned Object should look more easy to manipulate.. e.g {id:"", field_name:value}
     * @param responseObject {JsonObject} the responseObject from Sugar
     * @return {JsonObject} returnObject
     */
    private JsonObject makeJsonObject(@NotNull JsonObject responseObject){
        if(responseObject.has("result_count"))
            throw new UnsupportedOperationException("You can't make a SINGLE Object from a responseObject " +
                    "that holds list of Objects. Specify the correct conversionType. " +
                    "Using SugarResponse.LIST might solve the problem"
            );
        //this can be a login session response... so lets just return the data
        if(!responseObject.has("entry_list")) return responseObject;
        JsonArray entryList = responseObject.getAsJsonArray("entry_list");
        JsonObject entry = entryList.get(0).getAsJsonObject();//since its a single object let retrieve the first index
        JsonObject returnObject = new JsonObject();
        Set<Map.Entry<String, JsonElement>> entries = entry.entrySet();
        getNameValueListObjectsKeyValues(entries, returnObject);
        return returnObject;
    }

    /**
     *
     * @param responseObject {JsonObject}
     * @return {JsonArray} returnArray
     */
    private JsonArray makeListJsonObject(@NotNull JsonObject responseObject){
        if(!responseObject.has("relationship_list")) throw
                new UnsupportedOperationException("Can't create a list of objects from the responseObject received." +
                        "Specifying SugarResponse.SINGLE or SugarResponse.DEFAULT might might solve the problem");
        JsonArray entryList = responseObject.getAsJsonArray("entry_list");
        JsonArray returnArray = new JsonArray();
        for(JsonElement element: entryList){
            JsonObject record = new JsonObject();
            Set<Map.Entry<String, JsonElement>> entries = element.getAsJsonObject().entrySet();
            getNameValueListObjectsKeyValues(entries, record);
            returnArray.add(record);
        }
        return returnArray;
    }

    public void getNameValueListObjectsKeyValues(@NotNull Set<Map.Entry<String, JsonElement>> fromEntries,
                                                 @NotNull JsonObject toObject){
        for (Map.Entry<String, JsonElement> entry :  fromEntries){
            if(!entry.getValue().isJsonObject() && !entry.getValue().isJsonArray())
                toObject.addProperty(entry.getKey(), entry.getValue().getAsString());
            //possibly the named_value_list
            if(entry.getValue().isJsonObject())
                addJsonObjectKeyValues(entry.getValue().getAsJsonObject(), toObject);
        }
    }

    /**
     * 27th-03-2016
     * This is mostly used to retrieve named_value_list key and values
     * @param fromObject {JsonObject} The object to which we want to retrieve values from | named_value_list object
     * @param toObject {JsonObject} The Object to which we want to add the values to
     */
    private void addJsonObjectKeyValues(@NotNull JsonObject fromObject, @NotNull JsonObject toObject){
        Set<Map.Entry<String, JsonElement>> fields = fromObject.entrySet();
        for(Map.Entry<String, JsonElement> field : fields){
            toObject.addProperty(field.getKey(), field.getValue().getAsJsonObject().get("value").getAsString());
        }
    }



    @Override
    public String call() throws Exception {
        //lets free unused objects
        System.gc();
        return null;
    }

    @Override
    public void onSuccessful(String data) {
        setData(data);
        try {
            call();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onFailed(String errorMessage) {

    }
}

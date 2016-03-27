/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Paulex Open Source Project
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use copy, modify,
 * merge, publis    h, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package Impl;


import Interfaces.SugarRequestInterface;
import PRequest.PRequest;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Okeke Paul
 * <p>
 * This is a demo implementation of sugarCRM Api
 * with Java. The Full sugarCRM API methods aren't fully
 * available in this release but we will continually improve
 * on the API
 *</p>
 *
 * Created by paulex on 25/03/16.
 */
public class SugarAPI implements SugarRequestInterface {

    public final static int SET_ENTRY = 1;
    public final static int GET_ENTRY = 2;
    public final static int GET_ENTRY_LIST = 3;
    public final static int GET_ENTRIES = 4;
    public final static int SET_ENTRIES = 5;

    private String crmUrl;
    private String sessionID;
    private HashMap<String, String> dataTemp;
    private JsonParser parser;

    private SugarAPI(){}//lets prevent direct initialization

    //factory method
    public static SugarRequestInterface getSugarAPIInstance(String crmUrl){
        return new SugarAPI(crmUrl);
    }

    private SugarAPI(String crmUrl){
        this.crmUrl =  crmUrl;
        dataTemp = new HashMap<>();
        parser = new JsonParser();//use this parser
    }

    /**
     * @param username The SugarCRM Username
     * @param password {String} The sugarCRM user password
     *                 Note: that the password should be hashed with
     *                 MD5 algorithm
     */
    @Override
    public void login(String username, String password, SugarResponse callback){
        JsonObject userAuth = new JsonObject();
        JsonObject userAuthParams = new JsonObject();
        userAuthParams.addProperty("user_name", username);
        userAuthParams.addProperty("password", password);
        userAuth.add("user_auth", userAuthParams);
        PRequest.post(this.crmUrl, buildSugarData("login", userAuth.toString()), callback);
    }

    public String getSessionID() {
        return sessionID;
    }

    /**
     * Okeke Paul
     * It is important that after login-in this method should be called
     * by the user/client, to set the session id for sub-sequent request.
     * SugarAPI wouldn't set this value automatically for you
     * @param sessionID {String} The sugarCRM sessionID
     */
    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    /**
     * Use this method to get a single record. This is a wrapper for the get_entry method
     * @param moduleName {String} The name of the module to query or plainly the table name
     * @param data {HashMap} The post data to be sent.. this is the value of rest_data @see sugarCRM documentation.
     * @param callback {SugarResponse} the callback method that handles the server response.
     */
    @Override
    public void getRecord(@NotNull String moduleName, @NotNull  HashMap<String, String> data,
                          @NotNull SugarResponse callback) {
        PRequest.post(this.crmUrl, buildSugarData("get_entry",
                buildSugarParams(GET_ENTRY, moduleName, data, null)), callback);
    }

    /**
     * @apiNote Use <b>fields</b> to represent the list of columns you want selected
     *          Use <b>query</b> to represent your query. Mostly for your where statements, note that your where
     *          Queries should be without the where clause. @see sugarcrm documentation for more information
     *          Use <b>max</b> to represent the max_results of records to be returned by sugar
     *          Use <b>extras</b> as a wrapper for link_name_to_fields_array, which is use to specify corresponding
     *          table records related to the record queried for
     * @param moduleName {String} The name of the module to query or plainly the table name
     * @param data {HashMap} The post data to be sent.. this is the value of rest_data @see sugarCRM documentation.
     * @param callback {SugarResponse} the callback method that handles the server response.
     */
    @Override
    public void getRecords(@NotNull String moduleName, @NotNull  HashMap<String, String> data,
                           @NotNull SugarResponse callback) {
        PRequest.post(this.crmUrl, buildSugarData("get_entry_list",
                buildSugarParams(GET_ENTRY_LIST, moduleName, data, null)), callback);
    }


    @Override
    public void getMultipleRecords(@NotNull String moduleName, @NotNull HashMap<String, String> data,
                                   @NotNull SugarResponse callback) {
        if(!data.containsKey("ids")) throw new UnsupportedOperationException("To get multiple records you must" +
                "specify an array record ids in your data");
        PRequest.post(this.crmUrl, buildSugarData("get_entries",
                buildSugarParams(GET_ENTRIES, moduleName, data, null)), callback);
    }

    /**
     * @param moduleName {String} The name of the module to query or plainly the table name
     * @param data {HashMap} The post data .. Note that the ordering arrangement of the data is important
     *             for it to work else Sugar might respond with an invalid session id message
     * @param callback @{SugarResponse} the callback method that handles the server response.
     */
    @Override
    public void setRecord(@NotNull String moduleName, @NotNull  HashMap<String, String> data,
                          @NotNull SugarResponse callback) {
        PRequest.post(this.crmUrl, buildSugarData("set_entry",
                buildSugarParams(SET_ENTRY, moduleName, data, null)), callback);
    }

    @Override
    public void setRecords(@NotNull String moduleName, @NotNull  HashMap<String, String> data,
                           @NotNull HashMap<String, String>[] inserts, @NotNull SugarResponse callback) {
        PRequest.post(this.crmUrl, buildSugarData("set_entries",
                buildSugarParams(SET_ENTRIES, moduleName, data, inserts)), callback);
    }

    /**
     * Update : 27th-03-2016
     * When passing Arrays to the server, we need to remove all escape characters from the Json String
     *
     * @param type {int} The MethodType
     * @param $module {String} The name of the module to query or plainly the table name
     * @param data {HashMap} The post data .. Note that the ordering arrangement of the data is important
     *             for it to work else Sugar might respond with an invalid session id message
     *
     * @apiNote Use <b>fields</b> to represent the list of columns you need to select
     *          Use <b>query</b> to represent your query. Mostly for your where statements, note that your where
     *          Queries should be without the where clause. @see sugarcrm documentation for more information
     * @return {String}
     */
    private String buildSugarParams(int type, @NotNull String $module, @NotNull HashMap<String, String> data,
                                   @Nullable HashMap<String, String> [] arrayInputs){
        String _return; JsonObject params = new JsonObject();
        params.addProperty("session", data.get("session")); params.addProperty("module_name", $module);
        switch (type){
            case GET_ENTRY:
                _return = makeGetEntryParams(params, data);
                break;
            case SET_ENTRY://we need to build up the name_value_list array.. easy stuffs
                _return = makeSetEntryParams(params, data);
                break;
            case SET_ENTRIES:
                _return = makeSetEntriesParams(params, arrayInputs);
                System.out.println(_return);
                break;
            case GET_ENTRY_LIST:
                setParameterDefaults(data);
                _return = makeGetEntryListParams(params, data);
                System.out.println(_return);
                break;
            case GET_ENTRIES:
                _return = makeGetEntriesParams(params, data);
                break;
            default:
                _return = "";
                break;
        }
        return _return;
    }

    private String makeGetEntryParams(JsonObject params, HashMap<String, String> data){
        if(data.containsKey("id")) params.addProperty("id", data.get("id"));
        if(data.containsKey("fields")) params.addProperty("select_fields", data.get("fields"));
        return params.toString();
    }

    /**
     * Added 27th-03-2016
     * @param params
     * @param data
     * @return
     */
    private String makeGetEntriesParams(JsonObject params, HashMap<String, String> data){
        String ids =(data.get("ids")==null) ? "[]" : data.get("ids");
        if(!ids.equals("[]")) ids = parser.parse(ids).getAsJsonArray().toString();
        params.addProperty("ids", ids);
        String fields = (data.get("fields")==null)? "" : data.get("fields");
        if(!fields.equals("")) fields = parser.parse(fields).getAsJsonArray().toString();
        params.addProperty("select_fields", fields);
        params.addProperty("link_name_to_fields_array", "");
        params.addProperty("track_view", "false");
        return params.toString().replaceAll("\\]\"","]").replaceAll("\\\\", "").replace("\"[", "[").replace("\"[", "[");
    }

    /**
     * Added 27th-03-2016
     * @param params
     * @param data
     * @return
     */
    private String makeGetEntryListParams(JsonObject params, HashMap<String, String> data){
        params.addProperty("query", data.get("query"));
        params.addProperty("order_by", data.get("order_by"));
        params.addProperty("offset", data.get("offset"));
        String field = data.get("fields");
        if(!data.get("fields").equals("")) field = parser.parse(data.get("fields")).getAsJsonArray().toString();
        params.addProperty("select_fields",field);
        params.addProperty("link_name_to_fields_array", data.get("extras"));
        params.addProperty("max_results", data.get("max"));
        params.addProperty("deleted", data.get("deleted"));
        params.addProperty("favorites", data.get("favorites"));
        return params.toString().replaceAll("\\]\"","]").replaceAll("\\\\", "").replace("\"[", "[");
    }

    /**
     * Added 27th-03-2016
     * @param params
     * @param data
     * @return
     */
    private String makeSetEntryParams(JsonObject params, HashMap<String, String> data){
        JsonArray pool = new JsonArray();
        buildNameValueList(data, pool);
        params.add("name_value_list", pool);
        return params.toString();
    }

    /**
     * Added 27th-03-2016
     * @param params
     * @param data
     * @return
     */
    private String makeSetEntriesParams(JsonObject params, HashMap<String, String>[] data){
        JsonArray namedValueList = new JsonArray();
        for (HashMap<String, String> aData : data) {
            JsonArray recordArgs = new JsonArray();
            buildNameValueList(aData, recordArgs);
            namedValueList.add(recordArgs);
        }
        params.add("name_value_list", namedValueList);
        return params.toString();
    }

    /**
     * Added 27th-03-2016
     * @param fromData
     * @param toArray
     */
    private void buildNameValueList(HashMap<String, String> fromData, JsonArray toArray){
        for (Map.Entry<String, String> entry : fromData.entrySet()) {
            JsonObject recordObj = new JsonObject();
            recordObj.addProperty("name", entry.getKey());
            recordObj.addProperty("value", entry.getValue());
            toArray.add(recordObj);
        }
    }

    private void setParameterDefaults(HashMap<String, String> data){
        if(!data.containsKey("query")) data.put("query", null);
        if(!data.containsKey("order_by")) data.put("order_by", "");
        if(!data.containsKey("offset")) data.put("offset", String.valueOf(0));
        if(!data.containsKey("fields")) data.put("fields", "");
        if(!data.containsKey("extras")) data.put("extras", "[]");
        if(!data.containsKey("max")) data.put("max", String.valueOf(30));
        if(!data.containsKey("deleted")) data.put("deleted", String.valueOf(0));
        if(!data.containsKey("favorites")) data.put("favorites", String.valueOf(false));
    }

    private HashMap<String , String> buildSugarData(String methodType, String data){
        dataTemp.put("method", methodType+"");
        dataTemp.put("input_type", "JSON");
        dataTemp.put("response_type", "JSON");
        dataTemp.put("rest_data", data);
        return dataTemp;
    }
}
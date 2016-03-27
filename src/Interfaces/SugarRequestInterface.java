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

package Interfaces;




import Impl.SugarResponse;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;

/**
 * @author Okeke Paul
 * <p>
 *     An Interface that can only be used to access the SugarAPI
 * </p>
 * Created by paulex on 25/03/16.
 */
public interface SugarRequestInterface {

    void login(String username, String password, SugarResponse callback);

    String getSessionID();

    void setSessionID(String sessionID);

    void getRecord(@NotNull String moduleName, @NotNull HashMap<String, String> data, @NotNull SugarResponse callback);

    void getRecords(@NotNull String moduleName, @NotNull  HashMap<String, String> data, @NotNull SugarResponse callback);

    void getMultipleRecords(@NotNull String moduleName, @NotNull HashMap<String, String> data,
                            @NotNull SugarResponse callback);

    void setRecord(@NotNull String moduleName, @NotNull HashMap<String, String> data, @NotNull SugarResponse callback);

    void setRecords(@NotNull String moduleName, @NotNull HashMap<String, String> data,
                    @NotNull HashMap<String, String>[] inserts, @NotNull SugarResponse callback);
}

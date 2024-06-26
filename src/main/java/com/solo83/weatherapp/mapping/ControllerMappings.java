package com.solo83.weatherapp.mapping;

/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 * =============================================================================
 */


import java.util.HashMap;
import java.util.Map;


import com.solo83.weatherapp.controller.HomeController;
import com.solo83.weatherapp.controller.Register;
import com.solo83.weatherapp.controller.ThymeLeafController;
import org.thymeleaf.web.IWebRequest;


public class ControllerMappings {


    private static final Map<String, ThymeLeafController> controllersByURL;


    static {
        controllersByURL = new HashMap<>();
        controllersByURL.put("/", new HomeController());
        controllersByURL.put("/register", new Register());

    }



    public static ThymeLeafController resolveControllerForRequest(final IWebRequest request) {
        final String path = getRequestPath(request);
        return controllersByURL.get(path);
    }


    // Path within application might contain the ";jsessionid" fragment due to URL rewriting
    private static String getRequestPath(final IWebRequest request) {

        String requestPath = request.getPathWithinApplication();

        final int fragmentIndex = requestPath.indexOf(';');
        if (fragmentIndex != -1) {
            requestPath = requestPath.substring(0, fragmentIndex);
        }

        return requestPath;

    }


    private ControllerMappings() {
        super();
    }


}
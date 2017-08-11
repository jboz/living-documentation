/*
 * Copyright (C) 2017 Focus IT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.ifocusit.livingdoc.plugin.confluence.client;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import static ch.ifocusit.livingdoc.plugin.confluence.utils.InputStreamUtils.inputStreamAsString;

/**
 * @author Julien Boz
 */
@SuppressWarnings("WeakerAccess")
public class RequestFailedException extends RuntimeException {

    RequestFailedException(HttpRequest request, HttpResponse response) {
        super(""
                + response.getStatusLine().getStatusCode()
                + " "
                + response.getStatusLine().getReasonPhrase()
                + " "
                + request.getRequestLine().getMethod()
                + " "
                + request.getRequestLine().getUri()
                + " "
                + failedResponseContent(response)
        );
    }

    private static String failedResponseContent(HttpResponse response) {
        try {
            return inputStreamAsString(response.getEntity().getContent());
        } catch (Exception ignored) {
            return "";
        }
    }

}

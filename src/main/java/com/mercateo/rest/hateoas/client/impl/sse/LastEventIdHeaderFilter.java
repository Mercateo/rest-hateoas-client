/**
 * Copyright © 2016 Mercateo AG (http://www.mercateo.com)
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
package com.mercateo.rest.hateoas.client.impl.sse;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import org.glassfish.jersey.media.sse.SseFeature;

@Priority(Priorities.HEADER_DECORATOR)
public class LastEventIdHeaderFilter implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (requestContext.getHeaderString(SseFeature.LAST_EVENT_ID_HEADER) == null) {
            String lastKnownEventId = (String) requestContext.getConfiguration().getProperty(
                    SseFeature.LAST_EVENT_ID_HEADER);
            if (lastKnownEventId != null) {
                requestContext.getHeaders().putSingle(SseFeature.LAST_EVENT_ID_HEADER,
                        lastKnownEventId);
            }
        }

    }

}

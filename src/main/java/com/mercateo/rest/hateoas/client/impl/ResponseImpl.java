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
package com.mercateo.rest.hateoas.client.impl;

import java.net.URI;
import java.util.Optional;

import com.google.common.annotations.VisibleForTesting;
import com.mercateo.rest.hateoas.client.OngoingResponse;
import com.mercateo.rest.hateoas.client.Response;
import com.mercateo.rest.hateoas.client.schema.ClientHyperSchema;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class ResponseImpl<T> implements Response<T> {

    @NonNull
    private final ResponseBuilder responseBuilder;

    @VisibleForTesting
    final ClientHyperSchema jsonHyperSchema;

    protected final T value;

    @NonNull
    private URI uri;

    @Override
    public Optional<T> getResponseObject() {
        return Optional.ofNullable(value);
    }

    @Override
    public <S> OngoingResponse<S> prepareNextWithResponse(@NonNull Class<S> clazz) {
        if (jsonHyperSchema == null) {
            throw new IllegalStateException("There is no possibility for a next response");
        }
        return new OngoingResponseImpl<S>(clazz, jsonHyperSchema, responseBuilder, uri);
    }

    @Override
    public boolean isRelPresent(@NonNull String rel) {
        return jsonHyperSchema != null && jsonHyperSchema.getByRel(rel).isPresent();
    }
}

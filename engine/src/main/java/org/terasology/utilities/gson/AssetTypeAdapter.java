/*
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.utilities.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.terasology.gestalt.assets.Asset;
import org.terasology.utilities.Assets;

import java.lang.reflect.Type;

/**
 */
public class AssetTypeAdapter<V extends Asset> implements JsonDeserializer<V> {

    private Class<V> type;

    public AssetTypeAdapter(Class<V> type) {
        this.type = type;
    }

    @Override
    public V deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return type.cast(Assets.get(json.getAsString(), type).orElse(null));
    }
}

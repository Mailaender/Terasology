/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.entitySystem.prefab.internal;

import com.google.common.base.Charsets;
import org.terasology.entitySystem.metadata.ComponentLibrary;
import org.terasology.entitySystem.prefab.PrefabData;
import org.terasology.gestalt.assets.format.AbstractAssetAlterationFileFormat;
import org.terasology.gestalt.assets.format.AssetDataFile;
import org.terasology.persistence.serializers.EntityDataJSONFormat;
import org.terasology.persistence.serializers.PrefabSerializer;
import org.terasology.persistence.typeHandling.TypeHandlerLibrary;
import org.terasology.protobuf.EntityData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 */
public class PrefabDeltaFormat extends AbstractAssetAlterationFileFormat<PrefabData> {

    private final ComponentLibrary componentLibrary;
    private final TypeHandlerLibrary typeHandlerLibrary;

    public PrefabDeltaFormat(ComponentLibrary componentLibrary, TypeHandlerLibrary typeHandlerLibrary) {
        super("prefab");
        this.componentLibrary = componentLibrary;
        this.typeHandlerLibrary = typeHandlerLibrary;
    }

    @Override
    public void apply(AssetDataFile assetDataFile, PrefabData assetData) throws IOException {

        try (BufferedReader deltaReader = new BufferedReader(new InputStreamReader(assetDataFile.openStream(), Charsets.UTF_8))) {
            EntityData.Prefab delta = EntityDataJSONFormat.readPrefab(deltaReader);
            PrefabSerializer serializer = new PrefabSerializer(componentLibrary, typeHandlerLibrary);
            serializer.deserializeDeltaOnto(delta, assetData);
        }
    }
}

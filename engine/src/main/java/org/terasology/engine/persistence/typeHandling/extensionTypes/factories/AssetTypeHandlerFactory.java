// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.engine.persistence.typeHandling.extensionTypes.factories;

import org.terasology.gestalt.assets.Asset;
import org.terasology.engine.persistence.typeHandling.TypeHandler;
import org.terasology.engine.persistence.typeHandling.TypeHandlerContext;
import org.terasology.engine.persistence.typeHandling.TypeHandlerFactory;
import org.terasology.engine.persistence.typeHandling.extensionTypes.AssetTypeHandler;
import org.terasology.nui.reflection.TypeInfo;

import java.util.Optional;

/**
 * Generates an {@link AssetTypeHandler} for each type extending {@link Asset} so that an
 * {@link AssetTypeHandler} does not have to be manually registered for each subtype of {@link Asset}.
 */
public class AssetTypeHandlerFactory implements TypeHandlerFactory {
    @Override
    public <T> Optional<TypeHandler<T>> create(TypeInfo<T> typeInfo, TypeHandlerContext context) {
        Class<? super T> rawType = typeInfo.getRawType();

        if (!Asset.class.isAssignableFrom(rawType)) {
            return Optional.empty();
        }

        @SuppressWarnings({"unchecked"})
        TypeHandler<T> typeHandler = new AssetTypeHandler(rawType);

        return Optional.of(typeHandler);
    }
}
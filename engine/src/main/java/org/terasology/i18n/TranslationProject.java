/*
 * Copyright 2015 MovingBlocks
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

package org.terasology.i18n;

import java.util.Locale;
import java.util.Set;

import org.terasology.i18n.assets.Translation;

/**
 * Describes a translation project. It performs the actual mapping of ID strings to human-readable text
 * in a language that is specified through {@link Locale}.
 */
public interface TranslationProject {

    /**
     * @param trans the translation to add.
     */
    void add(Translation trans);

    /**
     * If no perfect match is found for the given locale, fallback strategies will attempt to find the closest match.
     * @param id the id of the string to translate (without project reference).
     * @param locale the target locale
     * @return the translated string
     */
    String translate(String id, Locale locale);

    /**
     * @return the set of registered locales with at least one entry
     */
    Set<Locale> getAvailableLocales();
}

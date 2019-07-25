/*
 * Copyright (C) 2016 The Android Open Source Project
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

package com.example.android.nfcprovisioning;

import android.content.res.Configuration;
import android.os.Build;
import android.os.LocaleList;

import java.util.Locale;


public final class CompatUtils {

    private CompatUtils() {
        // Prevent instantiation
    }

    /**
     * Retrieves the primary locale from the specified {@link Configuration}.
     *
     * @param configuration The current {@link Configuration}.
     * @return The primary locale.
     */
    public static Locale getPrimaryLocale(Configuration configuration) {
        if (Build.VERSION.SDK_INT >= 24) {
            final LocaleList locales = configuration.getLocales();
            if (locales.size() > 0) {
                return locales.get(0);
            }
        }
        //noinspection deprecation
        return configuration.locale;
    }

}

/*
 * Copyright 2014 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nebulights.coinstacks

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration

/**
 * Example of migrating a Realm file from version 0 (initial version) to its last version (version 3).
 */
class Migration : RealmMigration {

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        var oldVersion = oldVersion

        val schema = realm.schema

        // Migrate from version 0 to version 1
        if (oldVersion == 0L) {

            schema.create("BasicAuthenticationRealm")
                    .addField("exchange", String::class.java, FieldAttribute.PRIMARY_KEY, FieldAttribute.REQUIRED)
                    .addField("apiKey", String::class.java, FieldAttribute.REQUIRED)
                    .addField("apiSecret", String::class.java, FieldAttribute.REQUIRED)
                    .addField("password", String::class.java, FieldAttribute.REQUIRED)
                    .addField("userName", String::class.java, FieldAttribute.REQUIRED)

            schema.create("WatchAddressRealm")
                    .addField("exchange", String::class.java, FieldAttribute.REQUIRED)
                    .addField("address", String::class.java, FieldAttribute.REQUIRED)
                    .addField("type", String::class.java, FieldAttribute.REQUIRED)
                    .addField("nickName", String::class.java, FieldAttribute.REQUIRED)

            oldVersion++
        }
    }
}
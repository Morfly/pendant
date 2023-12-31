/*
 * Copyright 2023 Pavlo Stavytskyi
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

package io.morfly.pendant.starlark.lang.feature

import io.morfly.pendant.starlark.lang.Checkpoint
import io.morfly.pendant.starlark.lang.ContextId
import io.morfly.pendant.starlark.lang.type.Key
import io.morfly.pendant.starlark.lang.type.StringType
import io.morfly.pendant.starlark.lang.type.TupleType
import io.morfly.pendant.starlark.lang.type.Value
import io.morfly.pendant.starlark.lang.Modifier


private fun CollectionsFeatureUnderCompilationTest.CompilationTests() {

    // =================
    // ===== Lists =====
    // =================

    val list1: List<StringType> = list["item1", "item2"]
    val list2 = list["item1", 2]
    val list3: List<StringType> = list("item1", "item2")
    val list4: List<StringType> = list()
    val list5 = list()


    // ========================
    // ===== Dictionaries =====
    // ========================

    val dict1: Map<Key, Value> = dict { "key1" to "value1"; "key2" to "value2" }
    val dict2: Map<Key, Value> = dict { "key1" to "value1"; 2 to 2.0 }
    val dict3: Map<Key, Value> = dict {}
    val dict4 = dict {}


    // ==================
    // ===== Tuples =====
    // ==================

    val tuple1: TupleType = tuple("value1", 2, 3.0)
    val tuple2 = tuple("value1", 2, 3.0)
    val tuple3: TupleType = tuple()
    val tuple4 = tuple()
}


private class CollectionsFeatureUnderCompilationTest :
// Feature under test
    CollectionsFeature {

    override val modifiers = linkedMapOf<ContextId, MutableMap<Checkpoint, MutableList<Modifier<*>>>>()
}
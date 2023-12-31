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

@file:Suppress("LocalVariableName")

package io.morfly.pendant.starlark.lang.feature


private fun ReassignmentsFeatureUnderCompilationTest.CompilationTests() {

    // ================================
    // ===== String reassignments =====
    // ================================

    val STRING_VARIABLE by "value"

    STRING_VARIABLE `=` "new_value"
    STRING_VARIABLE `=` STRING_VARIABLE
    STRING_VARIABLE `=` "new_value1" `+` "new_value2" `+` STRING_VARIABLE `+` null
    STRING_VARIABLE `=` STRING_VARIABLE `+` "new_value2"
    STRING_VARIABLE `=` STRING_VARIABLE `+` STRING_VARIABLE
    STRING_VARIABLE `=` null


    // ==============================
    // ===== List reassignments =====
    // ==============================

    val LIST_VARIABLE by list["item"]

    LIST_VARIABLE `=` list["new_item"]
    LIST_VARIABLE `=` LIST_VARIABLE
    LIST_VARIABLE `=` list["new_item"] `+` list["new_item2"] `+` LIST_VARIABLE `+` null
    LIST_VARIABLE `=` LIST_VARIABLE `+` list["new_item2"]
    LIST_VARIABLE `=` LIST_VARIABLE `+` LIST_VARIABLE `+` LIST_VARIABLE
    LIST_VARIABLE `=` LIST_VARIABLE `+` LIST_VARIABLE `+` list["new_item3"]
    LIST_VARIABLE `=` null


    // ====================================
    // ===== Dictionary reassignments =====
    // ====================================

    val DICT_VARIABLE by dict { "key" to "value" }

    DICT_VARIABLE `=` dict { "new_key" to "new_value" }
    DICT_VARIABLE `=` { "new_key" to "new_value" }
    DICT_VARIABLE `=` { 1 to 1 } `+` dict { 2 to 2 } `+` DICT_VARIABLE `+` null
    DICT_VARIABLE `=` DICT_VARIABLE `+` { 2 to "value2" }
    DICT_VARIABLE `=` DICT_VARIABLE `+` DICT_VARIABLE `+` DICT_VARIABLE
    DICT_VARIABLE `=` null
}


private interface ReassignmentsFeatureUnderCompilationTest :
// Feature under test
    ReassignmentsFeature,
// Additional features for compatibility tests
    AssignmentsFeature,
    DynamicBinaryPlusFeature,
    CollectionsFeature
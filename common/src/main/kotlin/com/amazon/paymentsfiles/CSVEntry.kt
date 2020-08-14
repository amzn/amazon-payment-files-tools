// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

/**
 * CSV Entry Object Class
 *
 * Object for mapping column names to values for an entry in a CSV-type file
 *
 * @param fields an array of CSVFieldModel objects denoting field types and their corresponding validations
 * @param values an array of strings denoting entry fields
 * @param lineNo an integer representing the row of the table
 * @throws FieldsAndValuesException when the length of the fields and values arrays do not match
 * @throws ColumnNotFoundException when the value corresponding to a field not present is requested
 */
class CSVEntry(fields: Array<out CSVFieldModel>, values: Array<String>, val lineNo: Int = 1) {

    init {
        if (fields.size != values.size)
            throw FieldsAndValuesException()
    }

    val contents = fields.zip(values).associate { it }

    fun get(column: CSVFieldModel): String = this.contents[column] ?: throw ColumnNotFoundException(column.fieldName)
}

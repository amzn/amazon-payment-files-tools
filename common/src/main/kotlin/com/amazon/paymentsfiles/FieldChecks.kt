// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Currency

/*
 * CSV Field Validation Functions
 *
 * Minor methods for checking common criteria for CSV fields
 *
 * @param fieldName a String denoting the name of the field being examined for use in the error message
 * @param value the String that was passed as a value in the given field and needs to be validated
 * @return a FieldError object if the check fails and null otherwise
 */
fun checkNonEmpty(fieldName: String, value: String): FieldError? =
    if (value.isEmpty()) FieldError.empty(fieldName) else null

fun checkLength(fieldName: String, value: String, maxLength: Int) =
    if (value.length > maxLength) FieldError.exceedsLength(fieldName, maxLength) else null

fun checkValidChoice(fieldName: String, value: String, choices: List<String>) =
    if (value in choices) null else FieldError.notValidChoice(fieldName, choices)

fun checkInteger(fieldName: String, value: String): FieldError? = try {
    value.toInt()
    null
} catch (e: NumberFormatException) {
    FieldError.notInt(fieldName)
}

fun checkDouble(fieldName: String, value: String): FieldError? = try {
    value.toDouble()
    null
} catch (e: NumberFormatException) {
    FieldError.notDouble(fieldName)
}

fun checkISOCurrency(fieldName: String, value: String): FieldError? = try {
    Currency.getInstance(value)
    null
} catch (e: IllegalArgumentException) {
    FieldError.notCurrency(fieldName)
}

fun checkAllASCII(fieldName: String, value: String): FieldError? {
    for (c in value.toByteArray())
        if (c < 0) // Any char with a value over 127 (and therefore not ASCII) will have a negative byte value
            return FieldError.containsUnicode(fieldName)
    return null
}

fun checkDateTimeParse(fieldName: String, value: String, pattern: String): FieldError? = try {
    DateTimeFormatter.ofPattern(pattern).parse(value)
    null
} catch (e: DateTimeParseException) {
    FieldError.dateTimeError(fieldName, pattern)
}

/*
 * Field Validation Function Generators
 *
 * Minor methods for creating CSV field checks from inputs
 *
 * @return a function of type (String, String) -> FieldError?
 */
fun makeLengthCheck(maxLength: Int): (String, String) -> FieldError? =
    { fieldName, value -> checkLength(fieldName, value, maxLength) }

fun makeChoiceCheck(choices: List<String>): (String, String) -> FieldError? =
    { fieldName, value -> checkValidChoice(fieldName, value, choices) }

fun makeDateTimeParseCheck(pattern: String): (String, String) -> FieldError? =
    { fieldName, value -> checkDateTimeParse(fieldName, value, pattern) }

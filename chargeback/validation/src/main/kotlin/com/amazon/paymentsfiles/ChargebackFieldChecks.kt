// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/*
 * Field Validation Functions Specific to Chargebacks
 *
 * Minor methods for checking criteria for all the fields are met
 *
 * @param fieldName a String denoting the name of the field being examined for use in the error message
 * @param value the String that was passed as a value in the given field and needs to be validated
 * @return a FieldError object if the check fails and null otherwise
 */
fun checkReasonDescription(fieldName: String, value: String): FieldError? =
        checkLength(fieldName, value, 50) ?: checkAllASCII(fieldName, value)

fun checkIsoInstant(fieldName: String, value: String): FieldError? = try {
    DateTimeFormatter.ISO_INSTANT.parse(value)
    null
} catch (e: DateTimeParseException) {
    StandardChargebackError.notIsoInstant(fieldName)
}

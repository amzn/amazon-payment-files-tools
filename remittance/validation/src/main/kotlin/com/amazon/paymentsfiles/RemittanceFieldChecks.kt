// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

/*
 * Field Validation Functions Specific to Remittances
 *
 * Minor methods for checking criteria for all the fields are met
 *
 * @param fieldName a String denoting the name of the field being examined for use in the error message
 * @param value the String that was passed as a value in the given field and needs to be validated
 * @return a FieldError object if the check fails and null otherwise
 */
fun checkCorrectFormatVersion(@Suppress("UNUSED_PARAMETER") fieldName: String, value: String): FieldError? =
        if (value == REMITTANCE_FILE_VERSION) null else StandardRemittanceError.FileFormatVersion.error

fun checkThirtyTwoNumeric(@Suppress("UNUSED_PARAMETER") fieldName: String, value: String): FieldError? {
    val pattern = """\d{1,32}"""
    return if (Regex(pattern).matches(value)) null else StandardRemittanceError.DepositAccountNumber.error
}

fun checkStandardMonetary(fieldName: String, value: String): FieldError? {
    val pattern = """-?\d{0,16}(\.\d{1,2})?"""
    return if (Regex(pattern).matches(value)) null else StandardRemittanceError.notStandardMonetary(fieldName)
} // Potentially replace with a cross-reference check to the provided currency

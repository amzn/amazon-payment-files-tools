// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

/**
 * Error Message Object Class
 *
 * Class for holding human readable messages relating to a field error
 *
 * @property normal standard error message reported
 * @property verbose extended error message with additional context
 * @constructor With one argument, sets normal and verbose error messages as the same value
 */
class FieldError(val normal: String, val verbose: String) {

    constructor(normal: String) : this(normal, normal)

    companion object {
        fun empty(fieldName: String) = FieldError("$fieldName cannot be empty")
        fun exceedsLength(fieldName: String, maxLength: Int) =
            FieldError("$fieldName cannot exceed $maxLength characters in length")

        fun notValidChoice(fieldName: String, choices: List<String>) =
            FieldError("$fieldName must be one of the following options: ${choices.joinToString()}")

        fun notInt(fieldName: String) = FieldError("$fieldName must be a valid integer")
        fun notDouble(fieldName: String) = FieldError("$fieldName must be in numeric decimal form")
        fun notCurrency(fieldName: String) = FieldError("$fieldName not a valid ISO 4217 currency code")
        fun containsUnicode(fieldName: String) = FieldError("$fieldName can only contain ASCII characters")
        fun dateTimeError(fieldName: String, pattern: String) =
            FieldError("$fieldName has improper date-time format (must be <$pattern>)")
    }
}

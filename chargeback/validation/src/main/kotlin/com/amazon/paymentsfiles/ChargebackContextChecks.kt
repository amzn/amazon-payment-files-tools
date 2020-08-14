// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

import java.util.Currency

/**
 * Context validation function to ensure that a record's Disputed Amount has the proper number of decimal places
 * expected for the corresponding Currency
 * @param record a CSVEntry object being examined
 * @return a FieldError object if the check fails and null otherwise
 */
fun checkCurrencyFields(record: CSVEntry): FieldError? = try {
    val currency = Currency.getInstance(record.get(ChargebackField.Currency))
    val amount = record.get(ChargebackField.DisputedAmount).toBigDecimal()
    if (amount.scale() == currency.defaultFractionDigits) null
    else StandardChargebackError.DisputedAmountContext.error
} catch (e: IllegalArgumentException) {
    null
}

val contextChecks = listOf(::checkCurrencyFields)

/**
 * CSVEntry extension function that performs additional validation checks across multiple fields of a chargeback entry
 */
fun CSVEntry.validateContext(): List<FieldError> = contextChecks.mapNotNull { check -> check(this) }

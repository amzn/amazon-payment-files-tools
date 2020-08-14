// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles.chargeback.generation

import com.amazon.paymentsfiles.CSVEntry
import com.amazon.paymentsfiles.ChargebackField
import com.amazon.paymentsfiles.DisputeStatusOption
import com.amazon.paymentsfiles.EntryFieldException
import com.amazon.paymentsfiles.ReasonOption
import com.amazon.paymentsfiles.StandardChargebackError
import com.amazon.paymentsfiles.validateFields
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.Currency

/**
 * Chargeback Entry Data Class
 *
 * Library class for grouping together necessary data for an individual entry in a chargeback file
 */
data class ChargebackEntry @JvmOverloads constructor(
    val disputeStatus: DisputeStatusOption,
    val caseNumber: String,
    val transactionID: String,
    val currency: Currency,
    var disputedAmount: BigDecimal,
    val reason: ReasonOption,
    val representmentDeadline: LocalDate,
    val reasonDescription: String = "",
    val disputeTime: Instant = Instant.now()
) {

    init {
        formatDisputedAmount()
        validateStringFields()
    }

    /**
     * Performs validation checks on the string fields Case Number, Transaction ID, and Reason Description
     * @throws EntryFieldException if any validation errors occur
     */
    private fun validateStringFields() {
        val errors = CSVEntry(
            arrayOf(ChargebackField.CaseNumber, ChargebackField.TransactionID, ChargebackField.ReasonDescription),
            arrayOf(caseNumber, transactionID, reasonDescription)
        ).validateFields()
        if (errors.isNotEmpty())
            throw EntryFieldException(errors.first().normal)
    }

    /**
     * Formats the Disputed Amount field with the proper number of decimal places for the given currency
     */
    private fun formatDisputedAmount() {
        if (disputedAmount.scale() > currency.defaultFractionDigits)
            throw EntryFieldException(StandardChargebackError.DisputedAmountContext.error.verbose)
        disputedAmount = disputedAmount.setScale(currency.defaultFractionDigits)
    }
}

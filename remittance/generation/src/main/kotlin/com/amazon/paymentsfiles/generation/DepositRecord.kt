// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles.generation

import com.amazon.paymentsfiles.CSVEntry
import com.amazon.paymentsfiles.DepositRecordField
import com.amazon.paymentsfiles.DirectionOption
import com.amazon.paymentsfiles.EntryFieldException
import com.amazon.paymentsfiles.TransactionMethodOption
import com.amazon.paymentsfiles.TransactionTypeOption
import com.amazon.paymentsfiles.validateFields
import java.math.BigDecimal
import java.util.Currency

/**
 * Deposit Record Data Class
 *
 * Library data class implementing RemittanceEntry for grouping together necessary data for a remittance deposit record
 * into a single data object
 */
data class DepositRecord @JvmOverloads constructor(
    val transactionMethod: TransactionMethodOption,
    val transactionType: TransactionTypeOption,
    val transactionID: String,
    val transactionAmountCurrency: Currency,
    val transactionAmount: BigDecimal,
    val processingDivisionID: String,
    val direction: DirectionOption? = null,
    val transactionFXCurrency: Currency? = null,
    val transactionFXAmount: BigDecimal? = null,
    val transactionFXRate: BigDecimal? = null
) {

    init {
        validateStringFields()
    }

    /**
     * Shortcut boolean functions for determining whether a header specifies foreign exchange fields
     */
    fun fxFieldsFilled(): Boolean = (direction != null && transactionFXCurrency != null && transactionFXAmount != null)

    fun fxRateSpecified(): Boolean = (transactionFXRate != null)

    /**
     * Performs validation checks on the string fields Case Number, Transaction ID, and Reason Description
     * @throws EntryFieldException if any validation errors occur
     */
    private fun validateStringFields() {
        val errors = CSVEntry(
            arrayOf(DepositRecordField.TransactionID, DepositRecordField.AmazonProcessingDivisionID),
            arrayOf(transactionID, processingDivisionID)
        ).validateFields()
        if (errors.isNotEmpty())
            throw EntryFieldException(errors.first().normal)
    }
}

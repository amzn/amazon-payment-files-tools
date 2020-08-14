// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles.generation

import com.amazon.paymentsfiles.CSVEntry
import com.amazon.paymentsfiles.DepositHeaderField
import com.amazon.paymentsfiles.EntryFieldException
import com.amazon.paymentsfiles.validateFields
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Currency

/**
 * Deposit Header Data Class
 *
 * Library data class implementing RemittanceEntry for grouping together necessary data for a remittance deposit header
 * into a single data object
 */
data class DepositHeader @JvmOverloads constructor(
    val depositDate: LocalDate,
    val depositAccountName: String,
    val depositAccountNumber: String,
    val remittanceVendorID: String,
    val effectiveDepositDate: LocalDate,
    val bankTransferID: String,
    val depositCurrency: Currency,
    val depositAmount: BigDecimal,
    val remittanceRevision: Int = 1,
    val fxPresentmentCurrency: Currency? = null,
    val fxPresentmentAmount: BigDecimal? = null,
    val fxRate: BigDecimal? = null
) {

    init {
        validateStringFields()
    }

    /**
     * Shortcut boolean functions for determining whether a header specifies foreign exchange fields
     */
    fun isFX(): Boolean = (fxPresentmentCurrency != null && fxPresentmentAmount != null)

    fun fxRateSpecified(): Boolean = (fxRate != null)

    /**
     * Performs validation checks on the string fields Case Number, Transaction ID, and Reason Description
     * @throws EntryFieldException if any validation errors occur
     */
    private fun validateStringFields() {
        val errors = CSVEntry(
            arrayOf(DepositHeaderField.DepositAccountName, DepositHeaderField.DepositAccountNumber,
                DepositHeaderField.BankTransferID),
            arrayOf(depositAccountName, depositAccountNumber, bankTransferID)
        ).validateFields()
        if (errors.isNotEmpty())
            throw EntryFieldException(errors.first().normal)
    }
}

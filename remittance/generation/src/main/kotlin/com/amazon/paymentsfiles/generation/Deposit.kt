// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles.generation

import com.amazon.paymentsfiles.FXRequirements
import com.amazon.paymentsfiles.RecordType
import java.io.OutputStreamWriter
import java.math.BigDecimal

/**
 * Deposit Generator Class
 *
 * Internal class for storing necessary metadata about the current deposit block and writing the deposit to the output
 * stream when generating a remittance file
 */
internal class Deposit(val header: DepositHeader, val writer: OutputStreamWriter, val fxRequirements: FXRequirements) {

    var recordCount = 0
    var depositTotal = BigDecimal("0.00")
    var initialized = false

    fun initialize() {
        writer.append(""""${header.toCSVLine()}"${'\n'}""")
        initialized = true
    }

    /**
     * DepositHeader extension function to write the header's fields into a proper CSV line to be written to a file
     */
    private fun DepositHeader.toCSVLine(): String = "${RecordType.DepositHeader.charName},${formatDate(depositDate)}," +
        "$depositAccountName,$depositAccountNumber,$remittanceVendorID,${formatDate(effectiveDepositDate)}," +
        "$bankTransferID,$depositCurrency,${formatMonetary(depositAmount)},$remittanceRevision," +
        "${filterNull(fxPresentmentCurrency)},${formatNullableMonetary(fxPresentmentAmount)},${filterNull(fxRate)}"

    /**
     * Function to run validation checks on a record being added to the deposit. If the checks pass, that record is
     * written to the file and deposit metadata is adjusted as necessary
     */
    fun processRecord(record: DepositRecord) {
        if (!this.initialized)
            this.initialize()
        checkFXFieldsProvided(record)
        checkFXRateProvided(record)
        writer.append(""""${record.toCSVLine()}"${'\n'}""")
        this.depositTotal += formatMonetary(record.transactionAmount)
        this.recordCount++
    }

    /**
     * Validation function performed when a record is added to ensure that the record provided foreign exchange fields
     * if the deposit fxRequirements requires it
     */
    private fun checkFXFieldsProvided(record: DepositRecord) {
        if (fxRequirements == FXRequirements.PayStationStandard && header.isFX() && !record.fxFieldsFilled())
            throw FXFieldsNotProvidedException()
        else if (fxRequirements == FXRequirements.OnlyInRecords && !record.fxFieldsFilled())
            throw FXFieldsNotProvidedException()
    }

    /**
     * Validation function performed when a record is added to ensure that the record provided a foreign exchange rate
     * if the deposit fxRequirements requires it
     */
    private fun checkFXRateProvided(record: DepositRecord) {
        if (
            fxRequirements == FXRequirements.PayStationStandard &&
            header.isFX() &&
            !header.fxRateSpecified() &&
            !record.fxRateSpecified()
        ) {
            throw FXRateNotProvidedException()
        } else if (fxRequirements == FXRequirements.OnlyInRecords && !record.fxRateSpecified()) {
            throw FXRateNotProvidedException()
        }
    }

    /**
     * DepositRecord extension function to write the record's fields into a proper CSV line to be written to a file
     */
    private fun DepositRecord.toCSVLine(): String = "${RecordType.DepositRecord.charName},${transactionMethod.abbr}," +
        "${transactionType.abbr},$transactionID,$transactionAmountCurrency,${formatMonetary(transactionAmount)}," +
        "$processingDivisionID,${filterNull(direction?.abbr)},${filterNull(transactionFXCurrency)}," +
        "${formatNullableMonetary(transactionFXAmount)},${filterNull(transactionFXRate)}"

    fun close() {
        checkDepositTotal()
        writeTrailer()
    }

    /**
     * Validation function performed when the deposit is closed that ensures the record transaction amounts sum to
     * the deposit amount provided in the header
     */
    private fun checkDepositTotal() {
        if (formatMonetary(header.depositAmount) != depositTotal)
            throw RecordAmountSumException()
    }

    private fun writeTrailer() = writer.append(
        """"${RecordType.DepositTrailer.charName},${formatDate(header.depositDate)},$recordCount"${'\n'}"""
    )
}

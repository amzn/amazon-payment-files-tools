// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

import java.math.BigDecimal

/**
 * Context validation function to ensure that a foreign exchange rate is provided if necessary.  When the FX setting is
 * Standard, either the record or its corresponding header must provide a foreign exchange rate.  If the FX setting is
 * OnlyInRecords, the record must specify a foreign exchange rate.
 * @param record the CSVEntry being examined for errors
 * @param internalState a RemittanceStats object containing necessary information about the validation process
 * @return an ArrayList of FieldError objects detected in that line
 */
fun checkFXFilled(record: CSVEntry, internalState: RemittanceStats): FieldError? =
    if (internalState.fx == FXRequirements.PayStationStandard &&
            internalState.prevDepositHeader!!.get(DepositHeaderField.FXRate) == "" &&
            record.get(DepositRecordField.TransactionFXRate) == "")
        StandardRemittanceError.FXRate.error
    else if (internalState.fx == FXRequirements.OnlyInRecords && record.get(DepositRecordField.TransactionFXRate) == "")
        StandardRemittanceError.FXRate.error
    else
        null

/**
 * Context validation function to ensure that the record's Transaction Type matches one of the permitted values
 * (which can vary depending on the file class)
 * @param record the CSVEntry being examined for errors
 * @param internalState a RemittanceStats object containing necessary information about the validation process
 * @return an ArrayList of FieldError objects detected in that line
 */
fun checkTransactionType(record: CSVEntry, internalState: RemittanceStats): FieldError? {
    val transactionTypeField = DepositRecordField.TransactionType
    val transactionTypeOptions = listOf(
            listOf(""),
            TransactionTypeOption.fetchOptions(internalState.fileClass).map { it.abbr }
    ).flatten()
    return checkValidChoice(transactionTypeField.fieldName, record.get(transactionTypeField), transactionTypeOptions)
}

/**
 * Context validation function to ensure that the deposit date in the deposit trailer matches the deposit date in the
 * deposit header
 * @param record the CSVEntry being examined for errors
 * @param internalState a RemittanceStats object containing necessary information about the validation process
 * @return an ArrayList of FieldError objects detected in that line
 */
fun checkDepositDates(record: CSVEntry, internalState: RemittanceStats): FieldError? {
    val headerDate = internalState.prevDepositHeader!!.get(DepositHeaderField.DepositDate)
    val trailerDate = record.get(DepositTrailerField.DepositDate)
    return if (DepositHeaderField.DepositDate.validation?.let { it("", headerDate) } == null &&
            DepositTrailerField.DepositDate.validation?.let { it("", trailerDate) } == null &&
            headerDate != trailerDate)
        StandardRemittanceError.DifferingDepositDates.error
    else
        null
}

/**
 * Context validation function to ensure that the Number of Records field in the deposit trailer matches the number of
 * records counted during the validation process.  If the Number of Records field cannot be converted to an integer
 * (and will thus report a field error), the context error is muted
 * @param record the CSVEntry being examined for errors
 * @param internalState a RemittanceStats object containing necessary information about the validation process
 * @return an ArrayList of FieldError objects detected in that line
 */
fun checkNumberOfRecords(record: CSVEntry, internalState: RemittanceStats): FieldError? = try {
    if (record.get(DepositTrailerField.NumberOfRecords).toInt() != internalState.recordCount)
        StandardRemittanceError.IncorrectNumberOfRecords.error
    else
        null
} catch (e: IllegalArgumentException) {
    null
}

/**
 * Context validation function to ensure that the Deposit Amount field in the deposit trailer matches the total sum
 * of transaction amounts in all the corresponding records.  If the Deposit Amount field cannot be converted into a
 * BigDecimal (and will thus report a field error), the context error is muted
 * @param record the CSVEntry being examined for errors
 * @param internalState a RemittanceStats object containing necessary information about the validation process
 * @return an ArrayList of FieldError objects detected in that line
 */
fun checkDepositAmount(
    @Suppress("UNUSED_PARAMETER") record: CSVEntry,
    internalState: RemittanceStats
): FieldError? = try {
    val headerDepositAmount = BigDecimal(internalState.prevDepositHeader!!.get(DepositHeaderField.DepositAmount))
    if (headerDepositAmount != internalState.depositAmountSum)
        StandardRemittanceError.incorrectDepositAmountSum(headerDepositAmount, internalState.depositAmountSum)
    else
        null
} catch (e: IllegalArgumentException) {
    null
}

/**
 * Context validation function to ensure that the Number of Remittance Records field in the file trailer matches the
 * number of deposits counted during the validation process.  If the Number of Records field cannot be converted
 * to an integer (and will thus report a field error), the context error is muted
 * @param record the CSVEntry being examined for errors
 * @param internalState a RemittanceStats object containing necessary information about the validation process
 * @return an ArrayList of FieldError objects detected in that line
 */
fun checkNumberOfRemittanceRecords(record: CSVEntry, internalState: RemittanceStats): FieldError? = try {
    if (record.get(TrailerField.NumberOfRemittanceRecords).toInt() != internalState.remittanceCount)
        StandardRemittanceError.IncorrectNumberOfRemittanceRecords.error
    else
        null
} catch (e: IllegalArgumentException) {
    null
}

val recordContextChecks = listOf(::checkFXFilled, ::checkTransactionType)

val depositTrailerContextChecks = listOf(::checkDepositDates, ::checkNumberOfRecords, ::checkDepositAmount)

val trailerContextChecks = listOf(::checkNumberOfRemittanceRecords)

/**
 * CSVEntry extension function that performs additional field checks against validator configuration state
 */
fun CSVEntry.validateContext(internalState: RemittanceStats): List<FieldError> =
        when (this.fetchRecordType()) {
            RecordType.DepositRecord -> recordContextChecks
            RecordType.DepositTrailer -> depositTrailerContextChecks
            RecordType.Trailer -> trailerContextChecks
            else -> listOf()
        }.mapNotNull { check -> check(this, internalState) }

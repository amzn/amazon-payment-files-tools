// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

import java.math.BigDecimal

/**
 * Remittance Validator Internal State
 *
 * Data class used to store and modify the internal state of a remittance validator for validation check functions
 */
data class RemittanceStats(
    val fileClass: RemittanceFileClass = RemittanceFileClass.Standard,
    val fx: FXRequirements = FXRequirements.None,
    val recordCount: Int = 0,
    val remittanceCount: Int = 0,
    val depositAmountSum: BigDecimal = BigDecimal("0.00"),
    val prevDepositHeader: CSVEntry? = null
) {

    /**
     * Returns a new set of remittance stats based on the record that was just processed
     */
    fun fetchNextState(record: CSVEntry): RemittanceStats = when (record.fetchRecordType()) {
        RecordType.DepositHeader -> RemittanceStats(
            fileClass = fileClass,
            fx = fx,
            recordCount = 0,
            remittanceCount = remittanceCount + 1,
            depositAmountSum = BigDecimal("0.00"),
            prevDepositHeader = record
        )
        RecordType.DepositRecord -> RemittanceStats(
            fileClass = fileClass,
            fx = fx,
            recordCount = recordCount + 1,
            remittanceCount = remittanceCount,
            depositAmountSum = try {
                depositAmountSum + BigDecimal(record.get(DepositRecordField.TransactionAmount))
            } catch (e: IllegalArgumentException) {
                depositAmountSum
            },
            prevDepositHeader = prevDepositHeader
        )
        else -> this
    }
}

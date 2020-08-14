// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

import java.math.BigDecimal

/**
 * Preset Remittance Error Objects
 *
 * Enum class containing the standard error message corresponding to remittance validation checks
 *
 * @property error a FieldError object with descriptive normal and verbose messages
 */
enum class StandardRemittanceError(val error: FieldError) {
    DepositAccountNumber(FieldError("Deposit Account Number must be numeric and cannot exceed 32 " +
            "characters in length")),
    FileFormatVersion(FieldError("File Format Version was expected to be " +
            REMITTANCE_FILE_VERSION)),
    IncorrectNumberOfRemittanceRecords(FieldError("Number of Remittance Records in trailer does not match the file",
            "Number of Remittance Records in the trailer is incorrect (should be equal to the number of Deposit " +
                    """Header, "D", records for the remittance)""")),
    IncorrectNumberOfRecords(FieldError("Number of Records in deposit trailer is does not match the deposit",
            "Number of Records in deposit trailer is incorrect (should be equal to the number of Deposit " +
                    """Record, "R", entries for the deposit)""")),
    DifferingDepositDates(FieldError("The Deposit Date in the Deposit Trailer must match the " +
            "corresponding Deposit Header")),
    FXRate(FieldError("Transaction FX Rate must be specified if FX Rate is left blank in the Deposit Header"));

    companion object {
        fun notStandardMonetary(fieldName: String) = FieldError("$fieldName not a proper numeric decimal value",
                "$fieldName not a proper decimal value (must be numeric with at most 16 digits before the" +
                        "decimal and exactly 2 digits after")
        fun incorrectDepositAmountSum(headerValue: BigDecimal, recordSum: BigDecimal) =
                FieldError("The Deposit Amount in the header was $headerValue but the records summed to $recordSum")
    }
}

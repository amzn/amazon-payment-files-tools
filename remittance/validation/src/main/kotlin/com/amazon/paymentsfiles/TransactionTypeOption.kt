// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

/**
 * Remittance Class Enum
 *
 * Enum class representing the variation of remittance file format to determine special validation processes
 */
enum class RemittanceFileClass {
    Standard, DLocal
}

/**
 * Valid Transaction Types
 *
 * Enumerated values indicating valid choices for the Transaction Type record field, varies by RemittanceFileClass
 */
enum class TransactionTypeOption(val abbr: String, val fileClass: RemittanceFileClass = RemittanceFileClass.Standard) {
    Sale("S"),
    Refund("R"),
    Chargeback("C"),
    FeeInterchangeAssessment("FIA"),
    FeeProcessor("F"),
    FeeBusinessUnit("FBU"),
    Reject("REJ"),
    OtherAdjustment("ADJ"),
    Reversal("REV"),
    CurrencyExchangeTrade("T"),
    Retrocharge("RET"),
    Representment("REP"),
    Cancel("CAN"),
    ProfitPayment("P"),
    FeeWireTransfer("FWT"),
    Tax("TAX"),
    DLocalFXF("FXF", RemittanceFileClass.DLocal), // Figure out what this represents and what to call it
    ExchangeRateHedging("FX_HEDGING", RemittanceFileClass.DLocal);

    companion object {
        fun fetchOptions(fileClass: RemittanceFileClass? = null) =
                enumValues<TransactionTypeOption>().filter {
                    it.fileClass == RemittanceFileClass.Standard || it.fileClass == fileClass
                }
    }
}

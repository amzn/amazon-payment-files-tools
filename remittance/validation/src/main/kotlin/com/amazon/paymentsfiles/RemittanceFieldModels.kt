// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

/**
 * Remittance Fields by Record Type
 *
 * Enum classes holding all the fields for an AMZN 2.1 Remittance file (implement CSVFieldModel interface)
 */
enum class HeaderField(
    override val fieldName: String,
    override val required: Requirement,
    override val validation: ((String, String) -> FieldError?)?
) : CSVFieldModel {
    RecordType("Record Type", FieldRequired.Always, null),
    CreationDate("Creation Date", FieldRequired.Always, makeDateTimeParseCheck("yyyyMMdd")),
    CreationTime("Creation Time", FieldRequired.Never, makeDateTimeParseCheck("HHmmss")),
    FileFormatVersion("File Format Version", FieldRequired.Always, ::checkCorrectFormatVersion)
}

enum class DepositHeaderField(
    override val fieldName: String,
    override val required: Requirement,
    override val validation: ((String, String) -> FieldError?)?
) : CSVFieldModel {
    RecordType("Record Type", FieldRequired.Always, null),
    DepositDate("Deposit Date", FieldRequired.Always, makeDateTimeParseCheck("yyyyMMdd")),
    DepositAccountName("Deposit Account Name", FieldRequired.Always, makeLengthCheck(100)),
    DepositAccountNumber("Deposit Account Number", FieldRequired.Always, ::checkThirtyTwoNumeric),
    RemittanceVendorID("Remittance Vendor ID", FieldRequired.Always, null),
    EffectiveDepositDate("Effective Deposit Date", FieldRequired.Always, makeDateTimeParseCheck("yyyyMMdd")),
    BankTransferID("Bank Transfer ID", FieldRequired.Always, makeLengthCheck(100)),
    DepositCurrency("Deposit Currency", FieldRequired.Always, ::checkISOCurrency),
    DepositAmount("Deposit Amount", FieldRequired.Always, ::checkStandardMonetary),
    RemittanceRevision("Remittance Revision", FieldRequired.Always, ::checkInteger),
    FXPresentmentCurrency("FX Presentment Currency", FXFieldRequired.Header, ::checkISOCurrency),
    FXPresentmentAmount("FX Presentment Amount", FXFieldRequired.Header, ::checkStandardMonetary),
    FXRate("FX Rate", FXFieldRequired.ContextDependent, ::checkDouble)
}

enum class DepositRecordField(
    override val fieldName: String,
    override val required: Requirement,
    override val validation: ((String, String) -> FieldError?)?
) : CSVFieldModel {
    RecordType("Record Type", FieldRequired.Always, null),
    TransactionMethod("Transaction Method", FieldRequired.Always,
            makeChoiceCheck(enumValues<TransactionMethodOption>().map { it.abbr })),
    TransactionType("Transaction Type", FieldRequired.Always, null),
    TransactionID("Transaction ID", FieldRequired.Always, makeLengthCheck(100)),
    TransactionAmountCurrency("Transaction Amount Currency", FieldRequired.Always, ::checkISOCurrency),
    TransactionAmount("Transaction Amount", FieldRequired.Always, ::checkStandardMonetary),
    AmazonProcessingDivisionID("Amazon Processing Division ID", FieldRequired.Always, makeLengthCheck(30)),
    Direction("Direction", FXFieldRequired.Record, makeChoiceCheck(enumValues<DirectionOption>().map { it.abbr })),
    TransactionFXPresentmentCurrency("Transaction FX Presentment Currency", FXFieldRequired.Record,
            ::checkISOCurrency),
    TransactionFXPresentmentAmount("Transaction FX Presentment Amount", FXFieldRequired.Record,
            ::checkStandardMonetary),
    TransactionFXRate("Transaction FX Rate", FXFieldRequired.ContextDependent, ::checkDouble)
}

enum class DepositTrailerField(
    override val fieldName: String,
    override val required: Requirement,
    override val validation: ((String, String) -> FieldError?)?
) : CSVFieldModel {
    RecordType("Record Type", FieldRequired.Always, null),
    DepositDate("Deposit Date", FieldRequired.Always, makeDateTimeParseCheck("yyyyMMdd")),
    NumberOfRecords("Number of Records", FieldRequired.Always, ::checkInteger)
}

enum class TrailerField(
    override val fieldName: String,
    override val required: Requirement,
    override val validation: ((String, String) -> FieldError?)?
) : CSVFieldModel {
    RecordType("Record Type", FieldRequired.Always, null),
    NumberOfRemittanceRecords("Number of Remittance Records", FieldRequired.Always, ::checkInteger)
}

/**
 * Fields with enumerated possible values
 */
enum class DirectionOption(val abbr: String) {
    Deposit("D"),
    Withdrawal("W")
}

enum class TransactionMethodOption(val abbr: String) {
    CreditCard("C"),
    DirectDebit("D"),
    CashOnDelivery("COD"),
    ConvenienceStore("CVS"),
    RewardsAccount("P"),
    CarrierBilling("CB")
}

const val REMITTANCE_FILE_VERSION = "2.1"

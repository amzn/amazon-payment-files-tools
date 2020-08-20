// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

/**
 * Chargeback Column Names
 *
 * Enum class of all the fields in a chargeback file entry (implements CSVFieldModel interface)
 */
enum class ChargebackField(
    override val fieldName: String,
    override val required: Requirement,
    override val validation: ((String, String) -> FieldError?)?
) : CSVFieldModel {
    DisputeStatus(
        "Dispute Status",
        FieldRequired.Always,
        makeChoiceCheck(enumValues<DisputeStatusOption>().map { it.name })
    ),
    CaseNumber("Case Number", FieldRequired.Always, makeLengthCheck(30)),
    TransactionID("Transaction ID", FieldRequired.Always, makeLengthCheck(38)),
    DisputeTime("Dispute Time", FieldRequired.Always, ::checkIsoInstant),
    Currency("Currency", FieldRequired.Always, ::checkISOCurrency),
    DisputedAmount("Disputed Amount", FieldRequired.Always, ::checkDouble),
    Reason("Reason", FieldRequired.Always, makeChoiceCheck(enumValues<ReasonOption>().map { it.name })),
    RepresentmentDeadline("Representment Deadline", FieldRequired.Always, makeDateTimeParseCheck("yyyy-MM-dd")),
    ReasonDescription("Reason Description", FieldRequired.Never, ::checkReasonDescription);
}

/**
 * Fields with enumerated possible values
 */
enum class DisputeStatusOption {
    Won, Lost, NeedsResponse
}

enum class ReasonOption {
    Duplicate, Fraudulent, SubscriptionCanceled, ProductUnacceptable, ProductNotReceived, Unrecognized,
    CreditNotProcessed, General, Overcharged, ProcessingError, PaidbyOtherMeans, OrderCanceled
}

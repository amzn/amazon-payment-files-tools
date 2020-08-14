// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

/**
 * Record Type Enums
 *
 * Enums for easy access to all of the field names and/or when all of them can be left empty or not
 */
enum class RecordType(val charName: String, val fields: Array<out CSVFieldModel>) {
    Header("P", enumValues<HeaderField>()),
    DepositHeader("D", enumValues<DepositHeaderField>()),
    DepositRecord("R", enumValues<DepositRecordField>()),
    DepositTrailer("E", enumValues<DepositTrailerField>()),
    Trailer("T", enumValues<TrailerField>())
}

/**
 * Converts a record type field value from a remittance entry into a Record Type object or null if the record type
 * value is invalid
 */
fun String.toRecordType(): RecordType? = when (this) {
    "P" -> RecordType.Header
    "D" -> RecordType.DepositHeader
    "R" -> RecordType.DepositRecord
    "E" -> RecordType.DepositTrailer
    "T" -> RecordType.Trailer
    else -> null
}

/**
 * Returns the RecordType from a CSVEntry by taking the first element of the entry's map values
 */
fun CSVEntry.fetchRecordType(): RecordType = this.contents.values.first().toRecordType()!!

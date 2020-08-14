// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles.generation

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/*
 * Remittance Generation Formatters
 *
 * Minor format functions for use when generating remittance files from data
 */
internal fun formatDate(date: LocalDate): String = date.format(DateTimeFormatter.BASIC_ISO_DATE)

internal fun formatDate(dateTime: LocalDateTime): String = dateTime.format(DateTimeFormatter.BASIC_ISO_DATE)

internal fun formatTime(dateTime: LocalDateTime): String = dateTime.format(DateTimeFormatter.ofPattern("HHmmss"))

internal fun filterNull(field: Any?): String = field?.toString() ?: ""

internal fun formatMonetary(amount: BigDecimal): BigDecimal {
    if (amount.scale() > PROPER_FRACTIONAL_DIGITS || amount.precision() - amount.scale() > MAX_WHOLE_DIGITS)
        throw LargeDecimalException()
    return amount.setScale(PROPER_FRACTIONAL_DIGITS, RoundingMode.UNNECESSARY)
}

internal fun formatNullableMonetary(amount: BigDecimal?): String = amount?.let { formatMonetary(it).toString() } ?: ""

/*
 * Constant values holding the maximum size of a remittance monetary values (before and after the decimal place)
 * Drawn from the Remittance 2.1 specifications that declare monetary values of type Number(18,2)
 */
private const val MAX_WHOLE_DIGITS = 16
private const val PROPER_FRACTIONAL_DIGITS = 2

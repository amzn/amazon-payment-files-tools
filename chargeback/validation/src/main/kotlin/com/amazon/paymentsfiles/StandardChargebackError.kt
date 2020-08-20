// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

/**
 * Preset Chargeback Error Objects
 *
 * Enum class containing standard error messages corresponding to chargeback validation checks
 *
 * @property error a FieldError object with descriptive normal and verbose messages
 */
enum class StandardChargebackError(val error: FieldError) {
    DisputedAmountContext(
        FieldError(
            "Disputed Amount has incorrect number of decimal places",
            "Disputed Amount has incorrect number of decimal places (must match default decimal places for " +
                "the given ISO 4217 currency code)"
        )
    );

    companion object {
        fun notIsoInstant(fieldName: String) = FieldError(
            "$fieldName not proper RFC3339 date-time instant",
            "$fieldName not proper RFC3339 date-time instant (must be in UTC timezone with form " +
                "<YYYY-MM-DD>T<HH:MM:SS>Z)"
        )
    }
}

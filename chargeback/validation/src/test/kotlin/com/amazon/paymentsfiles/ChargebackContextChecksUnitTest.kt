// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.File

class ChargebackContextChecksUnitTest {

    private val testPath = "TstResources/Chargebacks/"

    @Nested
    inner class PositiveCases {

        @Test
        fun `Deposit Amount Context Check Returns Null with Proper Inputs`() {
            val reader = ChargebackReader(File(testPath + "Basic.csv"))
            Assertions.assertNull(checkCurrencyFields(reader.records.first()))
        }

        @Test
        fun `Deposit Amount Field Context Check Returns Null with Invalid Currency`() {
            val reader = ChargebackReader(File(testPath + "BadFields.csv"))
            Assertions.assertNull(checkCurrencyFields(reader.records.first()))
        }
    }

    @Nested
    inner class NegativeCases {

        @Test
        fun `Deposit Amount Field Check Returns FieldError Object When Expected`() {
            val reader = ChargebackReader(File(testPath + "ContextChecks.csv"))
            Assertions.assertTrue(checkCurrencyFields(reader.records.first()) is FieldError)
        }
    }
}

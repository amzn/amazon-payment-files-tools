// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles.chargeback.generation

import com.amazon.paymentsfiles.DisputeStatusOption
import com.amazon.paymentsfiles.ReasonOption
import java.io.File
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.Currency

class ChargebackWriterUnitTest {

    private val testDir = "TstResources/Chargebacks/"
    private val goodChargebackPath = testDir + "Basic.csv"
    private val testFilePath = testDir + "tmp"

    @Nested
    inner class PositiveCases {

        @Test
        fun `Basic Chargeback File Correctly Generated`() {
            val basicEntry = ChargebackEntry(
                DisputeStatusOption.Won,
                "CBK-15108-512-d5a04f70-f26b-45",
                "f7IVC1DgvDt0WeG9xDHn",
                Currency.getInstance("JPY"),
                BigDecimal("3400"),
                ReasonOption.Unrecognized,
                LocalDate.parse("2018-05-21"),
                disputeTime = Instant.parse("2020-04-15T20:05:30Z"),
                reasonDescription = "Customer doesn't recognize charge"
            )
            ChargebackWriter(testFilePath).use { writer -> writer.addEntry(basicEntry) }
            Assertions.assertEquals(File(goodChargebackPath).readText(), File(testFilePath).readText())
            File(testFilePath).delete()
        }
    }

    @Nested
    inner class NegativeCases {

        @Test
        fun `Header Not Written When No Entry Added`() {
            ChargebackWriter(testFilePath).use {}
            Assertions.assertEquals("", File(testFilePath).readText())
            File(testFilePath).delete()
        }
    }
}

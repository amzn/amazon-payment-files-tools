// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

import com.amazon.paymentsfiles.validation.ChargebackHeaderException
import com.amazon.paymentsfiles.validation.EmptyChargebackException
import java.io.File
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ChargebackReaderUnitTest {

    private val testPath: String = "TstResources/Chargebacks/"

    @Nested
    inner class PositiveCases {
        @Test
        fun `Proper Chargeback Files Pass All Structure Checks and is Iterable`() {
            ChargebackReader(File(testPath + "Basic.csv"))
            ChargebackReader(File(testPath + "LongGood.csv"))
        }

        @Test
        fun `Proper Chargeback File is Correctly Parsed into Entries`() {
            val chargeback = ChargebackReader(File(testPath + "Basic.csv"))
            val entry = chargeback.records.first()
            Assertions.assertEquals(2, entry.lineNo)
            Assertions.assertEquals("Won", entry.get(ChargebackField.DisputeStatus))
            Assertions.assertEquals("2018-05-21", entry.get(ChargebackField.RepresentmentDeadline))
        }
    }

    @Nested
    inner class NegativeCases {
        @Test
        fun `Mislabeled Header Throws a Header Exception`() {
            Assertions.assertThrows(ChargebackHeaderException::class.java) {
                ChargebackReader(File(testPath + "MislabeledHeader.csv"))
            }
        }

        @Test
        fun `Header with Too Many Columns Throws a Header Exception`() {
            Assertions.assertThrows(ChargebackHeaderException::class.java) {
                ChargebackReader(File(testPath + "ExtraHeaderColumn.csv"))
            }
        }

        @Test
        fun `Entry with Too Many Columns Throws an Entry Exception`() {
            Assertions.assertThrows(EntryFormatException::class.java) {
                ChargebackReader(File(testPath + "ExtraEntryColumn.csv"))
            }
        }

        @Test
        fun `Chargeback with No Entries Throws Empty Exception`() {
            Assertions.assertThrows(EmptyChargebackException::class.java) {
                ChargebackReader(File(testPath + "NoEntries.csv"))
            }
        }
    }
}

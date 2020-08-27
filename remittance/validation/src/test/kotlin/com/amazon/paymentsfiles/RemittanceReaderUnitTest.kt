// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

import com.amazon.paymentsfiles.validation.RecordTypeException
import com.amazon.paymentsfiles.validation.RemittanceStructureException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.File

class RemittanceReaderUnitTest {

    private val testPath: String = "TstResources/Remittance/"

    @Nested
    inner class PositiveCases {

        @Test
        fun `Proper Remittance File is Correctly Parsed into Entries`() {
            val remit = RemittanceReader(File(testPath + "Basic.csv"))

            /**
             * Map of fields to expected values (one for each line of the file)
             * Placed into an array instead of a map so that it can be indexed when iterating the remit.records sequence
             */
            val properFieldValues = arrayOf<Pair<CSVFieldModel, String>>(
                Pair(HeaderField.FileFormatVersion, "2.1"),
                Pair(DepositHeaderField.DepositCurrency, "MXN"),
                Pair(DepositRecordField.AmazonProcessingDivisionID, "9353302582"),
                Pair(DepositTrailerField.DepositDate, "20160302"),
                Pair(TrailerField.NumberOfRemittanceRecords, "1")
            )

            var counter = 0
            for (line in remit.records) {
                val checkedField = properFieldValues[counter].first
                val expectedValue = properFieldValues[counter].second
                Assertions.assertEquals(expectedValue, line.get(checkedField))
                counter++
            }
        }

        @Test
        fun `File Without Quotation Wrapping Can Pass Structure Tests`() {
            RemittanceReader(File(testPath + "NotWrapped.csv"))
        }
    }

    @Nested
    inner class NegativeCases {

        @Test
        fun `Invalid Record Type Throws Structure Record Type Exception`() {
            Assertions.assertThrows(RecordTypeException::class.java) {
                RemittanceReader(File(testPath + "UnknownRecordType.csv"))
            }
        }

        @Test
        fun `First Line Not a Header Throws Structure Exception`() {
            Assertions.assertThrows(RemittanceStructureException::class.java) {
                RemittanceReader(File(testPath + "NoHeader.csv"))
            }
        }

        @Test
        fun `Last Line Not a Trailer Throws Structure Exception`() {
            Assertions.assertThrows(RemittanceStructureException::class.java) {
                RemittanceReader(File(testPath + "NoTrailer.csv"))
            }
        }

        @Test
        fun `Deposit Trailer Following Deposit Header Throws Structure Exception`() {
            Assertions.assertThrows(RemittanceStructureException::class.java) {
                RemittanceReader(File(testPath + "NoRecords.csv"))
            }
        }

        @Test
        fun `File Header Too Long Throws a Entry Format Exception`() {
            Assertions.assertThrows(EntryFormatException::class.java) {
                RemittanceReader(File(testPath + "LongHeader.csv"))
            }
        }
    }
}

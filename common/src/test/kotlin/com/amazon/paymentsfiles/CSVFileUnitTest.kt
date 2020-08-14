// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.File

class CSVFileUnitTest {

    private val testPath: String = "TstResources/CSVFiles/"

    @Nested
    inner class PositiveCases {

        @Test
        fun `Proper CSV File is Correctly Parsed into Entries`() {
            val csv = CSVFile(File(testPath + "Basic.csv"))
            Assertions.assertEquals(5, csv.rows.size)
            Assertions.assertEquals("FirstColumn", csv.rows[0][0])
            Assertions.assertEquals("2", csv.rows[1][1])
            Assertions.assertEquals("7", csv.rows[3][2])
        }

        @Test
        fun `Wrapped CSV Correctly Parsed into Entries`() {
            val csv = CSVFile(File(testPath + "Wrapped.csv"), quotesExpected = true)
            Assertions.assertEquals(5, csv.rows.size)
            Assertions.assertEquals("FirstColumn", csv.rows[0][0])
            Assertions.assertEquals("2", csv.rows[1][1])
            Assertions.assertEquals("7", csv.rows[3][2])
        }

        @Test
        fun `White Space Trimmed from Fields`() {
            val csv = CSVFile(File(testPath + "Padded.csv"))
            for (row in csv.rows)
                for (field in row)
                    Assertions.assertFalse(field.contains("""\s""".toRegex()))
        }

        @Test
        fun `Generic Text File Allowed when Split is not Required`() {
            CSVFile(File(testPath + "LoremIpsum.csv"), mustBeSplit = false)
        }

        @Test
        fun `CSV with UTF-8 BOM Allowed`() {
            CSVFile(File(testPath + "BOM.csv"))
        }
    }

    @Nested
    inner class NegativeCases {
        @Test
        fun `Generic Text File Throws Format Exception`() {
            Assertions.assertThrows(CSVFormatException::class.java) {
                CSVFile(File(testPath + "LoremIpsum.csv"))
            }
        }

        @Test
        fun `Quotes Expected Flag Throws Format Exception on Normal CSV`() {
            Assertions.assertThrows(CSVFormatException::class.java) {
                CSVFile(File(testPath + "Basic.csv"), quotesExpected = true)
            }
        }

        @Test
        fun `CSV with UTF-8 BOM Throws Exception on Quotes Expected`() {
            Assertions.assertThrows(CSVFormatException::class.java) {
                CSVFile(File(testPath + "BOM.csv"), quotesExpected = true)
            }
        }
    }
}

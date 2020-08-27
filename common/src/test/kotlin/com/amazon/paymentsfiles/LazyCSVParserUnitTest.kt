// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.File

class LazyCSVParserUnitTest {

    private val testPath: String = "TstResources/CSVFiles/"

    private fun loopThroughLines(file: File, mustBeSplit: Boolean = true) {
        val csv = LazyCSVParser(file, mustBeSplit)
        for (line in csv.readLines())
            continue
    }

    @Nested
    inner class PositiveCases {

        @Test
        fun `Proper CSV File is Correctly Parsed into Entries`() {
            val csv = LazyCSVParser(File(testPath + "Basic.csv"))
            var counter = 0
            val expectedValues = arrayOf("SecondColumn", "2", "0", "6", "9")
            for (line in csv.readLines()) {
                Assertions.assertEquals(expectedValues[counter], line[1])
                counter++
            }
        }

        @Test
        fun `White Space Trimmed from Fields`() {
            val csv = LazyCSVParser(File(testPath + "Padded.csv"))
            for (line in csv.readLines())
                for (field in line)
                    Assertions.assertFalse(field.contains("""\s""".toRegex()))
        }

        @Test
        fun `Generic Text File Allowed when Split is not Required`() {
            loopThroughLines(File(testPath + "LoremIpsum.csv"), mustBeSplit = false)
        }

        @Test
        fun `CSV with UTF-8 BOM Allowed`() {
            loopThroughLines(File(testPath + "BOM.csv"))
        }
    }

    @Nested
    inner class NegativeCases {
        @Test
        fun `Generic Text File Throws Format Exception`() {
            Assertions.assertThrows(CSVFormatException::class.java) {
                loopThroughLines(File(testPath + "LoremIpsum.csv"))
            }
        }
    }
}

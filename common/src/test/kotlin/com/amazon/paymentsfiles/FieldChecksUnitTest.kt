// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class FieldChecksUnitTest {

    private val simpleFieldChecks = listOf(
        ::checkNonEmpty, ::checkInteger, ::checkDouble, ::checkISOCurrency, ::checkAllASCII
    )

    @Nested
    inner class PositiveCases {

        private fun checkReturnsNothing(returnObject: FieldError?) = Assertions.assertNull(returnObject)

        @TestFactory
        fun `Simple Field Checks Return Null with Proper Input`(): ArrayList<DynamicTest> {
            val goodCasesForSimple = listOf("nonempty", "500", "112.25", "USD", "175: This is a proper ASCII sentence.")

            val testArray = arrayListOf<DynamicTest>()
            for (i in simpleFieldChecks.indices) {
                testArray.add(
                    DynamicTest.dynamicTest("Good Case for ${simpleFieldChecks[i].name}") {
                        checkReturnsNothing(simpleFieldChecks[i]("fieldName", goodCasesForSimple[i]))
                    }
                )
            }
            return testArray
        }

        @Test
        fun `Check Length Returns Null with Proper Inputs`() {
            checkReturnsNothing(checkLength("fieldName", "Short message", 100))
            checkReturnsNothing(makeLengthCheck(12)("fieldName", "12Characters"))
        }

        @Test
        fun `Check Valid Choice Returns Null with Proper Inputs`() {
            checkReturnsNothing(checkValidChoice("fieldName", "A", listOf("A", "B", "C")))
            checkReturnsNothing(makeChoiceCheck(listOf("1", "2", "3", "4"))("fieldName", "4"))
        }

        @Test
        fun `Check Date Time Parse Returns Null with Proper Inputs`() {
            checkReturnsNothing(checkDateTimeParse("fieldName", "235959", "HHmmss"))
            checkReturnsNothing(makeDateTimeParseCheck("yyyy-MM-dd")("fieldName", "2020-07-21"))
        }
    }

    @Nested
    inner class NegativeCases {

        private fun checkReturnsError(returnObject: FieldError?) = Assertions.assertTrue(returnObject is FieldError)

        @TestFactory
        fun `Simple Field Checks Return FieldError Object with Improper Input`(): ArrayList<DynamicTest> {
            val badCasesForSimple = listOf("", "2f4b", "0x112.25", "ABC", "This is nöt a proper ASCII sentence")

            val testArray = arrayListOf<DynamicTest>()
            for (i in simpleFieldChecks.indices) {
                testArray.add(
                    DynamicTest.dynamicTest("Bad Case for ${simpleFieldChecks[i].name}") {
                        checkReturnsError(simpleFieldChecks[i]("fieldName", badCasesForSimple[i]))
                    }
                )
            }
            return testArray
        }

        @Test
        fun `Check Length Returns FieldError Object with Improper Inputs`() {
            checkReturnsError(checkLength("fieldName", "Verbose message", 10))
            checkReturnsError(makeLengthCheck(11)("FieldName", "12Characters"))
        }

        @Test
        fun `Check Valid Choice Returns FieldError Object with Improper Inputs`() {
            checkReturnsError(checkValidChoice("fieldName", "D", listOf("A", "B", "C")))
            checkReturnsError(makeChoiceCheck(listOf("1", "2", "3", "4"))("fieldName", "0"))
        }

        @Test
        fun `Check Date Time Parse Returns FieldError Object with Proper Inputs`() {
            checkReturnsError(checkDateTimeParse("fieldName", "250000", "HHmmss"))
            checkReturnsError(makeDateTimeParseCheck("yyyy-MM-dd")("fieldName", "2020-13-04"))
        }
    }
}

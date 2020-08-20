// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory

class RemittanceFieldChecksUnitTest {

    private val fieldChecks = listOf(::checkCorrectFormatVersion, ::checkThirtyTwoNumeric, ::checkStandardMonetary)

    @Nested
    inner class PositiveCases {

        @TestFactory
        fun `Simple Field Checks Return Null with Proper Input`(): ArrayList<DynamicTest> {
            val goodCasesForSimple = listOf("2.1", "1234567890", "112.25")

            val testArray = arrayListOf<DynamicTest>()
            for (i in fieldChecks.indices) {
                testArray.add(
                    DynamicTest.dynamicTest("Good Case for ${fieldChecks[i].name}") {
                        Assertions.assertNull(fieldChecks[i]("fieldName", goodCasesForSimple[i]))
                    }
                )
            }
            return testArray
        }
    }

    @Nested
    inner class NegativeCases {

        @TestFactory
        fun `Simple Field Checks Return FieldError Object with Improper Input`(): ArrayList<DynamicTest> {
            val badCasesForSimple = listOf("1.1", "2f4b", "112.256")

            val testArray = arrayListOf<DynamicTest>()
            for (i in fieldChecks.indices) {
                testArray.add(
                    DynamicTest.dynamicTest("Bad Case for ${fieldChecks[i].name}") {
                        val returnObject = fieldChecks[i]("fieldName", badCasesForSimple[i])
                        Assertions.assertTrue(returnObject is FieldError)
                    }
                )
            }
            return testArray
        }
    }
}

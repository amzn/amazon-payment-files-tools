// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles.validation

import com.amazon.paymentsfiles.ChargebackField
import com.amazon.paymentsfiles.FieldRequired
import com.amazon.paymentsfiles.contextChecks
import java.io.File
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ChargebackValidatorUnitTest {

    private val testPath = "TstResources/Chargebacks/"

    @Nested
    inner class PositiveCases {

        @Test
        fun `Short Correctly Formatted Chargeback File Raises No Errors nor Exceptions`() {
            Assertions.assertEquals(0, countErrors(testPath + "Basic.csv"))
        }

        @Test
        fun `Long Correctly Formatted Chargeback File Raises No Errors nor Exceptions`() {
            Assertions.assertEquals(0, countErrors(testPath + "LongGood.csv"))
        }
    }

    @Nested
    inner class NegativeCases {

        @Test
        fun `Empty Field Errors Detected`() {
            val requiredFieldCount = enumValues<ChargebackField>().filter { it.required == FieldRequired.Always }.size
            Assertions.assertEquals(requiredFieldCount, countErrors(testPath + "EmptyFields.csv"))
        }

        @Test
        fun `Invalid Field Errors Detected`() {
            val totalFieldCount = enumValues<ChargebackField>().size
            Assertions.assertEquals(totalFieldCount, countErrors(testPath + "BadFields.csv"))
        }

        @Test
        fun `Context Check Field Errors Detected`() {
            Assertions.assertEquals(contextChecks.size, countErrors(testPath + "ContextChecks.csv"))
        }
    }

    companion object {
        fun countErrors(fileName: String): Int {
            var errorCounter = 0
            for (errorLine in ChargebackValidator().validate(File(fileName)))
                for (e in errorLine.errors)
                    errorCounter++
            return errorCounter
        }
    }
}

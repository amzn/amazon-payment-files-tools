// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles.validation

import com.amazon.paymentsfiles.FXRequirements
import com.amazon.paymentsfiles.FieldRequired
import com.amazon.paymentsfiles.RecordType
import com.amazon.paymentsfiles.RemittanceFileClass
import com.amazon.paymentsfiles.depositTrailerContextChecks
import com.amazon.paymentsfiles.recordContextChecks
import com.amazon.paymentsfiles.trailerContextChecks
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.File

class RemittanceValidatorUnitTest {

    private val testPath = "TstResources/Remittance/"
    private val emptyErrorSuffix = "empty"

    @Nested
    inner class PositiveCases {

        @Test
        fun `Short Remittance with Empty FX Fields Raises No Errors nor Exceptions`() {
            Assertions.assertEquals(0, RemittanceValidator().countErrors(testPath + "OnlyRequired.csv"))
        }

        @Test
        fun `All-Records FX Raises No Errors nor Exceptions`() {
            val validator = RemittanceValidator(fx = FXRequirements.OnlyInRecords, quotesExpected = false)
            Assertions.assertEquals(0, validator.countErrors(testPath + "FXOnlyInRecords.csv"))
        }

        @Test
        fun `Additional Transaction Types Allowed for DLocal`() {
            val validator = RemittanceValidator(fileClass = RemittanceFileClass.DLocal, quotesExpected = false)
            Assertions.assertEquals(0, validator.countErrors(testPath + "DLocal.csv"))
        }
    }

    @Nested
    inner class NegativeCases {

        @Test
        fun `Empty Field Errors Detected`() {
            var countOfRequiredFields = 0
            for (recordType in enumValues<RecordType>())
                countOfRequiredFields += recordType.fields
                    .filter { it.required == FieldRequired.Always && it.fieldName != "Record Type" }
                    .size

            val validator = RemittanceValidator()
            Assertions.assertEquals(countOfRequiredFields, validator.countErrors(testPath + "EmptyFields.csv"))
        }

        @Test
        fun `Invalid Field Errors Detected`() {
            var countOfValidatedFields = 0
            for (recordType in enumValues<RecordType>())
                countOfValidatedFields += recordType.fields.filter { it.validation != null }.size

            val validator = RemittanceValidator(fx = FXRequirements.PayStationStandard, fileClass = RemittanceFileClass.DLocal)
            Assertions.assertEquals(countOfValidatedFields, validator.countErrors(testPath + "BadFields.csv"))
        }

        @Test
        fun `Context Check Field Errors Detected`() {
            val countOfContextChecks = recordContextChecks.size + depositTrailerContextChecks.size + trailerContextChecks.size
            val validator = RemittanceValidator(fx = FXRequirements.PayStationStandard, quotesExpected = false)
            Assertions.assertEquals(countOfContextChecks, validator.countErrors(testPath + "ContextChecks.csv"))
        }
    }

    companion object {
        private fun RemittanceValidator.countErrors(filePath: String): Int {
            var errorCounter = 0
            for (errorLine in this.validate(File(filePath)))
                for (e in errorLine.errors)
                    errorCounter++
            return errorCounter
        }
    }
}

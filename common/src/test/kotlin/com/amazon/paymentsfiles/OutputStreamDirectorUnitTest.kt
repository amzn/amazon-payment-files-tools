// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

import com.amazon.paymentsfiles.support.mockFileValidator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileOutputStream

class OutputStreamDirectorUnitTest {

    private val filePassOutput = "* * * * *\nChecking Errors for Basic.csv\n* * * * *\n\nPass: no errors found\n\n"
    private val fatalErrorOutput = "* * * * *\nChecking Errors for Basic.csv\n* * * * *\n\nFatal error: fatal error\n\n"
    private val fieldErrorOutput = "* * * * *\nChecking Errors for Basic.csv\n* * * * *\n\n1 error found in line #1:\n--> Error occurred\n\n"

    private fun verifyValidationOutput(fileValidator: FileValidator, desiredOutput: String, repetitions: Int = 1) {
        val outputFile = File("TstResources/tmp")
        val outputStream = OutputStreamDirector(FileOutputStream(outputFile))
        val fileToValidate = File("TstResources/CSVFiles/Basic.csv")

        Controller(fileValidator, outputStream).use {
            for (i in 0 until repetitions)
                it.validate(fileToValidate)
        }

        Assertions.assertEquals(desiredOutput, outputFile.readText())

        outputFile.delete()
    }

    @Nested
    inner class PositiveCases {

        @Test
        fun `File with No Errors Reports Pass Correctly`() {
            val fileValidator = mockFileValidator()
            verifyValidationOutput(fileValidator, desiredOutput = filePassOutput)
        }

        @Test
        fun `Multiple Files Can Be Reported Sequentially`() {
            val fileValidator = mockFileValidator()
            verifyValidationOutput(fileValidator, desiredOutput = filePassOutput + filePassOutput, repetitions = 2)
        }
    }

    @Nested
    inner class NegativeCases {

        @Test
        fun `File with Exception Reports Fatal Error Correctly`() {
            val fileValidator = mockFileValidator(throwFatalError = true)
            verifyValidationOutput(fileValidator, desiredOutput = fatalErrorOutput)
        }

        @Test
        fun `File with Field Error Reports Error Correctly`() {
            val fileValidator = mockFileValidator(fieldErrorCount = 1)
            verifyValidationOutput(fileValidator, desiredOutput = fieldErrorOutput)
        }
    }
}

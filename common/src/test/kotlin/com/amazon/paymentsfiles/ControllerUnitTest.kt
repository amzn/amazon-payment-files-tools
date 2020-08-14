package com.amazon.paymentsfiles

import com.amazon.paymentsfiles.support.mockFileValidator
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.File

class ControllerUnitTest {

    private val exampleFileName = "TstResources/CSVFiles/Basic.csv"
    private val outputDirector = mock<OutputStreamDirector>()

    private fun verifyController(fileValidator: FileValidator, reportNonFatalTimes: Int, reportFatalTimes: Int, setupTimes: Int = 1, cleanupTimes: Int = 1) {
        Controller(fileValidator, outputDirector).use {
            it.validate(File(exampleFileName))
        }
        verify(outputDirector, times(setupTimes)).setup(File(exampleFileName))
        verify(outputDirector, times(reportNonFatalTimes)).reportErrorLine(any())
        verify(outputDirector, times(reportFatalTimes)).reportFatalError(any())
        verify(outputDirector, times(cleanupTimes)).cleanup(File(exampleFileName))
    }

    @Nested
    inner class PositiveCases {
        @Test
        fun `Pass File When No Error Lines Returned`() {
            val fileValidator = mockFileValidator()
            verifyController(fileValidator, reportFatalTimes = 0, reportNonFatalTimes = 0)
        }
    }

    @Nested
    inner class NegativeCases {
        @Test
        fun `Report Errors When Error Lines Returned`() {
            val fileValidator = mockFileValidator(fieldErrorCount = 10)
            verifyController(fileValidator, reportFatalTimes = 0, reportNonFatalTimes = 10)
        }

        @Test
        fun `Chargeback Structure Exception Reported`() {
            val fileValidator = mockFileValidator(throwFatalError = true)
            verifyController(fileValidator, reportFatalTimes = 1, reportNonFatalTimes = 0)
        }
    }
}

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class RemittanceContextChecksUnitTest {

    @Nested
    inner class PositiveCases {

        @Test
        fun `FX Fields Filled Check Returns Null with Proper Input`() {
            Assertions.assertNull(runFXFieldsCheck(fx = FXRequirements.PayStationStandard, headerFXRate = "NonemptyField", recordFXRate = ""))
            Assertions.assertNull(runFXFieldsCheck(fx = FXRequirements.PayStationStandard, headerFXRate = "", recordFXRate = "NonemptyField"))
            Assertions.assertNull(runFXFieldsCheck(fx = FXRequirements.OnlyInRecords, headerFXRate = "", recordFXRate = "NonemptyField"))
        }

        @Test
        fun `Transaction Type Field Check Returns Null with Proper Input`() {
            Assertions.assertNull(runTransactionTypeCheck(fileClass = RemittanceFileClass.Standard, transactionType = "S"))
            Assertions.assertNull(runTransactionTypeCheck(fileClass = RemittanceFileClass.DLocal, transactionType = "FX_HEDGING"))
        }

        @Test
        fun `Deposit Date Check Returns Null with Matching Dates or Invalid Dates`() {
            Assertions.assertNull(runDepositDateCheck(headerDepositDate = "20200531", trailerDepositDate = "20200531"))
            Assertions.assertNull(runDepositDateCheck(headerDepositDate = "2020-05-31", trailerDepositDate = "2020-05-30"))
        }

        @Test
        fun `Number of Records Check Returns Null with Proper Value or Non-Integer`() {
            Assertions.assertNull(runNumberOfRecordsCheck(trailerNumberOfRecords = "19", recordCount = 19))
            Assertions.assertNull(runNumberOfRecordsCheck(trailerNumberOfRecords = "NotAnInteger", recordCount = 1))
        }

        @Test
        fun `Deposit Amount Check Returns Null with Proper Values or Non-Numeric`() {
            Assertions.assertNull(runDepositAmountCheck(headerDepositAmount = "368.99", depositAmountSum = BigDecimal("368.99")))
            Assertions.assertNull(runDepositAmountCheck(headerDepositAmount = "NonNumeric", depositAmountSum = BigDecimal("250.00")))
        }

        @Test
        fun `Number of Remittances Check Returns Null with Proper Value or Non-Integer`() {
            Assertions.assertNull(runNumberOfRemittancesCheck(trailerNumberOfRemittances = "7", remittanceCount = 7))
            Assertions.assertNull(runNumberOfRemittancesCheck(trailerNumberOfRemittances = "NotAnInteger", remittanceCount = 1))
        }
    }


    @Nested
    inner class NegativeCases {

        @Test
        fun `FX Fields Filled Returns Field Error with Improper Input`() {
            assertFieldError(runFXFieldsCheck(fx = FXRequirements.PayStationStandard, headerFXRate = "", recordFXRate = ""))
            assertFieldError(runFXFieldsCheck(fx = FXRequirements.OnlyInRecords, headerFXRate = "NonemptyField", recordFXRate = ""))
        }

        @Test
        fun `Transaction Type Field Check Returns Field Error with Improper Input`() {
            assertFieldError(runTransactionTypeCheck(fileClass = RemittanceFileClass.DLocal, transactionType = "Invalid"))
            assertFieldError(runTransactionTypeCheck(fileClass = RemittanceFileClass.Standard, transactionType = "FX_HEDGING"))
        }

        @Test
        fun `Deposit Date Check Returns Field Error when Values Don't Match`() {
            assertFieldError(runDepositDateCheck(headerDepositDate = "20200531", trailerDepositDate = "20200530"))
        }

        @Test
        fun `Number of Records Check Returns Field Error when Values Don't Match`() {
            assertFieldError(runNumberOfRecordsCheck(trailerNumberOfRecords = "5", recordCount = 3))
        }

        @Test
        fun `Deposit Amount Check Returns Field Error when Values Don't Match`() {
            assertFieldError(runDepositAmountCheck(headerDepositAmount = "369.00", depositAmountSum = BigDecimal("368.99")))
        }

        @Test
        fun `Number of Remittances Check Returns Field Error when Values Don't Match`() {
            assertFieldError(runNumberOfRemittancesCheck(trailerNumberOfRemittances = "11", remittanceCount = 4))
        }
    }


    companion object {
        private fun assertFieldError(checkReturn: FieldError?) =
                Assertions.assertTrue(checkReturn is FieldError)

        private fun buildCSV(contentsMap: Map<CSVFieldModel, String>): CSVEntry =
                CSVEntry(contentsMap.keys.toTypedArray(), contentsMap.values.toTypedArray())

        private fun runFXFieldsCheck(fx: FXRequirements, headerFXRate: String, recordFXRate: String): FieldError? {
            val internalState = RemittanceStats(
                fx = fx,
                prevDepositHeader = buildCSV(mapOf(DepositHeaderField.FXRate to headerFXRate))
            )
            val record = buildCSV(mapOf(DepositRecordField.TransactionFXRate to recordFXRate))
            return checkFXFilled(record, internalState)
        }

        private fun runTransactionTypeCheck(fileClass: RemittanceFileClass, transactionType: String): FieldError? {
            val internalState = RemittanceStats(fileClass = fileClass)
            val record = buildCSV(mapOf(DepositRecordField.TransactionType to transactionType))
            return checkTransactionType(record, internalState)
        }

        private fun runDepositDateCheck(headerDepositDate: String, trailerDepositDate: String): FieldError? {
            val internalState = RemittanceStats(
                prevDepositHeader = buildCSV(mapOf(DepositHeaderField.DepositDate to headerDepositDate))
            )
            val record = buildCSV(mapOf(DepositTrailerField.DepositDate to trailerDepositDate))
            return checkDepositDates(record, internalState)
        }

        private fun runNumberOfRecordsCheck(trailerNumberOfRecords: String, recordCount: Int): FieldError? {
            val internalState = RemittanceStats(
                recordCount = recordCount
            )
            val record = buildCSV(mapOf(DepositTrailerField.NumberOfRecords to trailerNumberOfRecords))
            return checkNumberOfRecords(record, internalState)
        }

        private fun runDepositAmountCheck(headerDepositAmount: String, depositAmountSum: BigDecimal): FieldError? {
            val internalState = RemittanceStats(
                depositAmountSum = depositAmountSum,
                prevDepositHeader = buildCSV(mapOf(DepositHeaderField.DepositAmount to headerDepositAmount))
            )
            return checkDepositAmount(buildCSV(mapOf()), internalState)
        }

        private fun runNumberOfRemittancesCheck(trailerNumberOfRemittances: String, remittanceCount: Int): FieldError? {
            val internalState = RemittanceStats(
                remittanceCount = remittanceCount
            )
            val record = buildCSV(mapOf(TrailerField.NumberOfRemittanceRecords to trailerNumberOfRemittances))
            return checkNumberOfRemittanceRecords(record, internalState)
        }
    }
}

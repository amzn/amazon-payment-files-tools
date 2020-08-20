// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles.generation

import com.amazon.paymentsfiles.DirectionOption
import com.amazon.paymentsfiles.FXRequirements
import com.amazon.paymentsfiles.PayProGenerationException
import com.amazon.paymentsfiles.TransactionMethodOption
import com.amazon.paymentsfiles.TransactionTypeOption
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.File
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Currency

class RemittanceWriterUnitTest {

    private val testDir = "TstResources/Remittance/"
    private val goodChargebackPath = testDir + "Basic.csv"
    private val testFilePath = testDir + "tmp"

    @Nested
    inner class PositiveCases {

        @Test
        fun `Basic Chargeback File Correctly Generated`() {
            val basicDepositHeader = DepositHeader(
                LocalDate.parse("2016-03-02"),
                "HSBC",
                "9353302582",
                "Amex",
                LocalDate.parse("2016-03-02"),
                "93533025820600018301",
                Currency.getInstance("MXN"),
                BigDecimal("375.49"),
                1,
                Currency.getInstance("USD"),
                BigDecimal("45.80"),
                BigDecimal("4.5903")
            )

            val basicDepositRecord = DepositRecord(
                TransactionMethodOption.CreditCard,
                TransactionTypeOption.FeeProcessor,
                "48QQB8H2J7L",
                Currency.getInstance("MXN"),
                BigDecimal("375.49"),
                "9353302582",
                DirectionOption.Deposit,
                Currency.getInstance("USD"),
                BigDecimal("1.50"),
                BigDecimal("12.34")
            )

            RemittanceWriter(testFilePath, LocalDateTime.parse("2016-03-01T03:54:05")).use {
                it.addDeposit(basicDepositHeader)
                it.addRecord(basicDepositRecord)
            }
            Assertions.assertEquals(File(goodChargebackPath).readText(), File(testFilePath).readText())
            File(testFilePath).delete()
        }

        @Test
        fun `No Exception Thrown when FX Checks Turned Off`() {
            RemittanceWriter(testFilePath, fxRequirements = FXRequirements.None).use {
                it.addDeposit(
                    DepositHeaderUnitTest.simpleDepositHeaderGenerator(
                        fxPresentmentCurrency = Currency.getInstance("USD"),
                        fxPresentmentAmount = BigDecimal("365")
                    )
                )
                it.addRecord(
                    DepositRecordUnitTest.simpleDepositRecordGenerator(
                        direction = DirectionOption.Deposit,
                        transactionFXCurrency = Currency.getInstance("USD"),
                        transactionFXAmount = BigDecimal("365")
                    )
                )
            }
            File(testFilePath).delete()
        }
    }

    @Nested
    inner class NegativeCases {

        @Test
        fun `Exception Thrown when Adding Record Before Deposit`() {
            Assertions.assertThrows(PayProGenerationException::class.java) {
                RemittanceWriter(testFilePath).use {
                    it.addRecord(DepositRecordUnitTest.simpleDepositRecordGenerator())
                }
            }
            File(testFilePath).delete()
        }

        @Test
        fun `Exception Thrown for FX Checks in PayStationStandard State`() {
            Assertions.assertThrows(PayProGenerationException::class.java) {
                RemittanceWriter(testFilePath).use {
                    it.addDeposit(
                        DepositHeaderUnitTest.simpleDepositHeaderGenerator(
                            fxPresentmentCurrency = Currency.getInstance("USD"),
                            fxPresentmentAmount = BigDecimal("365")
                        )
                    )
                    it.addRecord(
                        DepositRecordUnitTest.simpleDepositRecordGenerator(
                            direction = DirectionOption.Deposit,
                            transactionFXCurrency = Currency.getInstance("USD"),
                            transactionFXAmount = BigDecimal("365")
                        )
                    )
                }
            }
            File(testFilePath).delete()
        }

        @Test
        fun `Exception Thrown for FX Checks in OnlyInRecords State`() {
            Assertions.assertThrows(PayProGenerationException::class.java) {
                RemittanceWriter(testFilePath, fxRequirements = FXRequirements.OnlyInRecords).use {
                    it.addDeposit(DepositHeaderUnitTest.simpleDepositHeaderGenerator())
                    it.addRecord(
                        DepositRecordUnitTest.simpleDepositRecordGenerator(
                            // No direction
                            transactionFXCurrency = Currency.getInstance("USD"),
                            transactionFXAmount = BigDecimal("365"),
                            transactionFXRate = BigDecimal("1.00")
                        )
                    )
                }
            }
            File(testFilePath).delete()
        }

        @Test
        fun `Exception Thrown for Deposit Amount Not Matching`() {
            Assertions.assertThrows(PayProGenerationException::class.java) {
                RemittanceWriter(testFilePath, fxRequirements = FXRequirements.None).use {
                    it.addDeposit(DepositHeaderUnitTest.simpleDepositHeaderGenerator())
                    it.addRecord(
                        DepositRecordUnitTest.simpleDepositRecordGenerator(transactionAmount = BigDecimal("100.00"))
                    )
                }
            }
            File(testFilePath).delete()
        }
    }
}

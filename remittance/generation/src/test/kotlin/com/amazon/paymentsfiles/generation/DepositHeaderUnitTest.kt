// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles.generation

import com.amazon.paymentsfiles.EntryFieldException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Currency

class DepositHeaderUnitTest {

    @Nested
    inner class PositiveCases {

        @Test
        fun `Basic Deposit Header Throws No Exceptions`() {
            simpleDepositHeaderGenerator()
        }
    }

    @Nested
    inner class NegativeCases {

        @Test
        fun `Account Name Field Too Long Throws an Entry Field Exception`() {
            Assertions.assertThrows(EntryFieldException::class.java) {
                simpleDepositHeaderGenerator(depositAccountName = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz")
            }
        }

        @Test
        fun `Illegal Deposit Account Number Throws an Entry Field Exception`() {
            Assertions.assertThrows(EntryFieldException::class.java) {
                simpleDepositHeaderGenerator(depositAccountNumber = "000000000000x")
            }
        }

        @Test
        fun `Bank Transfer ID Field Too Long Throws an Entry Field Exception`() {
            Assertions.assertThrows(EntryFieldException::class.java) {
                simpleDepositHeaderGenerator(bankTransferID = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz")
            }
        }
    }

    companion object {
        fun simpleDepositHeaderGenerator(
            depositDate: LocalDate = LocalDate.now(),
            depositAccountName: String = "Wells Fargo",
            depositAccountNumber: String = "000000000000",
            remittanceVendorID: String = "EBANX",
            effectiveDepositDate: LocalDate = LocalDate.now(),
            bankTransferID: String = "ABC-0000",
            currency: Currency = Currency.getInstance("USD"),
            depositAmount: BigDecimal = BigDecimal("35.77"),
            remittanceRevision: Int = 1,
            fxPresentmentCurrency: Currency? = null,
            fxPresentmentAmount: BigDecimal? = null,
            fxRate: BigDecimal? = null
        ) = DepositHeader(
            depositDate, depositAccountName, depositAccountNumber, remittanceVendorID, effectiveDepositDate,
            bankTransferID, currency, depositAmount, remittanceRevision, fxPresentmentCurrency, fxPresentmentAmount,
            fxRate
        )
    }
}

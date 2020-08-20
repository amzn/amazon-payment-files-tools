// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles.generation

import com.amazon.paymentsfiles.DirectionOption
import com.amazon.paymentsfiles.EntryFieldException
import com.amazon.paymentsfiles.TransactionMethodOption
import com.amazon.paymentsfiles.TransactionTypeOption
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.Currency

class DepositRecordUnitTest {

    @Nested
    inner class PositiveCases {

        @Test
        fun `Basic Deposit Record Throws No Exceptions`() {
            simpleDepositRecordGenerator()
        }
    }

    @Nested
    inner class NegativeCases {

        @Test
        fun `Transaction ID Field Too Long Throws an Entry Field Exception`() {
            Assertions.assertThrows(EntryFieldException::class.java) {
                simpleDepositRecordGenerator(transactionID = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz")
            }
        }

        @Test
        fun `Illegal Deposit Account Number Throws an Entry Field Exception`() {
            Assertions.assertThrows(EntryFieldException::class.java) {
                simpleDepositRecordGenerator(processingDivisionID = "abcdefghijklmnopqrstuvwxyz012345")
            }
        }
    }

    companion object {
        fun simpleDepositRecordGenerator(
            transactionMethod: TransactionMethodOption = TransactionMethodOption.CreditCard,
            transactionType: TransactionTypeOption = TransactionTypeOption.Sale,
            transactionID: String = "ABC123456XYZ",
            transactionAmountCurrency: Currency = Currency.getInstance("USD"),
            transactionAmount: BigDecimal = BigDecimal("35.77"),
            processingDivisionID: String = "EBXAMZNRCO",
            direction: DirectionOption? = null,
            transactionFXCurrency: Currency? = null,
            transactionFXAmount: BigDecimal? = null,
            transactionFXRate: BigDecimal? = null
        ) = DepositRecord(
            transactionMethod, transactionType, transactionID, transactionAmountCurrency, transactionAmount,
            processingDivisionID, direction, transactionFXCurrency, transactionFXAmount, transactionFXRate
        )
    }
}

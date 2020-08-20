// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles.chargeback.generation

import com.amazon.paymentsfiles.DisputeStatusOption
import com.amazon.paymentsfiles.EntryFieldException
import com.amazon.paymentsfiles.ReasonOption
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.Currency

class ChargebackEntryUnitTest {

    fun simpleChargebackEntryGenerator(
        disputeStatus: DisputeStatusOption = DisputeStatusOption.Won,
        caseNumber: String = "CBK-15108-512-d5a04f70-f26b-45",
        transactionID: String = "f7IVC1DgvDt0WeG9xDHn",
        currency: Currency = Currency.getInstance("USD"),
        disputedAmount: BigDecimal = BigDecimal("100"),
        reason: ReasonOption = ReasonOption.Unrecognized,
        representmentDeadline: LocalDate = LocalDate.now(),
        disputeTime: Instant = Instant.now(),
        reasonDescription: String = ""
    ) = ChargebackEntry(
        disputeStatus, caseNumber, transactionID, currency, disputedAmount, reason, representmentDeadline,
        reasonDescription, disputeTime
    )

    @Nested
    inner class PositiveCases {

        @Test
        fun `Basic Chargeback Entry Throws No Exceptions`() {
            simpleChargebackEntryGenerator()
        }

        @Test
        fun `Disputed Amount is Properly Rounded for Currency Code`() {
            val cb1 = simpleChargebackEntryGenerator()
            val cb2 = simpleChargebackEntryGenerator(disputedAmount = BigDecimal("100.00"))
            Assertions.assertEquals(cb1.disputedAmount, cb2.disputedAmount)

            Assertions.assertThrows(EntryFieldException::class.java) {
                simpleChargebackEntryGenerator(disputedAmount = BigDecimal("100.004"))
            }
        }
    }

    @Nested
    inner class NegativeCases {

        @Test
        fun `Case Number Field Too Long Throws an Entry Field Exception`() {
            Assertions.assertThrows(EntryFieldException::class.java) {
                simpleChargebackEntryGenerator(caseNumber = "CBK-15108-512-d5a04f70-f26b-45-afewextracharactersinheretoviolatelength")
            }
        }

        @Test
        fun `Transaction ID Field Too Long Throws an Entry Field Exception`() {
            Assertions.assertThrows(EntryFieldException::class.java) {
                simpleChargebackEntryGenerator(transactionID = "f7IVC1DgvDt0WeG9xDHn-afewextracharactersinheretoviolatelength")
            }
        }

        @Test
        fun `Reason Description Field Too Long Throws an Entry Field Exception`() {
            Assertions.assertThrows(EntryFieldException::class.java) {
                simpleChargebackEntryGenerator(reasonDescription = "ViolationOfLengthRequirement--ViolationOfLengthRequirement--ViolationOfLengthRequirement")
            }
        }
    }
}

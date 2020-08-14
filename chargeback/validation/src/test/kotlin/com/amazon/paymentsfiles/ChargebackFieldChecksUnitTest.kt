// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ChargebackFieldChecksUnitTest {

    @Nested
    inner class PositiveCases {

        @Test
        fun `Iso_Instant Check Returns Null with Proper Inputs`() {
            Assertions.assertNull(checkIsoInstant("fieldName", "2020-05-27T13:28:51Z"))
        }

        @Test
        fun `Reason Description Check Returns Null with Proper Inputs`() {
            Assertions.assertNull(checkReasonDescription("fieldName", "A valid reason description with only ASCII chars"))
        }
    }

    @Nested
    inner class NegativeCases {

        @Test
        fun `Iso_Instant Check Returns FieldError Object with Improper Input`() {
            val returnObj: FieldError? = checkIsoInstant("fieldName", "2020-05-27T13:28:51")
            Assertions.assertTrue(returnObj is FieldError)
        }

        @Test
        fun `Reason Description Check Returns FieldError Object with Improper Input`() {
            val error1 = checkReasonDescription("fieldName", "This string is designed to be too long and exceed the maximum of fifty characters allowed")
            Assertions.assertTrue(error1 is FieldError)
            val error2 = checkReasonDescription("fieldName", "This string is not all ASCII ö")
            Assertions.assertTrue(error2 is FieldError)
            Assertions.assertFalse(error1?.normal == error2?.normal)
        }
    }
}

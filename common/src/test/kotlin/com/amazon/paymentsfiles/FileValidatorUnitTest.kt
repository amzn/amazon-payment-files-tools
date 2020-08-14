// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class FileValidatorUnitTest {

    enum class OptionalFieldModel(
            override val fieldName: String,
            override val required: Requirement = FieldRequired.Never,
            override val validation: ((String, String) -> FieldError?)? = ::checkInteger
    ) : CSVFieldModel {
        FirstColumn("FirstColumn"),
        SecondColumn("SecondColumn"),
        ThirdColumn("ThirdColumn")
    }

    @Nested
    inner class PositiveCases {

        @Test
        fun `Three Empty Optional Fields Return No Errors`() {
            val entry = CSVEntry(enumValues<OptionalFieldModel>(), arrayOf("", "", ""))
            val errorList = entry.validateFields()
            Assertions.assertEquals(0, errorList.size)
        }

        @Test
        fun `Three Valid Required Fields Return No Errors`() {
            val entry = CSVEntry(enumValues<OptionalFieldModel>(), arrayOf("5", "10", "200"))
            val errorList = entry.validateFields(mandatoryFields = setOf(FieldRequired.Never))
            Assertions.assertEquals(0, errorList.size)
        }
    }

    @Nested
    inner class NegativeCases {
        @Test
        fun `One Invalid Optional Field Returns An Error`() {
            val entry = CSVEntry(enumValues<OptionalFieldModel>(), arrayOf("5", "abc", "200"))
            val errorList = entry.validateFields()
            Assertions.assertEquals(1, errorList.size)
        }

        @Test
        fun `Three Empty Optional Fields Each Returns An Error`() {
            val entry = CSVEntry(enumValues<OptionalFieldModel>(), arrayOf("", "", ""))
            val errorList = entry.validateFields(mandatoryFields = setOf(FieldRequired.Never))
            Assertions.assertEquals(3, errorList.size)
        }
    }
}

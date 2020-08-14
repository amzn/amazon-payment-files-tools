// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CSVEntryUnitTest {

    enum class TestFieldModel(
            override val fieldName: String,
            override val required: Requirement = FieldRequired.Never,
            override val validation: ((String, String) -> FieldError?)? = null
    ) : CSVFieldModel {
        FirstColumn("FirstColumn"),
        SecondColumn("SecondColumn"),
        ThirdColumn("ThirdColumn"),
        FourthColumn("FourthColumn"),
    }

    private val fieldValues = arrayOf("1", "2", "3", "4")

    @Nested
    inner class PositiveCases {

        @Test
        fun `Field Names and Field Values Properly Associated`() {
            val entry = CSVEntry(enumValues<TestFieldModel>(), fieldValues)
            Assertions.assertEquals("1", entry.get(TestFieldModel.FirstColumn))
            Assertions.assertEquals("2", entry.get(TestFieldModel.SecondColumn))
            Assertions.assertEquals("3", entry.get(TestFieldModel.ThirdColumn))
            Assertions.assertEquals("4", entry.get(TestFieldModel.FourthColumn))
        }
    }

    @Nested
    inner class NegativeCases {

        @Test
        fun `Exception Thrown When Desired Field Not Present`() {
            val entry = CSVEntry(enumValues<TestFieldModel>(), fieldValues)
            val extraColumn = IndividualField("ExtraColumn")
            Assertions.assertThrows(ColumnNotFoundException::class.java) {
                entry.get(extraColumn)
            }
        }

        @Test
        fun `Exception Thrown When Columns and Values Have Different Lengths`() {
            val shortFieldValues = arrayOf("1", "2", "3")
            Assertions.assertThrows(FieldsAndValuesException::class.java) {
                CSVEntry(enumValues<TestFieldModel>(), shortFieldValues)
            }
        }
    }
}

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles.validation

import com.amazon.paymentsfiles.ChargebackReader
import com.amazon.paymentsfiles.ErrorLine
import com.amazon.paymentsfiles.FileValidator
import com.amazon.paymentsfiles.validateContext
import com.amazon.paymentsfiles.validateFields
import java.io.File

/**
 * Chargeback Validator Class
 *
 * Class implementing the File Validator interface that validates a file by checking fields against the chargeback
 * field models and a context check between the currency field and disputed amount field
 */
class ChargebackValidator : FileValidator {

    override fun validate(file: File): Sequence<ErrorLine> = sequence {
        val reader = ChargebackReader(file)
        for (record in reader.records) {
            val errors = listOf(record.validateFields(), record.validateContext()).flatten()
            if (errors.isNotEmpty())
                yield(ErrorLine(record.lineNo, errors))
        }
    }
}

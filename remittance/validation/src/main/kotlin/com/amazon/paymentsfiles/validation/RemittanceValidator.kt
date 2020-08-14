// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles.validation

import com.amazon.paymentsfiles.ErrorLine
import com.amazon.paymentsfiles.FXFieldRequired
import com.amazon.paymentsfiles.FXRequirements
import com.amazon.paymentsfiles.FieldError
import com.amazon.paymentsfiles.FileValidator
import com.amazon.paymentsfiles.RemittanceFileClass
import com.amazon.paymentsfiles.RemittanceReader
import com.amazon.paymentsfiles.RemittanceStats
import com.amazon.paymentsfiles.Requirement
import com.amazon.paymentsfiles.validateContext
import com.amazon.paymentsfiles.validateFields
import java.io.File

/**
 * Remittance Validation Tool
 *
 * Class that reports structural and field-level errors for remittance files
 *
 * @param fx an FXRequirements enum that determines which foreign exchange fields should be required
 * @param quotesExpected a Boolean indicating whether lines should be wrapped in quotation marks
 * for foreign exchange transactions
 */
class RemittanceValidator(
    val quotesExpected: Boolean = true,
    val fileClass: RemittanceFileClass = RemittanceFileClass.Standard,
    val fx: FXRequirements = FXRequirements.None
) : FileValidator {

    val mandatoryFields: Set<Requirement> = when (fx) {
        FXRequirements.PayStationStandard -> mutableSetOf(FXFieldRequired.Header, FXFieldRequired.Record)
        FXRequirements.OnlyInRecords -> mutableSetOf(FXFieldRequired.Record)
        FXRequirements.None -> mutableSetOf()
    }

    /**
     * Primary validation function that computes a sequence of error lines for a file using a lazy remittance parser
     */
    override fun validate(file: File): Sequence<ErrorLine> {
        var internalState = RemittanceStats(fileClass = fileClass, fx = fx)
        return sequence {
            for (record in RemittanceReader(file, quotesExpected).records) {
                val errors = arrayListOf<FieldError>()
                errors.addAll(record.validateFields(mandatoryFields))
                errors.addAll(record.validateContext(internalState))
                if (errors.size > 0)
                    yield(ErrorLine(record.lineNo, errors.toList()))
                internalState = internalState.fetchNextState(record)
            }
        }
    }
}

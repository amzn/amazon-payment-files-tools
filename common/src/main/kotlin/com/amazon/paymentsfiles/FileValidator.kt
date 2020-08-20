// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

import java.io.File

/**
 * File Validator Interface
 *
 * Interface for different types of CSV file readers. Requires implementation of fatal error structural checks and
 * individual line validation
 */
interface FileValidator {

    fun validate(file: File): Sequence<ErrorLine>
}

/**
 * CSVEntry extension function for looping through validation checks and generating errors for the error report
 * @param mandatoryFields a set of FieldRequired enum objects that should be interpreted as mandatory
 * @return a List of field errors to be added to the Validator error report (if there are any)
 */
fun CSVEntry.validateFields(mandatoryFields: Set<Requirement> = setOf()): List<FieldError> =
    this.contents.keys.mapNotNull { field ->
        if (field.required == FieldRequired.Always || field.required in mandatoryFields)
            checkNonEmpty(field.fieldName, this.get(field))
                ?: field.validation?.let { it(field.fieldName, this.get(field)) }
        else if (this.get(field) != "")
            field.validation?.let { it(field.fieldName, this.get(field)) }
        else
            null
    }

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles.support

import com.amazon.paymentsfiles.ErrorLine
import com.amazon.paymentsfiles.FieldError
import com.amazon.paymentsfiles.FileValidator
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever

/**
 * Function to mock the FileValidator interface to test the behavior of the controller and standard output director
 */
fun mockFileValidator(fieldErrorCount: Int = 0, throwFatalError: Boolean = false): FileValidator {
    val mock = mock<FileValidator>()
    val basicError = ErrorLine(1, arrayListOf(FieldError("Error occurred")))
    if (throwFatalError)
        whenever(mock.validate(any())).thenThrow(IllegalArgumentException("fatal error"))
    else
        whenever(mock.validate(any())).thenReturn(sequence {
            for (i in 0 until fieldErrorCount)
                yield(basicError)
        })
    return mock
}

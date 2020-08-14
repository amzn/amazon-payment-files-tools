// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

import java.io.File

/**
 * Validation Output Interface
 *
 * Interface for interpreting and reporting errors in a file validation process
 * Implements the Autoclosable interface
 */
interface OutputDirector : AutoCloseable {

    val verbose: Boolean

    fun setup(file: File)

    fun reportFatalError(e: Exception)

    fun reportErrorLine(errorLine: ErrorLine)

    fun cleanup(file: File)
}

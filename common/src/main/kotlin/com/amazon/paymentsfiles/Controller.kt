// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

import java.io.File

/**
 * Centralized Validation Tool
 *
 * @param validator an object that implements the FileValidator interface to parse a file with a particular file form
 * in mind and report format errors
 * @param outputDirector an object that implements the OutputDirector interface to write output in a particular
 * form to an output stream
 */
class Controller(private val validator: FileValidator, private val outputDirector: OutputDirector) : AutoCloseable {

    /**
     * Validate an input file and write a proper validation report
     * @param file a Java.io.File to parse and validate
     */
    fun validate(file: File) = try {
        outputDirector.setup(file)
        for (errorLine in validator.validate(file))
            outputDirector.reportErrorLine(errorLine)
    } catch (e: IllegalArgumentException) {
        outputDirector.reportFatalError(e)
    } finally {
        outputDirector.cleanup(file)
    }

    /**
     * Autoclosable close method to close the stream within the outputDirector
     */
    override fun close() {
        outputDirector.close()
    }
}

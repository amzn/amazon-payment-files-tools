// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

import com.github.ajalt.mordant.AnsiCode
import com.github.ajalt.mordant.TermColors
import java.io.File
import java.io.OutputStream
import java.io.OutputStreamWriter

/**
 * Director to Standard Output
 *
 * Class implementing ValidationOutputDirector that forwards a file validation to an output stream.  If the output
 * stream is the standard output (which is the default), it will add text styling
 */
class OutputStreamDirector(
    private val outputStream: OutputStream = System.out,
    override val verbose: Boolean = false
) : OutputDirector {

    private val writer = OutputStreamWriter(outputStream)
    private var errorCount = 0

    /**
     * Open-source tool for detecting the command window's color styling compatibility and styling the text if possible
     */
    private val t = TermColors()
    private fun AnsiCode.print(msg: String) {
        writer.append(if (outputStream == System.out) this(msg) else msg)
    }

    /**
     * Write a bold header to inform the reader of the file name being validated
     */
    override fun setup(file: File) {
        errorCount = 0
        t.bold.print("* * * * *\nChecking Errors for ${file.name}\n* * * * *\n\n")
    }

    /**
     * Report a fatal error in red and register an error was seen
     */
    override fun reportFatalError(e: Exception) {
        t.red.print("Fatal error: ${e.message}\n\n")
        errorCount++
    }

    /**
     * Print a line number and all corresponding field errors found there in red
     */
    override fun reportErrorLine(errorLine: ErrorLine) {
        errorCount++
        printErrorLineHeader(errorLine)
        for (e in errorLine.errors)
            printError(e, verbose)
        writer.append("\n")
    }

    private fun printErrorLineHeader(errorLine: ErrorLine) = errorLine.run {
        t.red.print("${errors.size} error${if (errors.size > 1) "s" else ""} found in line #$lineNo:\n")
    }

    private fun printError(error: FieldError, verbose: Boolean) =
        t.red.print("--> " + (if (verbose) error.verbose else error.normal) + "\n")

    /**
     * Inform the reader if no errors were found and then close the OutputStreamWriter
     */
    override fun cleanup(file: File) {
        if (errorCount == 0)
            reportPass()
    }

    /**
     * Autocloseable close method closes the stream writer and its underlying stream
     */
    override fun close() {
        writer.close()
    }

    private fun reportPass() = t.green.print("Pass: no errors found\n\n")
}

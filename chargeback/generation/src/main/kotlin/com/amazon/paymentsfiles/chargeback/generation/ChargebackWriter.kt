// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles.chargeback.generation

import com.amazon.paymentsfiles.ChargebackField
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.time.temporal.ChronoUnit

/**
 * Chargeback Generator Class
 *
 * Library class for the creation and population of proper chargeback files.  Consumer interface includes two
 * methods for adding entries to the file and for closing the file
 *
 * @param outputStream an OutputStream indicating where to write the chargeback lines
 * @constructor takes instead of an output stream a file name string to which the validator will attempt to create a
 * file output stream
 */
class ChargebackWriter(outputStream: OutputStream) : AutoCloseable {

    constructor(filePath: String) : this(FileOutputStream(filePath))

    private val writer: OutputStreamWriter = OutputStreamWriter(outputStream, Charsets.UTF_8)
    private var initialized: Boolean = false

    private fun initialize() {
        writeHeader()
        initialized = true
    }

    private fun writeHeader() = writer.write(enumValues<ChargebackField>().joinToString(",") { it.fieldName } + "\n")

    fun addEntry(entry: ChargebackEntry) {
        if (!initialized)
            initialize()
        writer.append("${entry.toCSVLine()}\n")
    }

    /**
     * com.amazon.paymentsfile.chargeback.generation.ChargebackEntry extension function to write the record's fields into a proper CSV line to be written to a file
     */
    private fun ChargebackEntry.toCSVLine(): String = "$disputeStatus,$caseNumber,$transactionID," +
        "${disputeTime.truncatedTo(ChronoUnit.SECONDS)},$currency,$disputedAmount,$reason," +
        "$representmentDeadline,$reasonDescription"

    override fun close() = writer.close()
}

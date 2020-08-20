// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles.generation

import com.amazon.paymentsfiles.FXRequirements
import com.amazon.paymentsfiles.REMITTANCE_FILE_VERSION
import com.amazon.paymentsfiles.RecordType
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.time.LocalDateTime

/**
 * Remittance Generator Class
 *
 * Library class for the creation and population of proper chargeback files.  Consumer interface includes two
 * methods for adding entries to the file and for closing the file
 *
 * @param outputStream an OutputStream indicating where to write the remittance entries
 * @param creationTime a LocalDateTime indicating when the remittance record was created (defaulted to now)
 * @param fxRequirements an FXRequirements enum determining which foreign exchange checks should be performed (defaulted
 * to no foreign exchange checks)
 * @constructor takes instead of an output stream a file name string to which the validator will attempt to create a
 * file output stream
 */
class RemittanceWriter @JvmOverloads constructor(
    outputStream: OutputStream,
    private val creationTime: LocalDateTime = LocalDateTime.now(),
    private val fxRequirements: FXRequirements = FXRequirements.PayStationStandard
) : AutoCloseable {

    @JvmOverloads
    constructor(
        filePath: String,
        creationTime: LocalDateTime = LocalDateTime.now(),
        fxRequirements: FXRequirements = FXRequirements.PayStationStandard
    ) : this(FileOutputStream(filePath), creationTime, fxRequirements)

    private val writer: OutputStreamWriter = OutputStreamWriter(outputStream, Charsets.UTF_8)
    private var initialized = false
    private var remittanceCount = 0
    private var deposit: Deposit? = null

    private fun initialize() {
        writeHeader()
        initialized = true
    }

    private fun writeHeader() = writer.write(
        """"${RecordType.Header.charName},${formatDate(creationTime)},""" +
            """${formatTime(creationTime)},$REMITTANCE_FILE_VERSION"${'\n'}"""
    )

    fun addDeposit(header: DepositHeader) {
        deposit?.close()
        deposit = Deposit(header, writer, fxRequirements)
        remittanceCount++
    }

    fun addRecord(record: DepositRecord) {
        if (!initialized)
            initialize()
        deposit?.processRecord(record) ?: throw NoDepositException()
    }

    override fun close() {
        if (initialized) {
            deposit?.close()
            writeTrailer()
        }
        writer.close()
    }

    private fun writeTrailer() = writer.append(""""${RecordType.Trailer.charName},$remittanceCount"${'\n'}""")
}

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

import com.amazon.paymentsfiles.validation.RecordTypeException
import com.amazon.paymentsfiles.validation.RemittanceStructureException
import java.io.File

/**
 * Lazy Remittance File Parser
 *
 * Class for lazily parsing files into entries with proper Remittance fields
 *
 * @param file a Java.io.File object to parse lazily
 * @param quotesExpected a Boolean indicating whether each line of the file should be wrapped in quotation marks,
 * defaults to true
 * @property records a sequence of parsed CSVEntry objects
 */
class RemittanceReader(file: File, quotesExpected: Boolean = true) {

    private val csv = LazyCSVParser(file, quotesExpected = quotesExpected)

    init {
        checkStructure()
    }

    /**
     * Iterable records attribute representing a sequence of the file's lines parsed into CSVEntry objects
     */
    val records: Sequence<CSVEntry> = sequence {
        var lineNo = 0
        for (values in csv.readLines()) {
            lineNo++
            val fields = values[0].toRecordType()!!.fields
            yield(CSVEntry(fields, values, lineNo))
        }
    }

    /**
     * Function that checks for fatal errors in the structure of records upon initialization
     * @throws RecordTypeException if a record has an invalid Record Type value
     * @throws RemittanceStructureException if record types are not ordered properly
     * @throws EntryFormatException if a line has an improper number of fields
     */
    private fun checkStructure() {
        var lineNo = 0
        var prevRecordType: RecordType? = null
        for (fields in csv.readLines()) {
            lineNo++
            val recordType = fields[0].toRecordType() ?: throw RecordTypeException(lineNo, fields[0])
            if (transitionStates[prevRecordType]?.contains(recordType) == false)
                throw RemittanceStructureException(lineNo, recordType, prevRecordType)
            if (fields.size != recordType.fields.size)
                throw EntryFormatException(lineNo, recordType.name, recordType.fields.size)
            prevRecordType = recordType
        }
        if (prevRecordType?.name != RecordType.Trailer.name)
            throw RemittanceStructureException(lineNo, null, prevRecordType)
    }

    companion object {
        /**
         * A finite state transaction map to dictate what is considered a proper remittance structure in terms of
         * the order in which records of various types are written
         */
        private val transitionStates = mapOf<RecordType?, Set<RecordType?>>(
                null to setOf(RecordType.Header),
                RecordType.Header to setOf(RecordType.DepositHeader),
                RecordType.DepositHeader to setOf(RecordType.DepositRecord),
                RecordType.DepositRecord to setOf(RecordType.DepositRecord, RecordType.DepositTrailer),
                RecordType.DepositTrailer to setOf(RecordType.DepositHeader, RecordType.Trailer)
        )
    }
}

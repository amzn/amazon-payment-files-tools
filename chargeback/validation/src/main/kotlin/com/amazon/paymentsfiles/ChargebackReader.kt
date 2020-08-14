// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

import com.amazon.paymentsfiles.validation.ChargebackHeaderException
import com.amazon.paymentsfiles.validation.EmptyChargebackException
import java.io.File

/**
 * Lazy Chargeback File Parser
 *
 * Class for lazily parsing files into entries with proper Chargeback fields
 *
 * @param file a Java.io.File object to parse lazily
 * @property records a sequence of parsed CSVEntry objects
 */
class ChargebackReader(file: File) {

    private val csv = LazyCSVParser(file)

    init {
        checkNonEmpty()
        checkHeader()
        checkStructure()
    }

    /**
     * Iterable records attribute representing a sequence of the file's lines parsed into CSVEntry objects
     */
    val records: Sequence<CSVEntry> = sequence {
        var lineNo = 1
        for (values in csv.readLines().drop(1)) {
            lineNo++
            yield(CSVEntry(enumValues<ChargebackField>(), values, lineNo))
        }
    }

    /**
     * Check that the given file has at least 2 lines (a header and at least one entry)
     * @throws EmptyChargebackException if the file has fewer than 2 lines
     */
    private fun checkNonEmpty() = try {
        csv.readLines().drop(1).first()
    } catch (e: NoSuchElementException) {
        throw EmptyChargebackException()
    }

    /**
     * Parse the first row of the CSV to ensure it matches the expected header for a chargeback file
     * @throws ChargebackHeaderException if the header does not match the expected appearance
     */
    private fun checkHeader() {
        val header = csv.readLines().first()
        val columnNames = enumValues<ChargebackField>().map { it.fieldName }
        if (header.size != columnNames.size)
            throw ChargebackHeaderException()
        for (i in header.indices)
            if (header[i] != columnNames[i])
                throw ChargebackHeaderException()
    }

    /**
     * Check the structure of each line matches the expected number of columns
     * @throws EntryFormatException if the row has an improper number of columns
     */
    private fun checkStructure() {
        var lineNo = 1
        for (values in csv.readLines().drop(1)) {
            lineNo++
            val fields = enumValues<ChargebackField>()
            if (values.size != fields.size)
                throw EntryFormatException(lineNo, "Chargeback", fields.size)
        }
    }
}

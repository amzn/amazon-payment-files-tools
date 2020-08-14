// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

import java.io.File

/**
 * CSV File Data Object
 *
 * Class for that parses comma-separated value files, removes BOMs, and organizes the data into entries
 *
 * @param file a java.io.File object to interpret
 * @param mustBeSplit a Boolean dictating whether lines are permitted to have no commas, defaults to true
 * @param quotesExpected a Boolean dictating whether parsed lines should be expected to be wrapped in quotation marks
 * @property rows an array-list of string arrays storing each row split at the commas
 * @throws CSVFormatException if a line has no commas and mustBeSplit is set to true
 */
class CSVFile(val file: File, val mustBeSplit: Boolean = true, val quotesExpected: Boolean = false) {

    val rows = file.readLines().mapIndexed { i, row -> clean(row, i, mustBeSplit, quotesExpected) }

    companion object {
        /**
         * Splits a line from a file into an array of field strings by removing surrounding quotation
         * marks if necessary and then splitting the string on commas, trimming surrounding white space
         * @throws CSVFormatException if the file is not wrapped in quotation marks when expected or if a line must
         * be split but has no commas
         */
        internal fun clean(line: String, lineNo: Int, mustBeSplit: Boolean, quotesExpected: Boolean): Array<String> {
            var clean: String = line
            if (quotesExpected) {
                if (line.first() != '"' || line.last() != '"')
                    throw CSVFormatException(lineNo, quotesExpected)
                clean = line.substring(1, line.length - 1)
            }
            if (mustBeSplit && !clean.contains(",".toRegex()))
                throw CSVFormatException(lineNo, quotesExpected)
            return clean.split(',').map { it.trim() }.toTypedArray()
        }
    }
}

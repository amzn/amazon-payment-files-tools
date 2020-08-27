// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

import java.io.File

/**
 * CSV File Data Object
 *
 * Parses comma-separated value files, removes BOMs, and organizes the data into entries
 *
 * @param file a [File] object to interpret
 * @param mustBeSplit a Boolean dictating whether lines are permitted to have no commas, defaults to true
 * @throws CSVFormatException if a line has no commas and mustBeSplit is set to true
 */
class CSVFile(val file: File, val mustBeSplit: Boolean = true) {

    /** list of string arrays storing each row split at the commas */
    val rows = file.readLines().mapIndexed { i, row -> clean(row, i, mustBeSplit) }

    companion object {
        /**
         * Splits a line from a file into an array of field strings by removing surrounding quotation
         * marks if necessary and then splitting the string on commas, trimming surrounding white space
         * @throws CSVFormatException if the file is not wrapped in quotation marks when expected or if a line must
         * be split but has no commas
         */
        internal fun clean(line: String, lineNo: Int, mustBeSplit: Boolean): Array<String> {
            var clean: String = line

            if (mustBeSplit && !clean.contains(",".toRegex()))
                throw CSVFormatException(lineNo)
            return clean.split(',').map { it.trim() }.toTypedArray()
        }
    }
}

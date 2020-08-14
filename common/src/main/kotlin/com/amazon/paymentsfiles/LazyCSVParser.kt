// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

import java.io.BufferedReader
import java.io.File

/**
 * Lazy Parser for CSV Files
 *
 * Class for that parses large comma-separated value files line by line and organizes the data into a sequence of
 * field arrays
 *
 * @param file a java.io.File object to interpret
 * @param mustBeSplit a Boolean dictating whether lines are permitted to have no commas, defaults to true
 * @param quotesExpected a Boolean dictating whether parsed lines should be expected to be wrapped in quotation marks
 * @property readLines a sequence of string arrays representing each row split at the commas
 * @throws CSVFormatException if a line has no commas and mustBeSplit is set to true
 */
class LazyCSVParser(val file: File, val mustBeSplit: Boolean = true, val quotesExpected: Boolean = false) {

    fun readLines(): Sequence<Array<String>> = sequence {
        val reader: BufferedReader = file.bufferedReader()
        var lineNo = 0
        for (line in reader.lineSequence()) {
            lineNo++
            yield(CSVFile.clean(line, lineNo, mustBeSplit, quotesExpected))
        }
        reader.close()
    }
}

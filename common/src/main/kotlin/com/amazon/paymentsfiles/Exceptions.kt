// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

/**
 * Custom exception type used by the CSVFile class for alerting that a file is not properly comma-separated
 */
class CSVFormatException(lineNo: Int, quotesExpected: Boolean = false, cause: Throwable? = null) :
    IllegalArgumentException(
        "line $lineNo is not comma-separated${if (quotesExpected) " or not wrapped in quotation marks" else ""}",
        cause
    )

/**
 * Custom exception type used by the CSV Entry class for use when a particular column is being requested from an entry
 * but that column is not present in the entry's map
 */
class ColumnNotFoundException(invalidColumn: String, cause: Throwable? = null) :
    IllegalArgumentException("""CSV entry has no attribute "$invalidColumn"""", cause)

/**
 * Custom exception type used when the CSV Entry class receives incompatible columns and values arrays
 */
class FieldsAndValuesException(cause: Throwable? = null) :
    IllegalArgumentException("Columns and values arrays passed to CSV Entry have different lengths", cause)

/**
 * Custom exception type used by CSV-type file classes for catching entries with an improper width
 */
class EntryFormatException(lineNo: Int, entryTypeName: String, columnCount: Int, cause: Throwable? = null) :
    IllegalArgumentException("the $entryTypeName entry at line $lineNo should have $columnCount columns", cause)

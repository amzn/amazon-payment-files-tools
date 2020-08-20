// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles.validation

import com.amazon.paymentsfiles.RecordType

/**
 * Custom exception type used by the Remittance class for alerting that a row's Record Type is invalid
 */
class RecordTypeException(lineNo: Int, invalidRecordType: String, cause: Throwable? = null) : IllegalArgumentException(
    """the record type "$invalidRecordType" at line $lineNo is invalid--valid """ +
        "record types are " + enumValues<RecordType>().joinToString { it.charName + " for " + it.name },
    cause
)

/**
 * Custom exception type used by the Remittance class when two incompatible entry types are placed adjacent in the file
 */
class RemittanceStructureException(lineNo: Int, type: RecordType?, prevType: RecordType?, cause: Throwable? = null) :
    IllegalArgumentException(
        when {
            type == null -> """the file must end with a Trailer, "T", entry"""
            prevType == null -> """the file must begin with a Header, "P", entry"""
            else -> """the "$type" entry at line $lineNo cannot follow a "$prevType" entry"""
        },
        cause
    )

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

/**
 * Error Line Data Class
 *
 * Object for holding a line of field errors from a file validation tool and the corresponding line number where the
 * errors were found
 */
data class ErrorLine(val lineNo: Int, val errors: Collection<FieldError>)

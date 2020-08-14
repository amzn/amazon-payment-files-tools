// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

/**
 * Root custom run-time exception type used by the generation libraries to help users identify the source of
 * any reported exceptions
 */
open class PayProGenerationException(msg: String, cause: Throwable? = null) : RuntimeException(msg, cause)

/**
 * Custom exception thrown in the generation libraries when an invalid entry is passed into a data class
 * specific to the file type
 */
open class EntryFieldException(msg: String, cause: Throwable? = null) : PayProGenerationException(msg, cause)

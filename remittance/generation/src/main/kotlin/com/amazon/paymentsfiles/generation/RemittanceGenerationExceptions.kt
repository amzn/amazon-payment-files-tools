// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles.generation

import com.amazon.paymentsfiles.EntryFieldException
import com.amazon.paymentsfiles.PayProGenerationException

/**
 * Custom exception thrown during remittance generation when the user tries to add an entry before creating a deposit
 */
class NoDepositException(cause: Throwable? = null) :
        PayProGenerationException("A deposit must be created before a record can be added", cause)

/**
 * Custom exception thrown during remittance generation when the user submits a BigDecimal object that is too large
 */
class LargeDecimalException(cause: Throwable? = null) :
        EntryFieldException("Big decimal cannot exceed 16 digits before the decimal nor 2 digits after", cause)

/**
 * Custom exception thrown during remittance generation when a header deposit amount does not match the sum of the
 * transaction amounts associated with that deposit
 */
class RecordAmountSumException(cause: Throwable? = null) :
        PayProGenerationException("The deposit amount specified in the header does not match " +
                "the subsequent records' amounts", cause)

/**
 * Custom exceptions thrown during remittance generation when foreign exchange fields are required but not provided
 */
class FXFieldsNotProvidedException(cause: Throwable? = null) :
        EntryFieldException("Foreign exchange fields expected but not provided", cause)

class FXRateNotProvidedException(cause: Throwable? = null) :
        PayProGenerationException("Foreign exchange rate not provided for the deposit or individual transaction", cause)

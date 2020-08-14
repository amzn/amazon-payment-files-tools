// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles.validation

import com.amazon.paymentsfiles.ChargebackField

/**
 * Custom exception type used by the Chargeback class for catching invalid or rearranged column header values
 */
class ChargebackHeaderException(cause: Throwable? = null) :
        IllegalArgumentException("the specified header is incorrect--proper header names are " +
                enumValues<ChargebackField>().joinToString { it.fieldName }, cause)

/**
 * Custom exception type used by the Chargeback class for alerting that a chargeback has are no entries
 */
class EmptyChargebackException(cause: Throwable? = null) :
        IllegalArgumentException("the specified chargeback has no entries", cause)

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

/**
 * Enum class utilized in validation for determining whether foreign exchange fields are expected to be in deposit
 * headers and/or in deposit records
 */
enum class FXRequirements {
    PayStationStandard, OnlyInRecords, None
}

/**
 * Enum class implementing Requirement interface allowing more flexibility in marking whether an FX field is required
 * to be nonempty
 */
enum class FXFieldRequired : Requirement {
    Header, Record, ContextDependent
}

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles

/**
 * Fields in a CSV Row type
 *
 * Interface for demarcating all fields in a type of CSV row for validation
 * @property fieldName the proper field name in String form
 * @property required a FieldRequired object representing when that field is required to be nonempty
 * @property validation a nullable validation function that returns a FieldError object if failed
 */
interface CSVFieldModel {
    val fieldName: String
    val required: Requirement
    val validation: ((String, String) -> FieldError?)?
}

class IndividualField(
    override val fieldName: String,
    override val required: Requirement = FieldRequired.Never,
    override val validation: ((String, String) -> FieldError?)? = null
) : CSVFieldModel

/**
 * Interface reflecting when a field can or cannot be left empty
 */
interface Requirement {
    val name: String
}

/**
 * Basic field requirements
 */
enum class FieldRequired : Requirement {
    Always, Never
}

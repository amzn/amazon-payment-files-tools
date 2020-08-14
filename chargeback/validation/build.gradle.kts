// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

plugins {
    application
}

dependencies {
    implementation("com.github.ajalt:clikt:2.8.0")

    implementation(project(":common"))
}

application {
    mainClass.set("com.amazon.paymentsfiles.validation.CLIKt")
    applicationName = "validate-dispute"
}

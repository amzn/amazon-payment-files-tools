// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.amazon.paymentsfiles.validation

import com.amazon.paymentsfiles.Controller
import com.amazon.paymentsfiles.FXRequirements
import com.amazon.paymentsfiles.OutputStreamDirector
import com.amazon.paymentsfiles.RemittanceFileClass
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.file

/**
 * Command Line Tool
 *
 * Inherits from the open-source Clikt package for Kotlin. Defines the options and arguments to be specified from
 * the command line and throws errors for invalid arguments
 *
 * @property verbose a Boolean flag option to turn on longer error messages
 * @property fx a Boolean flag option to require extra foreign exchange field checks
 * @property files a list of File objects parsed from the command line
 */
class CLI : CliktCommand() {

    private val verbose: Boolean by option("-v", "--verbose", help = "Provides more context with error messages")
        .flag()

    private val fx: String by option("-fx", help = "Adds additional checks on foreign exchange transaction fields")
        .choice("standard", "onlyRecords")
        .default("none")

    private val fileClass: String by option(
        "--file-class", "-fc", help = "Adjust the validation process for specific file sources"
    ).choice("DLocal", "Standard").default("Standard")

    private val files by argument(help = "Any number of valid file paths to validate")
        .file(mustExist = true, canBeFile = true, canBeDir = false)
        .multiple(required = true)

    private fun fetchFX(): FXRequirements = when (fx) {
        "standard" -> FXRequirements.PayStationStandard
        "onlyRecords" -> FXRequirements.OnlyInRecords
        else -> FXRequirements.None
    }

    private fun fetchFileClass(): RemittanceFileClass = when (fileClass) {
        "DLocal" -> RemittanceFileClass.DLocal
        else -> RemittanceFileClass.Standard
    }

    override fun run() {
        val validator = RemittanceValidator(fetchFileClass(), fetchFX())
        Controller(validator, OutputStreamDirector(verbose = verbose)).use {
            for (file in files)
                it.validate(file)
        }
    }
}

/**
 * Executable Main Access
 *
 * A public static void main function to read command line arguments before passing them to a CLI object for processing
 *
 * @param args an array of strings into which command line arguments are read
 */
fun main(args: Array<String>) = CLI().main(args)

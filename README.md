# Amazon Payments Files Tools

PayProSDK is a software development kit for payment processors and Amazon
payment processor integration teams. It is designed to help facilitate the
process of generating and validating proper dispute and remittance files.
Processors as well as processor integration teams can access the package
through the external GitHub release. 

## Validating Dispute Chargeback files

Run the validation tool like:

```
./validate-dispute [FILES]
```

## Validating Remittance files

Run the validation tool like:

```
./validate-remittance [-no-wrap] [-fx] [FILES]
```

## Generating Dispute Chargeback files

Example code listing:

```kotlin
import com.amazon.paymentsfiles.*

import java.math.BigDecimal
import java.time.*
import java.util.*

val entry = ChargebackEntry(
    disputeStatus = DisputeStatusOption.Won,
    caseNumber = "Chargeback-00001",
    transactionID = "abcdef0000001",
    currency = Currency.getInstance("USD"),
    disputedAmount = BigDecimal("100.00"),
    reason = ReasonOption.Unrecognized,
    representmentDeadline = LocalDate.parse("2020-01-31"),
    reasonDescription = "Customer doesn't recognize charge",
    disputeTime = Instant.parse("2020-01-01T00:00:00Z")
)

ChargebackWriter("/Users/name/Desktop/AccountingReports/chargeback.csv").use {
    it.addEntry(entry)
}
```

## Generating Remittance files

```kotlin
val depositHeader = DepositHeader(
    depositDate = LocalDate.parse("2016-03-02"),
    depositAccountName = "HSBC",
    depositAccountNumber = "9353302582",
    remittanceVendorID = "Amex",
    effectiveDepositDate = LocalDate.parse("2016-03-02"),
    bankTransferID = "1234567890",
    depositCurrency = Currency.getInstance("MXN"),
    depositAmount = BigDecimal("375.49")
)

val depositRecord = DepositRecord(
    transactionMethod = TransactionMethodOption.CreditCard,
    transactionType = TransactionTypeOption.FeeProcessor,
    transactionID = "abc1234",
    transactionAmountCurrency = Currency.getInstance("MXN"),
    TransactionAmount = BigDecimal("375.49"),
    processingDivissionID = "1234567890"
)

RemittanceWriter(testFilePath, LocalDateTime.parse("2016-03-01T03:54:05")).use {
    it.addDeposit(depositHeader)
    it.addRecord(depositRecord)
}
```

## Security

See [CONTRIBUTING](CONTRIBUTING.md#security-issue-notifications) for more information.

## License

This project is licensed under the Apache-2.0 License.


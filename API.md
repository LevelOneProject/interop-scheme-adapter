# Scheme Adapter API
***

This document has details about the different endpoints that Scheme Adapter exposes:
* `POST` [**Create Invoice Notification**](#post-invoice-notification)
* `GET` [**Get Invoice**](#get-invoice)
* `POST` [**Create Quote**](#create-quote)
* `POST` [**Submit Payment**](#submit-payment)
* `GET` [**Get ilp address**](#get-ilp-address)
* `GET` [**Health check**](#health-check)

The different endpoints often deal with these [data structures:](#data-structures)
* [**Invoice Object**](#invoice-object)
* [**Quote Object**](#quote-object)
* [**Payment Object**](#payment-object)

Information about various errors returned can be found here:
* [**Error Information**](#error-information)

***

## Endpoints

### **Post Invoice Notification**
This endpoint allows a merchant to post invoice notifications to the payer.

#### HTTP Request
```POST http://host:8088/scheme/adapter/v1/invoices```


#### Headers
| Field | Type | Description |
| ----- | ---- | ----------- |
| Content-Type | String | Must be set to `application/json` |

#### Request body
[**Invoice Object**](#invoice-object)

#### Response 201 Created


#### Request
``` http
POST http://host:8088/scheme/adapter/v1/invoices HTTP/1.1
Content-Type: application/json
{
  "invoiceId": "3",
  "submissionUrl": "receiver-dfsp-host:8088/scheme/adapter/v1",
  "senderIdentifier": "78956562",
  "memo": "Invoice from merchant for 100 USD"
}
```

#### Response
``` http
HTTP/1.1 201 CREATED
```

#### Errors (4xx)
| Field | Description |
| ----- | ----------- |

### **Get Invoice**
This endpoint allows retrieval of the invoice based on ID. Query Parameter invoiceUrl indicates the location of invoice on the merchant side.

#### HTTP Request
```GET http://host:8088/scheme/adapter/v1/invoices?invoiceUrl=http://merchant-dfsp-host:8088/scheme/adapter/v1/invoices/123```


#### Response 200 OK
[**Invoice Object**](#invoice-object)

### **Create Quote**
This endpoint allows a DFSP to request for a quote.

#### HTTP Request
```POST http://scheme-adapter/quotes```

#### Headers
| Field | Type | Description |
| ----- | ---- | ----------- |
| Content-Type | String | Must be set to `application/json` |

#### Request body
| Name | Type | Description |
| ---- | ---- | ----------- |
| paymentId | UUID | Unique Identifier |
| **payer** | **Object** | **Person initiating the quote** |
| payer.identifier | String | Payer Id |
| payer.identifierType | String | Central Directory Registry Type |
| payer.url | URL | Base URL at which the payer information can be retrieved from |
| payer.account | String | Payer Ledger Account |
| **payee** | **Object** | **Person for whom the quote is being requested for** |
| payee.identifier | String | Payee Id |
| payee.identifierType | String | Central Directory Registry Type |
| payee.url | URL | Base URL at which the payee information can be retrieved from |
| payee.account | String | Payee Ledger Account |
| amountType | String | Should be either "SEND" or "RECEIVE" |
| amount | Object | Amount Object |
| amount.amount | number | Amount  |
| amount.currency | String | defaulted to "USD" |
| fees | Object | Payer Fees Object |
| fees.amount | number | Amount  |
| fees.currency | String | defaulted to "USD" |
| transferType | String | defaulted to "p2p" |


#### Response 201 Created
| Name | Type | Description |
| ---- | ---- | ----------- |
| paymentId | UUID | Unique Identifier |
| receiveAmount | Object | Amount Object |
| receiveAmount.amount | number | Amount  |
| receiveAmount.currency | String | defaulted to "USD" |
| payeeFee | Object | Payee Fees Object |
| payeeFee.amount | number | Amount  |
| payeeFee.currency | String | defaulted to "USD" |
| payeeCommission | Object | Payee Commission Object |
| payeeCommission.amount | number | Amount  |
| payeeCommission.currency | String | defaulted to "USD" |
| connectorAccount | String | Ledger account URI of the connector through which this payment can be sent. |
| ipr | String | Interledger Payment Request |
| sourceExpiryDuration | number | Number of seconds after the payment is submitted that the outgoing transfer will expire. |


#### Request
``` http
POST http://scheme-adapter-host:8088/scheme/adapter/v1/quotes HTTP/1.1
Content-Type: application/json
{
   "paymentId": "110ec58a-a0f2-4ac4-8393-c866d813b8d1",
   "payer": {
     "identifier": "92806391",
     "identifierType": "eur"
   },
   "payee": {
     "identifier": "30754016",
     "identifierType": "eur",
     "url": "ec2-34-194-186-111.compute-1.amazonaws.com:8088/scheme/adapter/v1",
     "account": "http://ec2-34-194-186-111.compute-1.amazonaws.com:8088/ilp/ledger/v1/accounts/alice"
   },
   "transferType": "p2p",
   "amountType": "SEND",
   "amount": {
     "amount": "10",
     "currency": "USD"
   },
   "fees": {
     "amount": "0.25",
     "currency": "USD"
   }
}
```

#### Response
``` http
HTTP/1.1 201 CREATED
Content-Type: application/json
{
      "paymentId": "110ec58a-a0f2-4ac4-8393-c866d813b8d1",
      "receiveAmount": {
        "amount": "9.25",
        "currency": "USD"
      },
      "payeeFee": {
        "amount": "1",
        "currency": "USD"
      },
      "payeeCommission": {
        "amount": "1",
        "currency": "USD"
      },
      "connectorAccount":"http://host:port/scheme/adapter/v1/ilp/ledger/v1/accounts/connector-account",
      "ipr": "c29tZSBpcHIgaGVyZQ==",
      "sourceExpiryDuration": "10"
  }
```

#### Errors


### **Submit Payment**
This endpoint is used to submit p2p and invoice payments. This is a pass through of the underlying [ilp-service](https://github.com/LevelOneProject/ilp-service)  /payIPR endpoint

#### HTTP Request
```POST http://scheme-adapter/payment```

#### Request body
| Name | Type | Description |
| ---- | ---- | ----------- |
| paymentId | UUID | Unique Identifier |
| ipr | String | Interledger payment request. This value is returned as part of quote call |
| sourceAmount | number | Amount that the sender will send. Depending on amount type of SEND/RECEIVE this value is calculated as part of quote |
| sourceAccount | URI | Ledger account URI to send the transfer from. |
| connectorAccount | URI | Ledger account URI of the connector through which this payment will be sent. |
| sourceExpiryDuration | Integer | Number of seconds after the payment is submitted that the outgoing transfer will expire.|


#### Response 200 OK
| Field | Type | Description |
| ----- | ---- | ----------- |
| paymentId | UUID | Unique Identifier |
| connectorAccount | URI | Ledger account URI of the connector through which this payment will be sent. |
| status | string | executed, rejected, expired|
| rejectionMessage | JSON Object | Only present when status is rejected.|
| fulfillment | Base64-URL String | Only present when status is executed|


#### Request
``` http
POST http://scheme-adapter-host:8088/scheme/adapter/v1/payment HTTP/1.1
Content-Type: application/json
{
   "paymentId": "110ec58a-a0f2-4ac4-8393-c866d813b8d1",
   "ipr":"",
   "sourceAmount":"10",
   "sourceAccount":"",
   "connectorAccount":"",
   "sourceExpiryDuration":"10"
}
```

#### Response
``` http
HTTP/1.1 201 CREATED
Content-Type: application/json
{

}
```

#### Errors (4xx)
| Field | Description |
| ----- | ----------- |
| NotFoundError | The requested payment could not be found. |
``` http
{
  "id": "NotFoundError",
  "message": "The requested resource could not be found."
}
```


#### **Health Check**
Get the current status of the service

##### HTTP Request
`GET http://scheme-adapter/health`

##### Response 200 OK
| Field | Type | Description |
| ----- | ---- | ----------- |
| status | String | The status of the service, *OK* if the service is working |

##### Request
``` http
GET http://scheme-adapter/health HTTP/1.1
```

##### Response
``` http
HTTP/1.1 200 OK
{
  "status": "OK"
}
```

***

## Data Structures

### Invoice Object

A resource represents the information returned about an identifier and identifier type.

A resource object can have the following fields:

| Name | Type | Description |
| ---- | ---- | ----------- |
| invoiceId | string | Invoice ID |
| senderIdentifier | String | Sender ID |
| submissionUrl | URL | URL of the receiver that is receiving the invoice |
| invoiceUrl | URL | (optional)URL of the receiver(payer) |
| memo | String | Information placeholder |
| account | String | ledger account |
| currencyCode | String | defaulted to "$" |
| currencySymbol | String | defaulted to "USD" |
| name | String | sender name |
| userNumber | String | User Number |
| amount | String | amount |
| status | String | enum "paid, unpaid, cancelled " |
| invoiceInfo | String | additional information about the invoice |


### Quote Object

Represents a DFSP that has registered with the central directory.

Some fields are Read-only, meaning they are set by the API and cannot be modified by clients. A DFSP object can have the following fields:

| Name | Type | Description |
| ---- | ---- | ----------- |
| paymentId | UUID | Unique Identifier |
| payer | Object | Person initiating the quote |
| payer.identifier | String | Payer Id |
| payer.identifierType | String | Central Directory Registry Type |
| payer.url | URL | Base URL at which the payer information can be retrieved from |
| payer.account | String | Payer Ledger Account |
| payee | Object | Person for whom the quote is being requested for |
| payee.identifier | String | Payee Id |
| payee.identifierType | String | Central Directory Registry Type |
| payee.url | URL | Base URL at which the payee information can be retrieved from |
| payee.account | String | Payee Ledger Account |
| amountType | String | Should be either "SEND" or "RECEIVE" |
| amount | Object | Amount Object |
| amount.amount | number | Amount  |
| amount.currency | String | defaulted to "USD" |
| fees | Object | Payer Fees Object |
| fees.amount | number | Amount  |
| fees.currency | String | defaulted to "USD" |
| transferType | String | defaulted to "p2p" |

### Payment Object
Data structure that represents the payment in p2p transactions. This object is a mere representation of the Payment object used by ilp-service in /payIPR.

| Name | Type | Description |
| ---- | ---- | ----------- |
| ipr | String | Interledger payment request. This value is returned as part of quote call |
| sourceAmount | number | Amount that the sender will send. Depending on amount type of SEND/RECEIVE this value is calculated as part of quote |
| sourceAccount | URI | Ledger account URI to send the transfer from. |
| connectorAccount | URI | Ledger account URI of the connector through which this payment will be sent. |
| sourceExpiryDuration | Integer | Number of seconds after the payment is submitted that the outgoing transfer will expire.|

***

## Error information

This section identifies the potential errors returned and the structure of the response.

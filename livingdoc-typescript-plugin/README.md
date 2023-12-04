# livingdoc-typescript-plugin

Living documentation plugin for typescript.

[![npm Version](https://img.shields.io/npm/v/livingdoc-typescript-plugin.svg)](https://www.npmjs.com/package/livingdoc-typescript-plugin)
[![build-status](https://travis-ci.org/jboz/living-documentation.svg?branch=master)](https://travis-ci.org/jboz/livingdoc-typescript-plugin)
[![js-standard-style](https://img.shields.io/badge/code%20style-standard-brightgreen.svg?style=flat)](https://github.com/feross/standard)

## Usage

### Install

```shell
npm install --global livingdoc
```

### No installation

```shell
npx livingdoc ...
```

### Generate classes diagram

```bash
livingdoc-typescript-plugin diagram -i src\domain\**\*.ts -o dist\domain-classes.svg
```

Result example :
<img src="./docs/diagram.svg">

### Generate glossary

```bash
livingdoc-typescript-plugin glossary -i src\domain\**\*.ts -o dist\glossary.md
```

Result example :

| ObjectName                   | Attribute name               | Type         | Description                                                               |
| ---------------------------- | ---------------------------- | ------------ | ------------------------------------------------------------------------- |
| [Access](Access)             |                              |              |                                                                           |
|                              | [phoneNumber](phoneNumber)   | string       |                                                                           |
|                              | [price](price)               | number       |                                                                           |
|                              | [dateTime](dateTime)         | string       |                                                                           |
| [Bill](Bill)                 |                              |              | @RootAggregate<br>Monthly bill.                                           |
|                              | [month](month)               | string       | Which month of the bill.                                                  |
|                              | [contract](contract)         | Contract     | Contract concerned by the bill.                                           |
|                              | [accesses](accesses)         | Access[]     | Bill contents.                                                            |
|                              | [paymentState](paymentState) | PaymentState | Bill payment state                                                        |
| [BillsService](BillsService) |                              |              |                                                                           |
| [CallAccess](CallAccess)     |                              |              |                                                                           |
|                              | [duration](duration)         | string       |                                                                           |
| [Contract](Contract)         |                              |              | Telecom contract                                                          |
|                              | [id](id)                     | number       | Contract identifier.<br>Generate by the system and communicate to client. |
|                              | [customer](customer)         | Customer     | Contract customer.                                                        |
| [Customer](Customer)         |                              |              | Customer of the telecom service                                           |
|                              | [email](email)               | string       | Email of the customer.                                                    |
|                              | [contracts](contracts)       | Contract[]   | Customer's contracts.                                                     |
| [PaymentState](PaymentState) |                              | Enumeration  | Bill payment state values.                                                |
|                              | [WAITING](WAITING)           |              | Wainting payment by the client.                                           |
|                              | [DONE](DONE)                 |              | Client has payed.                                                         |
| [SmsAccess](SmsAccess)       |                              |              |                                                                           |

## Options

### -i, --input <path>

    Define the path of the Typescript file

### -o, --output <path>

    Define the path of the output file. If not defined, it'll output on the STDOUT

### -d, --deep <boolean>

    Indicate if program must through dependancies content or not

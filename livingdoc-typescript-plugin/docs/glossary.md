
| ObjectName | Attribute name | Type | Description |
| ---------- | -------------- | ---- | ----------- |
|[Access](Access)||||
||[phoneNumber](phoneNumber)|string||
||[price](price)|number||
||[dateTime](dateTime)|string||
|[Bill](Bill)|||@RootAggregate<br>Monthly bill.|
||[month](month)|string|Which month of the bill.|
||[contract](contract)|Contract|Contract concerned by the bill.|
||[accesses](accesses)|Access[]|Bill contents.|
||[paymentState](paymentState)|PaymentState|Bill payment state|
|[BillsService](BillsService)||||
|[CallAccess](CallAccess)||||
||[duration](duration)|string||
|[Contract](Contract)|||Telecom contract|
||[id](id)|number|Contract identifier.<br>Generate by the system and communicate to client.|
||[customer](customer)|Customer|Contract customer.|
|[Customer](Customer)|||Customer of the telecom service|
||[email](email)|string|Email of the customer.|
||[contracts](contracts)|Contract[]|Customer's contracts.|
|[PaymentState](PaymentState)||Enumeration|Bill payment state values.|
||[WAITING](WAITING)||Wainting payment by the client.|
||[DONE](DONE)||Client has payed.|
|[SmsAccess](SmsAccess)||||

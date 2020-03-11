import { Glossary } from '../src/glossary.mojo';

describe('Glossary', () => {
  it('should generate diagram for a single file', () => {
    expect(
      new Glossary().generateFromPath('test/resources/telecom/**/*.ts', false).then((document: string) => {
        expect(document).toEqual(`
| ObjectName | Attribute name | Type | Description |
| ---------- | -------------- | ---- | ----------- |
|Access||||
||phoneNumber|string||
||price|number||
||dateTime|string||
|Bill|||Monthly bill.|
||month|string|Which month of the bill.|
||contract|Contract|Contract concerned by the bill.|
||accesses|Access[]|Bill contents.|
||paymentState|PaymentState|Bill payment state|
|BillsService||||
|CallAccess||||
||duration|string||
|Contract|||Telecom contract|
||id|number|Contract identifier. Generate by the system and communicate to client.|
||customer|Customer|Contract customer.|
|Customer|||Customer of the telecom service|
||email|string|Email of the customer.|
||contracts|Contract[]|Customer's contracts.|
|PaymentState||Enumeration|Bill payment state values.|
||WAITING||Wainting payment by the client.|
||DONE||Client has payed.|
|SmsAccess||||
`);
      })
    );
  });
});

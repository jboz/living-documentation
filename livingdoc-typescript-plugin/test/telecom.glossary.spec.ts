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
|Bill||||
||month|string||
||contract|Contract||
||accesses|Access[]||
||paymentState|PaymentState||
|BillsService||||
|CallAccess||||
||duration|string||
|Contract||||
||id|number||
||customer|Customer||
|Customer||||
||email|string||
||contracts|Contract[]||
|PaymentState||||
||WAITING|||
||DONE|||
|SmsAccess||||
`);
      })
    );
  });
});

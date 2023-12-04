import { Diagram } from '../src/diagram.mojo';

describe('Diagram', () => {
  test('should generate diagram for a single file', () => {
    expect(new Diagram().generate(['test/resources/anything/domain/service/MyService.service.ts'], false)).toEqual(`
@startuml

class MyService {
  repository: MyRepository
  findAll(): MyRootAggregate[]
  findById(rootAggregateId: string): MyRootAggregate
}

MyService --> MyRepository: repository
MyService -- MyRootAggregate: use

@enduml
`);
  });

  test('should generate diagram with dependencies and uses models', () => {
    return new Diagram().generateFromPath('test/resources/anything/**/*.ts').then(document => {
      expect(document).toEqual(`
@startuml

class AbstractClass

class First

interface MyAbstractBean

enum MyEnum {
  CONST_1
  CONST_2
}

interface MyFirstConcretBean {
  data: string
}

class MyRepository

interface MyRootAggregate {
  identity: MyRootIdentity
  vo: MyValueObject
}

interface MyRootIdentity {
  value: string
}

interface MySecondConcretBean

class MyService {
  repository: MyRepository
  findAll(): MyRootAggregate[]
  findById(rootAggregateId: string): MyRootAggregate
}

interface MyValueObject {
  aString: string
  aNumber: number
  aBoolean: boolean
  aUndefined: undefined
  aNull: null
  aAny: any
  aSet: Set<string>
  aArray: Array<string>
  anotherArray: string[]
  aMap: Map<number, string>
  beans: MyAbstractBean[]
  classesBeans: AbstractClass[]
  theEnum: MyEnum
}

class Second

AbstractClass <|- First
AbstractClass <|- Second
MyAbstractBean <|- MyFirstConcretBean
MyAbstractBean <|- MySecondConcretBean
MyRootAggregate --> MyRootIdentity: identity
MyRootAggregate --> MyValueObject: vo
MyService --> MyRepository: repository
MyService -- MyRootAggregate: use
MyValueObject --> "*" AbstractClass: classesBeans
MyValueObject --> "*" MyAbstractBean: beans
MyValueObject --> MyEnum: theEnum

@enduml
`);
    });
  });

  test('should generate diagram with color and links', () => {
    expect(
      new Diagram({ links: true }).generateFromPath('test/resources/telecom/domain/aggregate/**/*.ts', false).then((document: string) => {
        expect(document).toEqual(`
@startuml

interface Access [[#Access]] {
  phoneNumber: string
  price: number
  dateTime: string
}

class Bill [[#Bill]] #wheat {
  month: string
  contract: Contract
  accesses: Access[]
  paymentState: PaymentState
}

interface CallAccess [[#CallAccess]] {
  duration: string
}

interface Contract [[#Contract]] {
  id: number
  customer: Customer
}

interface Customer [[#Customer]] {
  email: string
  contracts: Contract[]
}

enum PaymentState [[#PaymentState]] {
  WAITING
  DONE
}

interface SmsAccess [[#SmsAccess]]

Access <|- CallAccess
Access <|- SmsAccess
Bill --> "*" Access: accesses
Bill --> Contract: contract
Bill --> PaymentState: paymentState
Contract --> Customer: customer
Customer --> "*" Contract: contracts

@enduml
`);
      })
    );
  });
});

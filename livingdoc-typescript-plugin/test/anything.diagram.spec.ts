import { diagram } from '../src/diagram.mojo';

describe('Diagram generator', () => {
  it('should generate diagram for a single file', () => {
    expect(diagram.generateDiagram(['test/resources/anything/domain/service/MyService.service.ts'], false)).toEqual(`
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

  it('should generate diagram with dependencies and uses models', () => {
    return diagram.generateDiagramFromPath('test/resources/anything/**/*.ts').then(document => {
      expect(document).toEqual(`
@startuml

class AbstractClass

class First

interface MyAbstractBean

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

@enduml
`);
    });
  });
});

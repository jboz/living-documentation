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

MyService --> MyRepository
MyService -- MyRootAggregate: use

@enduml
`);
  });

  it('should generate diagram with dependencies and uses models', () => {
    expect(diagram.generateDiagram(['test/resources/anything/index.ts'])).toEqual(`
@startuml

interface MyRootIdentity {
  value: string
}

interface MyAbstractBean

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
}

interface MyRootAggregate {
  identity: MyRootIdentity
  vo: MyValueObject
}

class MyRepository

class MyService {
  repository: MyRepository
  findAll(): MyRootAggregate[]
  findById(rootAggregateId: string): MyRootAggregate
}

MyValueObject --> MyAbstractBean
MyRootAggregate --> MyRootIdentity
MyRootAggregate --> MyValueObject
MyService --> MyRepository
MyService -- MyRootAggregate: use

@enduml
`);
  });
});

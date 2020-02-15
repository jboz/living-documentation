import { diagram } from '../src/diagram.mojo';

describe('Diagram generator', () => {
  it('should generate diagram with dependencies and uses models', () => {
    expect(diagram.generateDiagram(['test/resources/domain/service/MyService.service.ts'])).toEqual(`
@startuml

class MyRootAggregate

class MyRepository

class MyService {
  repository: MyRepository
  findAll(): MyRootAggregate[]
  findById(rootAggregateId: string): MyRootAggregate
}

MyService --> MyRepository
MyService -- MyRootAggregate

@enduml
`);
  });
});

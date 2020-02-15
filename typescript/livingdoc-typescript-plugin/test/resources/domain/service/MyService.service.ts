import { MyRootAggregate } from '../aggregate/MyRootAggregate';
import { MyRepository } from '../repository/MyRepository.repository';

class MyService {
  constructor(private repository: MyRepository) {}

  public findAll(): MyRootAggregate[] {
    return null;
  }

  public findById(rootAggregateId: string): MyRootAggregate {
    return null;
  }
}

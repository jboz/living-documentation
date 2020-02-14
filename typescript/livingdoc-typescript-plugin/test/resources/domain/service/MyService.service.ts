import { MyRepository } from '../repository/MyRepository.repository';

class MyService {
  constructor(public repository: MyRepository) {}
}

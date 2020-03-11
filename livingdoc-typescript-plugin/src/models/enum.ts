import { GlobalParameters } from '../global-parameters';
import { WithMembersStatement } from './with-members.statement';

export class Enum extends WithMembersStatement {
  type = 'enum';

  public toTable() {
    const statements = [`|${this.name}||Enumeration|${this.comment ? this.comment : ''}|`];
    this.members.forEach(member => statements.push(member.toTable()));
    return statements.join(GlobalParameters.eol);
  }
}

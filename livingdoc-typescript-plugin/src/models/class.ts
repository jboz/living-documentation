import { GlobalParameters } from '../global-parameters';
import { WithMembersStatement } from './with-members.statement';

export class Class extends WithMembersStatement {
  public toPlantuml() {
    const statements = [`class ${this.name}${this.members.length > 0 ? ' {' : ''}`];
    this.members.forEach(member => statements.push(`${GlobalParameters.indent}${member.toPlantuml()}`));
    if (this.members.length > 0) {
      statements.push('}');
    }

    return statements.join(GlobalParameters.eol);
  }
}

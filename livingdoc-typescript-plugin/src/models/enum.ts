import { Options } from '..';
import { GlobalParameters } from '../global-parameters';
import { WithMembersStatement } from './with-members.statement';

export class Enum extends WithMembersStatement {
  type = 'enum';

  private get openBrace(): string {
    return this.members.length > 0 ? ' {' : '';
  }

  public toPlantuml(options?: Options) {
    const statements = [`${this.type} ${this.getName(options)}${this.openBrace}`];
    this.members.forEach(member => statements.push(`${GlobalParameters.indent}${member.toPlantuml(options)}`));
    if (this.members.length > 0) {
      statements.push('}');
    }

    return statements.join(GlobalParameters.eol);
  }

  public toTable() {
    const statements = [`|${this.nameWithAnchor}||Enumeration|${this.comments?.join(GlobalParameters.br)}|`];
    this.members.forEach(member => statements.push(member.toTable()));
    return statements.join(GlobalParameters.eol);
  }
}

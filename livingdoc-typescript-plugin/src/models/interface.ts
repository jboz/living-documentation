import { Options } from '..';
import { GlobalParameters } from '../global-parameters';
import { Property } from './property';
import { WithMembersStatement } from './with-members.statement';

export class Interface extends WithMembersStatement {
  type = 'interface';

  private get openBrace(): string {
    return this.members.length > 0 ? ' {' : '';
  }

  public toPlantuml(options: Options) {
    const statements = [`${this.type} ${this.getName(options)}${this.openBrace}`];
    this.members.forEach(member => statements.push(`${GlobalParameters.indent}${member.toPlantuml(options)}`));
    if (this.members.length > 0) {
      statements.push('}');
    }

    return statements.join(GlobalParameters.eol);
  }

  public toTable() {
    const statements = [`|${this.nameWithAnchor}|||${this.comments?.join(GlobalParameters.br)}|`];
    this.members.filter(member => member instanceof Property).forEach(member => statements.push(member.toTable()));
    return statements.join(GlobalParameters.eol);
  }
}

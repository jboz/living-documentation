import { GlobalParameters } from '../global-parameters';
import { Property } from './property';
import { Statement } from './statement';

export abstract class WithMembersStatement extends Statement {
  public members: Statement[] = [];
  public inheritance: Statement[] = [];

  constructor(name: string) {
    super(undefined, name);
  }

  abstract get type(): string;

  public toPlantuml() {
    const statements = [`${this.type} ${this.name}${this.members.length > 0 ? ' {' : ''}`];
    this.members.forEach(member => statements.push(`${GlobalParameters.indent}${member.toPlantuml()}`));
    if (this.members.length > 0) {
      statements.push('}');
    }

    return statements.join(GlobalParameters.eol);
  }

  public toTable() {
    const statements = [`|${this.name}||||`];
    this.members.filter(member => member instanceof Property).forEach(member => statements.push(member.toTable()));
    return statements.join(GlobalParameters.eol);
  }
}

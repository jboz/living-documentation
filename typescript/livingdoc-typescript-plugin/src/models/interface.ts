import { Statement } from './statement';

export class Interface extends Statement {
  public members: Statement[] = [];

  constructor(name: string) {
    super(name);
  }

  toPlantuml() {
    const statements = [`interface ${this.name}${this.members.length > 0 ? ' {' : ''}`];
    this.members.forEach(member => statements.push(`${this.indent}${member.toPlantuml()}`));
    if (this.members.length > 0) {
      statements.push('}');
    }
    return statements.join(this.eol);
  }
}

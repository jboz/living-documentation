import { EOL } from 'os';
import { Model } from './model';

export class Class implements Model {
  public members: string[] = [];
  constructor(public readonly name: string) {}

  toPlantuml() {
    const statements = [`class ${this.name}${this.members.length > 0 ? ' {' : ''}`];
    this.members.forEach(member => statements.push(`  ${member}`));
    if (this.members.length > 0) {
      statements.push('}');
    }
    return statements.join(EOL);
  }
}

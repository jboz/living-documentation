import { Statement } from './statement';

export class Association extends Statement {
  constructor(public readonly left: Statement, public readonly right: Statement, public readonly link: string) {
    super('association');
  }

  toPlantuml() {
    return `${this.left.name} ${this.link} ${this.right.name}`;
  }
}

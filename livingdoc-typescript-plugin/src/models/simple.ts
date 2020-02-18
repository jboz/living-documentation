import { Statement } from './statement';

export class Simple extends Statement {
  constructor(name: string) {
    super(name);
  }

  public toPlantuml() {
    return this.name;
  }
}

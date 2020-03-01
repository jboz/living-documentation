import { Statement } from './statement';

export class Association extends Statement {
  constructor(
    public readonly left: Statement,
    public readonly right: Statement,
    public readonly rightName: string | undefined,
    public readonly link: string,
    public readonly linkName: string | undefined = undefined
  ) {
    super(undefined, 'association');
  }

  public toPlantuml() {
    return `${this.left.name} ${this.link}${this.rightName ? ` "${this.rightName}"` : ''} ${this.right.name}${
      this.linkName ? ': ' + this.linkName : ''
    }`;
  }
}

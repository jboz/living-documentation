import { Options } from '..';

export abstract class Statement {
  public comments?: string[];

  constructor(public readonly parent: Statement | undefined, public readonly name: string) {}

  get nameWithAnchor(): string {
    return `[${this.name}](${this.name})`;
  }

  get diagramLink() {
    return `[[#${this.name}]]`;
  }

  get nameWithLink(): string {
    return `${this.name} ${this.diagramLink}`;
  }

  getName(options?: Options) {
    return options?.links ? this.nameWithLink : this.name;
  }

  public toPlantuml(options?: Options): string {
    return `${this.name}`;
  }

  public toTable(options?: Options): string {
    return `${this.name}`;
  }
}

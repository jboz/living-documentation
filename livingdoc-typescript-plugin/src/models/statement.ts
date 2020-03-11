export abstract class Statement {
  public comments?: string[];

  constructor(public readonly parent: Statement | undefined, public readonly name: string) {}

  public toPlantuml(): string {
    return `${this.name}`;
  }

  public toTable(): string {
    return `${this.name}`;
  }
}

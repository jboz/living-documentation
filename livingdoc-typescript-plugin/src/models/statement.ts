export abstract class Statement {
  constructor(public readonly parent: Statement | undefined, public readonly name: string) {}

  public toPlantuml(): string {
    return `${this.name}`;
  }
}

import { ClassLikeDeclarationBase, forEachChild, SourceFile, SyntaxKind, TypeChecker } from 'typescript';
import { Class } from '../models/class';
import { Model } from '../models/model';

export class ClassDiagramBuilder {
  sources!: readonly SourceFile[];
  private models: Model[] = [];

  constructor(private readonly checker: TypeChecker) {}

  public addSources(sources: readonly SourceFile[]): ClassDiagramBuilder {
    this.sources = sources;
    return this;
  }

  public build(): string {
    this.readSources();
    const output = ['@startuml'];
    this.models.forEach(model => output.push(model.toPlantuml()));
    output.push('@enduml');
    return output.join('\n\n');
  }

  private readSources() {
    this.sources.forEach(source => {
      if (!source.isDeclarationFile) {
        forEachChild(source, child => {
          if (child.kind === SyntaxKind.ClassDeclaration) {
            const childClass = child as ClassLikeDeclarationBase;
            if (childClass.name === undefined) {
              return;
            }
            // This is a top level class, get its symbol
            const classSymbol = this.checker.getSymbolAtLocation(childClass.name);
            if (classSymbol === undefined) {
              return;
            }
            this.models.push(Class.create(classSymbol, this.checker));
          }
        });
      }
    });
  }
}

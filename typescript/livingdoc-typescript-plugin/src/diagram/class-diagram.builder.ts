import { ClassLikeDeclarationBase, forEachChild, SourceFile, SyntaxKind, TypeChecker } from 'typescript';
import { Model } from '../models/model';
import { ClassFactory } from '../transformer/class.factory';

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
    const output = ['', '@startuml', ''];
    this.models.forEach(model => {
      output.push(model.toPlantuml());
      output.push('');
    });
    output.push('@enduml');
    output.push('');
    return output.join(`\n`);
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
            this.models.push(ClassFactory.create(classSymbol, this.checker));
          }
        });
      }
    });
  }
}

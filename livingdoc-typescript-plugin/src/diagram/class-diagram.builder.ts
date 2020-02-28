import { forEachChild, SourceFile, TypeChecker } from 'typescript';
import { AssociationFactory } from '../factories/association.factory';
import { GlobalFactory } from '../factories/global.factory';
import { GlobalParameters } from '../global-parameters';
import { Association } from '../models/association';
import { Statement } from '../models/statement';
import { WithMembersStatement } from '../models/with-members.statement';

export class ClassDiagramBuilder extends Statement {
  private sources!: readonly SourceFile[];
  private readonly statements: Statement[] = [];
  private readonly associations: Association[] = [];

  constructor(private readonly checker: TypeChecker) {
    super(undefined, 'ClassDiagramBuilder');
  }

  public addSources(sources: readonly SourceFile[]): ClassDiagramBuilder {
    this.sources = sources;

    return this;
  }

  public build(): string {
    this.readSources();
    this.detectAssociations();

    return this.toPlantuml();
  }

  public toPlantuml() {
    const output = ['', '@startuml', ''];
    this.statements.forEach(model => {
      output.push(model.toPlantuml());
      output.push('');
    });
    if (this.associations.length > 0) {
      this.associations.forEach(association => output.push(association.toPlantuml()));
      output.push('');
    }
    output.push('@enduml');
    output.push('');

    return output.join(GlobalParameters.eol);
  }

  private readSources() {
    this.sources.forEach(source => {
      if (!source.isDeclarationFile) {
        forEachChild(source, child => {
          // TODO mark deep as false if input filename pattern doesn't match the source path
          const statement = GlobalFactory.create(child, undefined, this.checker);
          if (statement) {
            this.statements.push(statement);
          }
        });
      }
    });
  }

  private detectAssociations() {
    this.statements.forEach(statement => {
      if (statement instanceof WithMembersStatement) {
        const root = statement;
        root.members.forEach(member => AssociationFactory.create(member).forEach(assoc => this.addAssociation(assoc)));
        root.inheritance.forEach(member => AssociationFactory.create(member).forEach(assoc => this.addAssociation(assoc)));
      }
    });
  }

  private addAssociation(newAssoc: Association | undefined) {
    if (newAssoc) {
      const existingAssoc = this.associations.find(
        assoc => assoc.left.name === newAssoc.left.name && assoc.right.name === newAssoc.right.name
      );
      if (existingAssoc === undefined) {
        this.associations.push(newAssoc);
      }
    }
  }
}

import sortBy from 'lodash/sortBy';
import { SourceFile, TypeChecker } from 'typescript';
import { Options } from '..';
import { AssociationFactory } from '../factories/association.factory';
import { GlobalParameters } from '../global-parameters';
import { Association } from '../models/association';
import { Statement } from '../models/statement';
import { WithMembersStatement } from '../models/with-members.statement';
import { TypescriptParser } from '../parser/typescript.parser';
import { BaseBuilder } from './base.builder';

export class ClassDiagramBuilder extends BaseBuilder {
  private sources!: readonly SourceFile[];
  private statements: Statement[] = [];
  private associations: Association[] = [];

  constructor(private readonly checker: TypeChecker, private readonly options?: Options) {
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
      output.push(model.toPlantuml(this.options));
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
    this.statements = TypescriptParser.parse(this.sources, this.checker);
  }

  private detectAssociations() {
    this.statements.forEach(statement => {
      if (statement instanceof WithMembersStatement) {
        const root = statement;
        root.members.forEach(member => AssociationFactory.create(member).forEach(assoc => this.addAssociation(assoc)));
        root.inheritance.forEach(member => AssociationFactory.createInheritance(member).forEach(assoc => this.addAssociation(assoc)));
      }
    });
    this.associations = sortBy(this.associations, ['left.name', 'right.name']);
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

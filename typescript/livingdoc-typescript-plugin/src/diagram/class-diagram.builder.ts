import { forEachChild, SourceFile, TypeChecker } from 'typescript';
import { GlobalFactory } from '../factories/global.factory';
import { Association } from '../models/association';
import { Method } from '../models/method';
import { Property } from '../models/property';
import { Statement } from '../models/statement';
import { Type } from '../models/type';
import { WithMembersStatement } from '../models/with-members.statement';

export class ClassDiagramBuilder extends Statement {
  private sources!: readonly SourceFile[];
  private statements: Statement[] = [];
  private associations: Association[] = [];

  constructor(private readonly checker: TypeChecker) {
    super('builder');
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

  toPlantuml() {
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
    return output.join(this.eol);
  }

  private readSources() {
    this.sources.forEach(source => {
      if (!source.isDeclarationFile) {
        forEachChild(source, child => {
          // TODO mark deep as false is input filename pattern doesn't match the source path
          const statement = GlobalFactory.create(child, this.checker);
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
        const statementWith = statement as WithMembersStatement;
        statementWith.members.forEach(member => {
          if (member instanceof Type) {
            this.addAssociation(new Association(statementWith, member, '-->'));
          } else if (member instanceof Property) {
            const property = member as Property;
            property.types.forEach(type => {
              if (type instanceof Type) {
                this.addAssociation(new Association(statementWith, type, '-->'));
              }
            });
          } else if (member instanceof Method) {
            const methodMember = member as Method;
            methodMember.returnTypes.forEach(type => {
              if (type instanceof Type) {
                this.addAssociation(new Association(statementWith, type, '--', 'use'));
              }
            });
          }
        });
      }
    });
  }

  private addAssociation(newAssoc: Association) {
    const existingAssoc = this.associations.find(
      assoc => assoc.left.name === newAssoc.left.name && assoc.right.name === newAssoc.right.name
    );
    if (existingAssoc === undefined) {
      this.associations.push(newAssoc);
    }
  }
}

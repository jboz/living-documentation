import { forEachChild, SourceFile, TypeChecker } from 'typescript';
import { GlobalFactory } from '../factories/global.factory';
import { Association } from '../models/association';
import { Class } from '../models/class';
import { Method } from '../models/method';
import { Property } from '../models/property';
import { Statement } from '../models/statement';
import { Type } from '../models/type';

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
      if (statement instanceof Class) {
        const classStatement = statement as Class;
        classStatement.members.forEach(member => {
          if (member instanceof Type || member instanceof Class) {
            this.addAssociation(new Association(classStatement, member, '-->'));
          } else if (member instanceof Property) {
            const property = member as Property;
            if (property.type instanceof Type) {
              this.addAssociation(new Association(classStatement, property.type, '-->'));
            }
          } else if (member instanceof Method) {
            const methodMember = member as Method;
            if (methodMember.returnType instanceof Type) {
              this.addAssociation(new Association(classStatement, methodMember.returnType, '--'));
            }
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

import { ArrayTypeNode, ParameterDeclaration, SyntaxKind, TypeChecker, TypeReferenceNode } from 'typescript';
import { Property } from '../models/property';
import { Simple } from '../models/simple';
import { Statement } from '../models/statement';
import { COLLECTION_NAMES, GlobalFactory } from './global.factory';

export class PropertyFactory {
  /**
   * @param declaration
   * @param checker
   * @param deep
   */
  public static create(parent: Statement | undefined, declaration: ParameterDeclaration, checker: TypeChecker, deep: boolean): Statement {
    if (declaration.type === undefined) {
      return new Simple(parent, declaration.getText());
    }
    const propertyStatement = new Property(parent, declaration.name.getText(), declaration.type.getText());
    if (declaration.type.kind === SyntaxKind.TypeReference) {
      const typeNode = declaration.type as TypeReferenceNode;
      if (typeNode.typeArguments) {
        propertyStatement.types = typeNode.typeArguments.map(argument => GlobalFactory.create(argument, propertyStatement, checker, deep));
      }
      propertyStatement.many = COLLECTION_NAMES.includes(typeNode.typeName.getText());
    } else if (declaration.type.kind === SyntaxKind.ArrayType) {
      const typeNode = declaration.type as ArrayTypeNode;
      propertyStatement.types = [GlobalFactory.create(typeNode.elementType, propertyStatement, checker, deep)];
      propertyStatement.many = true;
    }
    // no argument types specified, maybe the type is interesting
    propertyStatement.types.push(GlobalFactory.create(declaration.type, propertyStatement, checker, deep));

    return propertyStatement;
  }
}

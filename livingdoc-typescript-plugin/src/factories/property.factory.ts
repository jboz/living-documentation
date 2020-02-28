import { ArrayTypeNode, ParameterDeclaration, SyntaxKind, TypeChecker, TypeReferenceNode } from 'typescript';
import { Property } from '../models/property';
import { Simple } from '../models/simple';
import { Statement } from '../models/statement';
import { GlobalFactory } from './global.factory';

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
        // if type arguments are specified we don't care about the type itself
        propertyStatement.types = typeNode.typeArguments.map(argument => GlobalFactory.create(argument, propertyStatement, checker, deep));
      }
    } else if (declaration.type.kind === SyntaxKind.ArrayType) {
      const typeNode = declaration.type as ArrayTypeNode;
      // if type arguments are specified we don't care about the type itself
      propertyStatement.types = [GlobalFactory.create(typeNode.elementType, propertyStatement, checker, deep)];
    }
    // no argument types specified, maybe the type is interesting
    propertyStatement.types.push(GlobalFactory.create(declaration.type, propertyStatement, checker, deep));

    return propertyStatement;
  }
}

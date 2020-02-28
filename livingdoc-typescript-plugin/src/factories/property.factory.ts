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
    let propertyTypes: (Statement | undefined)[] = [];
    if (declaration.type.kind === SyntaxKind.TypeReference) {
      const typeNode = declaration.type as TypeReferenceNode;
      if (typeNode.typeArguments) {
        // if type arguments are specified we don't care about the type itself
        propertyTypes = typeNode.typeArguments.map(argument => GlobalFactory.create(argument, parent, checker, deep));
      }
    } else if (declaration.type.kind === SyntaxKind.ArrayType) {
      const typeNode = declaration.type as ArrayTypeNode;
      // if type arguments are specified we don't care about the type itself
      propertyTypes = [GlobalFactory.create(typeNode.elementType, parent, checker, deep)];
    }
    // no argument types specified, maybe the type is interesting
    propertyTypes.push(GlobalFactory.create(declaration.type, parent, checker, deep));

    const propertyStatement = new Property(parent, declaration.name.getText(), declaration.type.getText());
    propertyStatement.types = propertyTypes;
    return propertyStatement;
  }
}

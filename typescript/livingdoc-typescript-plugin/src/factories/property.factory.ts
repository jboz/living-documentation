import { ParameterDeclaration, SyntaxKind, TypeChecker, TypeReferenceNode } from 'typescript';
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
  static create(declaration: ParameterDeclaration, checker: TypeChecker, deep: boolean): Statement {
    if (declaration.type === undefined) {
      return new Simple(declaration.getText());
    }
    let propertyTypes: (Statement | undefined)[] = [];
    if (declaration.type.kind === SyntaxKind.TypeReference || declaration.type.kind === SyntaxKind.ArrayType) {
      const typeNode = declaration.type as TypeReferenceNode;
      if (typeNode.typeArguments) {
        // if type arguments are specified we don't care about the type itself
        propertyTypes = typeNode.typeArguments.map(argument => GlobalFactory.create(argument, checker, deep));
      } else {
        // no argument types specified, maybe the type is interesting
        propertyTypes = [GlobalFactory.create(declaration.type, checker, deep)];
      }
    }
    return new Property(declaration.name.getText(), propertyTypes, declaration.type.getText());
  }
}

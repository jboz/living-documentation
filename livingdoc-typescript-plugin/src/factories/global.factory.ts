import {
  ClassLikeDeclarationBase,
  FunctionDeclaration,
  Identifier,
  InterfaceDeclaration,
  Node,
  ParameterDeclaration,
  SyntaxKind,
  TypeChecker,
  TypeReferenceNode
} from 'typescript';
import { Statement } from '../models/statement';
import { Type } from '../models/type';
import { ClassFactory } from './class.factory';
import { InterfaceFactory } from './interface.factory';
import { MethodFactory } from './method.factory';
import { PropertyFactory } from './property.factory';

const KINDS_METHOD = [SyntaxKind.MethodDeclaration, SyntaxKind.MethodSignature];
const KINDS_PROPERTY = [
  SyntaxKind.PropertySignature,
  SyntaxKind.PropertyDeclaration,
  SyntaxKind.GetAccessor,
  SyntaxKind.SetAccessor,
  SyntaxKind.Parameter
];
export const COLLECTION_NAMES = ['Set', 'Array', 'Map', 'List'];

export class GlobalFactory {
  public static create(node: Node, parent: Statement | undefined, checker: TypeChecker, deep = true): Statement | undefined {
    if (node.kind === SyntaxKind.ClassDeclaration) {
      return ClassFactory.create(node as ClassLikeDeclarationBase, checker, deep);
      //
    } else if (node.kind === SyntaxKind.InterfaceDeclaration) {
      return InterfaceFactory.create(node as InterfaceDeclaration, checker, deep);
      //
    } else if (node.kind === SyntaxKind.TypeReference) {
      const typeNode = node as TypeReferenceNode;
      const indentity = typeNode.typeName as Identifier;
      if (COLLECTION_NAMES.includes(indentity.escapedText.toString())) {
        return undefined;
      }
      return new Type(parent, typeNode.typeName.getText());
      //
    } else if (KINDS_PROPERTY.includes(node.kind)) {
      return PropertyFactory.create(parent, node as ParameterDeclaration, checker, deep);
      //
    } else if (KINDS_METHOD.includes(node.kind)) {
      return MethodFactory.create(parent, node as FunctionDeclaration, checker, deep);
      //
    }

    return;
  }
}

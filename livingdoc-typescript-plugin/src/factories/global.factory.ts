import {
  ClassLikeDeclarationBase,
  FunctionDeclaration,
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
const KINDS_PROPERTY_SIMPLE = [SyntaxKind.StringKeyword, SyntaxKind.NumberKeyword, SyntaxKind.AnyKeyword, SyntaxKind.BooleanKeyword];

export class GlobalFactory {
  public static create(node: Node, checker: TypeChecker, deep = true): Statement | undefined {
    if (node.kind === SyntaxKind.ClassDeclaration) {
      return ClassFactory.create(node as ClassLikeDeclarationBase, checker, deep);
      //
    } else if (node.kind === SyntaxKind.InterfaceDeclaration) {
      return InterfaceFactory.create(node as InterfaceDeclaration, checker, deep);
      //
    } else if (node.kind === SyntaxKind.TypeReference) {
      const typeNode = node as TypeReferenceNode;

      return new Type(typeNode.typeName.getText());
      //
    } else if (KINDS_PROPERTY.includes(node.kind)) {
      return PropertyFactory.create(node as ParameterDeclaration, checker, deep);
      //
    } else if (KINDS_METHOD.includes(node.kind)) {
      return MethodFactory.create(node as FunctionDeclaration, checker, deep);
      //
    }

    return;
  }
}

import {
  ArrayTypeNode,
  ClassLikeDeclarationBase,
  FunctionDeclaration,
  Node,
  ParameterDeclaration,
  SyntaxKind,
  TypeChecker,
  TypeReferenceNode
} from 'typescript';
import { Method } from '../models/method';
import { Property } from '../models/property';
import { Simple } from '../models/simple';
import { Statement } from '../models/statement';
import { Type } from '../models/type';
import { ClassFactory } from './class.factory';

const KINDS_METHOD = [SyntaxKind.MethodDeclaration, SyntaxKind.MethodSignature];
const KINDS_PROPERTY = [
  SyntaxKind.PropertySignature,
  SyntaxKind.PropertyDeclaration,
  SyntaxKind.GetAccessor,
  SyntaxKind.Parameter,
  SyntaxKind.StringKeyword
];

export class GlobalFactory {
  static create(node: Node, checker: TypeChecker, deep = true): Statement | undefined {
    if (node.kind === SyntaxKind.ClassDeclaration) {
      const childClass = node as ClassLikeDeclarationBase;
      if (childClass.name === undefined) {
        return;
      }
      // This is a top level class, get its symbol
      const classSymbol = checker.getSymbolAtLocation(childClass.name);
      if (classSymbol === undefined) {
        return;
      }
      return ClassFactory.create(classSymbol, checker, deep);
      //
    } else if (node.kind === SyntaxKind.TypeReference) {
      const typeNode = node as TypeReferenceNode;
      return new Type(typeNode.typeName.getText());
      //
    } else if (node.kind === SyntaxKind.ArrayType) {
      const arrayNode = node as ArrayTypeNode;
      return new Type(arrayNode.elementType.getText(), '[]');
      //
    } else if (node.kind === SyntaxKind.Parameter) {
      const childProperty = node as ParameterDeclaration;
      if (childProperty.type === undefined) {
        return new Simple(childProperty.getText());
      }
      const propertyType = GlobalFactory.create(childProperty.type, checker, false);
      return new Property(childProperty.name.getText(), propertyType);
      //
    } else if (KINDS_METHOD.includes(node.kind)) {
      const declaration = node as FunctionDeclaration;
      if (declaration.type === undefined) {
        return new Simple(declaration.getFullText());
      }
      if (declaration.name === undefined) {
        return;
      }
      const parameters = declaration.parameters.map(parameter => GlobalFactory.create(parameter, checker, false));
      const returnType = GlobalFactory.create(declaration.type, checker, false);
      return new Method(declaration.name.getText(), parameters, returnType);
      //
    } else if (KINDS_PROPERTY.includes(node.kind)) {
      return new Simple(node.getText());
      //
    }
    return;
  }
}

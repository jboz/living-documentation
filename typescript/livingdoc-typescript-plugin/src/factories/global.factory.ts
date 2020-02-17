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
import { Method } from '../models/method';
import { Property } from '../models/property';
import { Simple } from '../models/simple';
import { Statement } from '../models/statement';
import { Type } from '../models/type';
import { ClassFactory } from './class.factory';
import { InterfaceFactory } from './interface.factory';

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
    } else if (node.kind === SyntaxKind.InterfaceDeclaration) {
      const interfaceChild = node as InterfaceDeclaration;
      // This is a top level class, get its symbol
      const interfaceSymbol = checker.getSymbolAtLocation(interfaceChild.name);
      if (interfaceSymbol === undefined) {
        return;
      }
      return InterfaceFactory.create(interfaceSymbol, checker, deep);
      //
    } else if (node.kind === SyntaxKind.TypeReference) {
      const typeNode = node as TypeReferenceNode;
      return new Type(typeNode.typeName.getText());
      //
    } else if (KINDS_PROPERTY.includes(node.kind)) {
      const childProperty = node as ParameterDeclaration;
      if (childProperty.type === undefined) {
        return new Simple(childProperty.getText());
      }
      let propertyTypes: (Statement | undefined)[] = [];
      if (childProperty.type.kind === SyntaxKind.TypeReference || childProperty.type.kind === SyntaxKind.ArrayType) {
        const typeNode = childProperty.type as TypeReferenceNode;
        if (typeNode.typeArguments) {
          // if type arguments are specified we don't care about the type itself
          propertyTypes = typeNode.typeArguments.map(argument => GlobalFactory.create(argument, checker, deep));
        } else {
          // no argument types specified, maybe the type is interesting
          propertyTypes = [GlobalFactory.create(childProperty.type, checker, deep)];
        }
      }
      return new Property(childProperty.name.getText(), propertyTypes, childProperty.type.getText());
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
      return new Method(declaration.name.getText(), parameters, [returnType], declaration.type.getText());
      //
    }
    return;
  }
}

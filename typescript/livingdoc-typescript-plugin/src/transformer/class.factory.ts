import { Symbol, SyntaxKind, TypeChecker } from 'typescript';
import { Class } from '../models/class';

const KINDS_METHOD = [SyntaxKind.MethodDeclaration, SyntaxKind.MethodSignature];
const KINDS_PROPERTY = [SyntaxKind.PropertySignature, SyntaxKind.PropertyDeclaration, SyntaxKind.GetAccessor, SyntaxKind.Parameter];

export class ClassFactory {
  public static create(classSymbol: Symbol, checker: TypeChecker): Class {
    const result = new Class(classSymbol.getName());
    // const classDeclaration = classSymbol.getDeclarations();

    // if (classDeclaration !== undefined && classDeclaration.length > 0) {
    //   result.isStatic = ComponentFactory.isStatic(classDeclaration[classDeclaration.length - 1]);
    //   result.isAbstract = ComponentFactory.isAbstract(classDeclaration[classDeclaration.length - 1]);
    // }

    if (classSymbol.members !== undefined) {
      classSymbol.members.forEach(member => {
        const declarations = member.getDeclarations();
        if (declarations === undefined) {
          return;
        }
        declarations.forEach(declaration => {
          if (declaration.kind === SyntaxKind.TypeParameter) {
            result.members.push(member.getName());
          } else if (KINDS_METHOD.includes(declaration.kind)) {
            result.members.push(member.getName());
          } else if (KINDS_PROPERTY.includes(declaration.kind)) {
            result.members.push(
              `${member.getName()}: ${checker.typeToString(checker.getTypeOfSymbolAtLocation(member, member.valueDeclaration))}`
            );
          }
        });
      });
      //   result.typeParameters = ComponentFactory.serializeTypeParameters(classSymbol.members, checker);
    }

    // if (classSymbol.exports !== undefined) {
    //   result.members = result.members.concat(ComponentFactory.serializeMethods(classSymbol.exports, checker));
    // }

    // if (classSymbol.globalExports !== undefined) {
    //   result.members = result.members.concat(ComponentFactory.serializeMethods(classSymbol.globalExports, checker));
    // }

    // if (classDeclaration !== undefined && classDeclaration.length > 0) {
    //   const heritageClauses: ts.NodeArray<ts.HeritageClause> | undefined = classDeclaration[classDeclaration.length - 1].heritageClauses;

    //   if (heritageClauses !== undefined) {
    //     heritageClauses.forEach((heritageClause: ts.HeritageClause): void => {
    //       if (heritageClause.token === ts.SyntaxKind.ExtendsKeyword) {
    //         result.extendsClass = ComponentFactory.getExtendsHeritageClauseName(heritageClause);
    //       } else if (heritageClause.token === ts.SyntaxKind.ImplementsKeyword) {
    //         result.implementsInterfaces = ComponentFactory.getImplementsHeritageClauseNames(heritageClause);
    //       }
    //     });
    //   }
    // }

    return result;
  }
}

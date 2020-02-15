import { Symbol, TypeChecker } from 'typescript';
import { Class } from '../models/class';
import { Statement } from '../models/statement';
import { GlobalFactory } from './global.factory';

export class ClassFactory {
  public static create(classSymbol: Symbol, checker: TypeChecker, deep = true): Class {
    const classStatement = new Class(classSymbol.getName());
    // const classDeclaration = classSymbol.getDeclarations();

    // if (classDeclaration !== undefined && classDeclaration.length > 0) {
    //   result.isStatic = ComponentFactory.isStatic(classDeclaration[classDeclaration.length - 1]);
    //   result.isAbstract = ComponentFactory.isAbstract(classDeclaration[classDeclaration.length - 1]);
    // }

    if (deep && classSymbol.members !== undefined) {
      classSymbol.members.forEach(member => {
        const declarations = member.getDeclarations();
        if (declarations === undefined) {
          return;
        }
        declarations.forEach(declaration => {
          ClassFactory.addMember(GlobalFactory.create(declaration, checker, false), classStatement);
          // if (declaration.kind === SyntaxKind.TypeParameter) {
          //   classStatement.members.push(new Simple(member.getName()));
          // } else if (KINDS_METHOD.includes(declaration.kind)) {
          //   classStatement.members.push(new Method(member.getName()));
          // } else if (KINDS_PROPERTY.includes(declaration.kind)) {
          //   // `${member.getName()}: ${checker.typeToString(checker.getTypeOfSymbolAtLocation(member, member.valueDeclaration))}`
          //   classStatement.members.push(new Property(member.getName(), GlobalFactory.create(member.valueDeclaration, checker, false)));
          // }
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

    return classStatement;
  }

  private static addMember(member: Statement | undefined, clazz: Class) {
    if (member) {
      clazz.members.push(member);
    }
  }
}

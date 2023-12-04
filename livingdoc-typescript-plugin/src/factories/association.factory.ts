import { Association } from '../models/association';
import { Method } from '../models/method';
import { Property } from '../models/property';
import { Statement } from '../models/statement';
import { Type } from '../models/type';

export class AssociationFactory {
  public static create(member: Statement | undefined): (Association | undefined)[] {
    if (member) {
      if (member instanceof Type) {
        if (member.parent) {
          let parent = member.parent;
          if ((member.parent instanceof Method || member.parent instanceof Property) && parent.parent) {
            parent = parent.parent;
          }
          return [new Association(parent, member, this.rightName(member), this.associationType(member), this.associationName(member))];
        }
      } else if (member instanceof Property) {
        const property = member;
        return property.types.map(type => AssociationFactory.create(type)[0]);
      } else if (member instanceof Method) {
        const methodMember = member;
        return methodMember.returnTypes.map(type => AssociationFactory.create(type)[0]);
      }
    }
    return [undefined];
  }

  public static createInheritance(member: Statement | undefined): (Association | undefined)[] {
    if (member) {
      if (member.parent) {
        let parent = member.parent;
        if ((member.parent instanceof Method || member.parent instanceof Property) && parent.parent) {
          parent = parent.parent;
        }
        return [new Association(member, parent, undefined, '<|-', undefined)];
      }
    }
    return [undefined];
  }

  private static rightName(member: Type): string | undefined {
    return member.parent instanceof Property && (member.parent as Property).many ? '*' : undefined;
  }

  private static associationType(member: Statement): string {
    if (member.parent instanceof Method) {
      return '--';
    }
    return '-->';
  }

  private static associationName(member: Statement): string | undefined {
    if (member.parent instanceof Method) {
      return 'use';
    }
    if (member.parent instanceof Property) {
      return member.parent.name;
    }
    return undefined;
  }
}

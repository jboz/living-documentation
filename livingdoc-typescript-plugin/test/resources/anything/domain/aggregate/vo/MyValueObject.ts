import { AbstractClass } from './InheritanceClass';
import { MyAbstractBean } from './MyAbstractBean';

export interface MyValueObject {
  aString: string;
  aNumber: number;
  aBoolean: boolean;
  aUndefined: undefined;
  aNull: null;
  aAny: any;
  aSet: Set<string>;
  aArray: Array<string>;
  anotherArray: string[];
  aMap: Map<number, string>;
  beans: MyAbstractBean[];
  classesBeans: AbstractClass[];
}

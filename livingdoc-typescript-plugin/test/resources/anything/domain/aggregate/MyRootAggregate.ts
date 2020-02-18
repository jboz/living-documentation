import { MyRootIdentity } from './MyRootIdentity';
import { MyValueObject } from './MyValueObject';

export interface MyRootAggregate {
  identity: MyRootIdentity;
  vo: MyValueObject;
}

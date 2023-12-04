import { MyRootIdentity } from './vo/MyRootIdentity';
import { MyValueObject } from './vo/MyValueObject';

export interface MyRootAggregate {
  identity: MyRootIdentity;
  vo: MyValueObject;
}

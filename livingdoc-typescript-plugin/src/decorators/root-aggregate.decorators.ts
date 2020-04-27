export function RootAggregate(constructor: Function) {
  Object.seal(constructor);
  Object.seal(constructor.prototype);
}

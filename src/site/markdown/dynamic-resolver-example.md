## Dynamic Injection Resolver Example

Dynamic Injection allow users to customize in some way the standard JSR-330 injection resolution.
Using simple examples we will illustrates three kinds of dynamic injection:

1. Dynamic Type Injection
1. Dynamic Qualified Injection
1. Dynamic Qualified Type Injection


### Dynamic Type Injection

Dynamic Type injection resolution is driven by the desire to dynamically resolve a specific type. Suppose we
have the following interface:

```java
public interface Engine {

    String getManufacturer();

    int getThrust();

}
```

Now suppose this Engine contract is injected into a Plane Service:

```java
    @Service
    public class Plane {

        private final Engine engine;

        @Inject
        Plane(Engine engine) {
            this.engine = engine;
        }
    }
```

Under normal circumstances the Engine contract will not be satisfied by the standard JSR-330 resolver unless
an Engine service or factory implementation is added to the [ServiceLocator][servicelocator]. That is to say if
we were to retrieve the Plane service from the [ServiceLocator][servicelocator] or inject it to another service we
will encounter [UnsatisfiedDependencyException][usde].

This is where dynamic injection comes in handy. Instead of having to explicitly define and add an Engine
service or Engine factory implementation to the [ServiceLocator][servicelocator] we can simply create an
[DynamicResolver][dynamicresolver] implementation that facilitates dynamic resolution of an Engine. First let's
create an Engine implementation:

```java
public class EngineImpl implements Engine {

    private final String manufacturer;
    private final int thrust;

    public EngineImpl(String manufacturer, int thrust) {
        this.manufacturer = manufacturer;
        this.thrust = thrust;
    }

    @Override
    public String getManufacturer() {
        return manufacturer;
    }

    @Override
    public int getThrust() {
        return thrust;
    }

}
```

Now let's create an Engine type dynamic resolver:

```java
@Service
public class EngineTypeDynamicResolver implements DynamicResolver<Engine> {

    @Override
    public Engine resolve(DynamicInjectee<Engine> injectee) {
        //create a new Engine instance dynamically. note that you can inject
        //services into the resolver to help you construct or retrieve an Engine.
        return new EngineImpl("Pratt & Whitney", 98000);
    }

}
```

Note that the above [DynamicResolver][dynamicresolver] implementation is just a regular Singleton scoped service that
takes the type it resolves (Engine) as its type parameter. Once the EngineTypeDynamicResolver is added to the ServiceLocator
any service that requires injection of an Engine will result in EngineTypeDynamicResolver being utilized to resolve and
provide an Engine instance.

### Dynamic Qualified Injection

While Dynamic Type injection deals with type based resolution, Dynamic Qualified injection deals with
[Qualifier][qualifier] based resolution. Suppose we are interested in handling resolution of any type
annotated with the following qualifier:

```java
@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface Boeing {

}
```

Now let's suppose we inject an instance of the Plane service into a Flight service using the above @Boeing qualifier:

```java
@Service
public class Flight {

    private final Plane plane;

    @Inject
    Flight(@Boeing Plane plane) {
        this.plane = plane;
    }

    public Plane getPlane() {
        return plane;
    }

}
```

Note that unless we have added a service or factory qualified with @Boeing the Plane injection above will not be
resolved. Once again we can turn to dynamic injection to help us dynamically create a qualified instances:

```java
@Boeing
@Service
public class BoeingDynamicResolver implements DynamicResolver<Object> {

    @Override
    public Object resolve(DynamicInjectee<Object> injectee) {
        //create a new Plane from scratch. note that you can inject services
        //into the resolver to help you construct or retrieve Plane instances.
        return new Plane(new EngineImpl("Royce Royce", 93000));
    }

}
```

In the BoeingDynamicResolver above notice that we have placed the @Boeing qualifier on the resolver and
neglect to specify the dynamic resolver type parameter. By placing a @Boeing qualifier on the resolver and
using omitting the type parameter we are telling the system the resolver can satisfy the injection
of any type qualified with @Boeing. Of course, with the flexibility of being able to handle any type comes
the responsibility of ensuring the object returned by the resolver can be injected safely into the injection point.

Note that dynamic qualified injection resolution does not limited you to a single qualifier. You can use
multiple qualifiers on injection points and as long as these qualifiers are placed on your DynamicResolver
implementation resolution will be satisfied by your dynamic resolver.

Also note that placing qualifiers on DynamicResolver implementations is for hinting purpose only. Qualifiers
on injection points on the other hand might be something you are interested and therefore are accessible from
the DynamicInjectee parameter.


### Dynamic Qualified Type Injection

As you might have guessed Dynamic Qualified Type Injection combines the functionality of Dynamic Type Injection
and Dynamic Qualified Injection we have demonstrated so far. Suppose we are interested in handling resolution of
Flight type annotated with the following qualifier:

```java
@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface Airline {

    String value() default "";
}

```

Now suppose we inject an instance of a Flight service into a Traveler service using the above
Airline qualifier:

```java
@Service
public class Traveler {

    private final Flight flight;

    @Inject
    Traveler(@Airline("United") Flight flight) {
        this.flight = flight;
    }

    public Flight getFlight() {
        return flight;
    }

}
```

Once again, there is no @Airline qualified Flight service in the system and so we will resort to using
dynamic resolver to resolve it. Unlike the previous dynamic qualified injection example where we resolved
any type qualified with @Boeing with dynamic qualified type injection we are only interested in resolving
an explicit type (Flight) qualified with an explicity qualifier (@Airline).


```java
@Airline
@Service
public class AirlineFlightDynamicResolver implements DynamicResolver<Flight> {

    @Override
    public Flight resolve(DynamicInjectee<Flight> injectee) {
        //create a new Flight from scratch. note that you can inject services
        //into the resolver to help you construct or retrieve Flight instances.
        return new Flight(new Plane(new EngineImpl("Royce Royce", 93000)));
    }

}
```

In the AirlineFlightDynamicResolver above notice that we have placed the @Airline qualifier on the
resolver and specified Flight as the dynamic type parameter. Again, by placing a @Airline qualifier
on the resolver and using Flight as our type parameter we are telling the system the resolver can
only satisfy the injection of Flight types qualified with @Airline.


### Dynamic Resolution Process

Now that we have learned about the various kinds of dynamic injection it is important to understand
how dynamic resolution works. When an unsatisfiable injectee is encountered the dynamic resolution
process starts with the most restrictive premise, we are trying to perform a Dynamic Qualified Type
injection. If a resolver is not found for the injectee's type and qualifiers then we look for an
Dynamic Qualified resolver. If again a dynamic resolver for the injectee's qualifiers is not found
we perform a last ditch effort and look for Dynamic Type resolver. If we still unable to find a dynamic
resolver for the injectee's type then the injectee can not be resolved and [UnsatisfiedDependencyException][usde]
will be thrown.


### Conclusion

In the examples above we have learned how to create and utilize dynamic injection to dynamically
satisfy type, qualified and qualified type injections. For a certain classes of problems that require
dynamic configuration dynamic injection be a powerful tool. If you are interested in exploring this
topic more please take a look at the [examples][dynamicexample].


[usde]: apidocs/org/glassfish/hk2/api/UnsatisfiedDependencyException.html
[servicelocator]: apidocs/org/glassfish/hk2/api/ServiceLocator.html
[dynamicresolver]: apidocs/org/glassfish/hk2/api/DynamicResolver.html
[dynamicresolver]: apidocs/org/glassfish/hk2/api/DynamicResolver.html
[qualifier]: http://docs.oracle.com/javaee/6/api/javax/inject/Qualifier.html
[dynamicexample]: https://github.com/hk2-project/hk2/tree/master/examples/dynamic-resolver
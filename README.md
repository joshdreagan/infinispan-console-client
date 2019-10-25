# Infinispan Console Client

To put:

`key=value`

To get:

 `key`

To remove:

`key=`

__Plain text__

```
mvn spring-boot:run '-Dinfinispan.cache-container.servers=localhost:11222' '-Dinfinispan.cache-container.security.authentication.enabled=true' '-Dinfinispan.cache-container.security.authentication.username=client' '-Dinfinispan.cache-container.security.authentication.password=password'
```

__SSL__

```
mvn spring-boot:run '-Dinfinispan.cache-container.servers=localhost:11222' '-Dinfinispan.cache-container.client-intelligence=BASIC' '-Dinfinispan.cache-container.security.authentication.enabled=true' '-Dinfinispan.cache-container.security.authentication.server-name=cache-service' '-Dinfinispan.cache-container.security.authentication.username=client' '-Dinfinispan.cache-container.security.authentication.password=password' '-Dinfinispan.cache-container.security.ssl.enabled=true' '-Dinfinispan.cache-container.security.ssl.sni-host-name=localhost' '-Dinfinispan.cache-container.security.ssl.trust-store=/home/user1/infinispan-console-client/client.ts' '-Dinfinispan.cache-container.security.ssl.trust-store-password=password'
```


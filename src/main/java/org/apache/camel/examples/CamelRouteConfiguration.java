/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.examples;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.infinispan.InfinispanOperation;
import org.apache.logging.log4j.util.Strings;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CamelRouteConfiguration extends RouteBuilder {
  
  @Autowired
  private InfinispanProperties infinispanProperties;
  
  @Bean
  @Autowired
  private Configuration cacheContainerConfiguration() {
    InfinispanProperties.CacheContainerProperties cacheContainerProperties = infinispanProperties.getCacheContainer();
    return new ConfigurationBuilder()
      .clientIntelligence(cacheContainerProperties.getClientIntelligence())
      .addServers(Strings.join(cacheContainerProperties.getServers(), ','))
      .security()
        .authentication()
        .enabled(cacheContainerProperties.getSecurity().getAuthentication().isEnabled())
        .username(cacheContainerProperties.getSecurity().getAuthentication().getUsername())
        .password(cacheContainerProperties.getSecurity().getAuthentication().getPassword())
        .realm(cacheContainerProperties.getSecurity().getAuthentication().getRealm())
        .serverName(cacheContainerProperties.getSecurity().getAuthentication().getServerName())
        .saslMechanism(cacheContainerProperties.getSecurity().getAuthentication().getSaslMechanism())
        .saslQop(cacheContainerProperties.getSecurity().getAuthentication().getSaslQop())
      .ssl()
        .enabled(cacheContainerProperties.getSecurity().getSsl().isEnabled())
        .sniHostName(cacheContainerProperties.getSecurity().getSsl().getSniHostName())
        .trustStoreFileName(cacheContainerProperties.getSecurity().getSsl().getTrustStore().toAbsolutePath().toString())
        .trustStorePassword(cacheContainerProperties.getSecurity().getSsl().getTrustStorePassword().toCharArray())
      .build()
    ;
  }
  
  @Bean
  private RemoteCacheManager cacheManager(Configuration cacheContainerConfiguration) {
    RemoteCacheManager rcm = new RemoteCacheManager(cacheContainerConfiguration);
    /*rcm.getCacheNames().forEach((t) -> {
      System.out.println(t);
      RemoteCache rc = rcm.getCache(t);
      rc.keySet().forEach((k) -> {
        System.out.println(k + "=" + rc.get(k));
      });
    });*/
    return rcm;
  }
  
  @Override
  public void configure() {
    
    from("stream:in?promptMessage=RAW(> )&initialPromptDelay=0")
      .filter(simple("${body} == ${null} || ${body} == ''"))
        .stop()
      .end()
      .setHeader("ConsoleInput", body())
      .setBody(simple("${body.split('=')}"))
      .choice()
        .when(simple("${header.ConsoleInput} regex '[^=]+'"))
          .to("direct:get")
        .when(simple("${header.ConsoleInput} regex '[^=]+=\\S+.*'"))
          .to("direct:put")
        .when(simple("${header.ConsoleInput} regex '[^=]+='"))
          .to("direct:remove")
        .otherwise()
          .setBody(constant("Unable to parse input: [${header.ConsoleInput}]"))
          .to("stream:out")
      .end()
    ;
    
    from("direct:get")
      .log(LoggingLevel.DEBUG, log, "Operation: [GET], Key: [${body[0]}]")
      .setHeader("CamelInfinispanOperation", constant(InfinispanOperation.GET))
      .setHeader("CamelInfinispanKey", simple("${body[0]}"))
      .toF("infinispan://%s?cacheContainerConfiguration=#cacheContainerConfiguration", infinispanProperties.getCache().getName())
      .to("stream:out")
    ;
    
    from("direct:put")
      .log(LoggingLevel.DEBUG, log, "Operation: [PUT], Key: [${body[0]}], Value: [${body[1]}]")
      .setHeader("CamelInfinispanOperation", constant(InfinispanOperation.PUT))
      .setHeader("CamelInfinispanKey", simple("${body[0]}"))
      .setHeader("CamelInfinispanValue", simple("${body[1]}"))
      .toF("infinispan://%s?cacheContainerConfiguration=#cacheContainerConfiguration", infinispanProperties.getCache().getName())
    ;
    
    from("direct:remove")
      .log(LoggingLevel.DEBUG, log, "Operation: [REMOVE], Key: [${body[0]}]")
      .setHeader("CamelInfinispanOperation", constant(InfinispanOperation.REMOVE))
      .setHeader("CamelInfinispanKey", simple("${body[0]}"))
      .toF("infinispan://%s?cacheContainerConfiguration=#cacheContainerConfiguration", infinispanProperties.getCache().getName())
    ;
  }
}

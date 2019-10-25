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

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import org.infinispan.client.hotrod.configuration.ClientIntelligence;
import org.infinispan.client.hotrod.configuration.SaslQop;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "infinispan")
public class InfinispanProperties {
  
  private CacheContainerProperties cacheContainer;
  private CacheProperties cache;

  public CacheContainerProperties getCacheContainer() {
    return cacheContainer;
  }

  public void setCacheContainer(CacheContainerProperties cacheContainer) {
    this.cacheContainer = cacheContainer;
  }

  public CacheProperties getCache() {
    return cache;
  }

  public void setCache(CacheProperties cache) {
    this.cache = cache;
  }
  
  public static class CacheContainerProperties {
    
    private List<String> servers = Collections.singletonList("localhost:11222");
    private ClientIntelligence clientIntelligence = ClientIntelligence.TOPOLOGY_AWARE;
    private SecurityProperties security;

    public List<String> getServers() {
      return servers;
    }

    public void setServers(List<String> servers) {
      this.servers = servers;
    }

    public ClientIntelligence getClientIntelligence() {
      return clientIntelligence;
    }

    public void setClientIntelligence(ClientIntelligence clientIntelligence) {
      this.clientIntelligence = clientIntelligence;
    }

    public SecurityProperties getSecurity() {
      return security;
    }

    public void setSecurity(SecurityProperties security) {
      this.security = security;
    }
    
    public static class SecurityProperties {
      
      private AuthenticationProperties authentication;
      private SslProperties ssl;

      public AuthenticationProperties getAuthentication() {
        return authentication;
      }

      public void setAuthentication(AuthenticationProperties authentication) {
        this.authentication = authentication;
      }

      public SslProperties getSsl() {
        return ssl;
      }

      public void setSsl(SslProperties ssl) {
        this.ssl = ssl;
      }
      
      public static class AuthenticationProperties {
        
        private boolean enabled = false;
        private String username;
        private String password;
        private String realm = "ApplicationRealm";
        private String serverName;
        private String saslMechanism = "DIGEST-MD5";
        private SaslQop saslQop = SaslQop.AUTH;

        public boolean isEnabled() {
          return enabled;
        }

        public void setEnabled(boolean enabled) {
          this.enabled = enabled;
        }

        public String getUsername() {
          return username;
        }

        public void setUsername(String username) {
          this.username = username;
        }

        public String getPassword() {
          return password;
        }

        public void setPassword(String password) {
          this.password = password;
        }

        public String getRealm() {
          return realm;
        }

        public void setRealm(String realm) {
          this.realm = realm;
        }

        public String getServerName() {
          return serverName;
        }

        public void setServerName(String serverName) {
          this.serverName = serverName;
        }

        public String getSaslMechanism() {
          return saslMechanism;
        }

        public void setSaslMechanism(String saslMechanism) {
          this.saslMechanism = saslMechanism;
        }

        public SaslQop getSaslQop() {
          return saslQop;
        }

        public void setSaslQop(SaslQop saslQop) {
          this.saslQop = saslQop;
        }
      }
    
      public static class SslProperties {

        private boolean enabled = false;
        private String sniHostName;
        private Path trustStore;
        private String trustStorePassword;

        public boolean isEnabled() {
          return enabled;
        }

        public void setEnabled(boolean enabled) {
          this.enabled = enabled;
        }

        public String getSniHostName() {
          return sniHostName;
        }

        public void setSniHostName(String sniHostName) {
          this.sniHostName = sniHostName;
        }

        public Path getTrustStore() {
          return trustStore;
        }

        public void setTrustStore(Path trustStore) {
          this.trustStore = trustStore;
        }

        public String getTrustStorePassword() {
          return trustStorePassword;
        }

        public void setTrustStorePassword(String trustStorePassword) {
          this.trustStorePassword = trustStorePassword;
        }
      }
    }
  }
  
  public static class CacheProperties {
    
    private String name;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }
}

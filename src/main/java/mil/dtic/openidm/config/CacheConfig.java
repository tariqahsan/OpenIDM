package mil.dtic.openidm.config;

// import org.ehcache.CacheManager;
// import org.ehcache.config.builders.CacheConfigurationBuilder;
// import org.ehcache.config.builders.CacheManagerBuilder;
// import org.ehcache.config.builders.ExpiryPolicyBuilder;
// import org.ehcache.config.builders.ResourcePoolsBuilder;
// import org.ehcache.xml.XmlConfiguration;
// import org.springframework.cache.annotation.EnableCaching;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// import org.ehcache.config.builders.*;
// import org.ehcache.config.units.EntryUnit;
// import org.ehcache.config.units.MemoryUnit;
// import java.time.Duration;

// import org.ehcache.config.builders.*;
// import org.ehcache.config.units.EntryUnit;
// import org.ehcache.config.units.MemoryUnit;
// import org.springframework.cache.CacheManager as SpringCacheManager;
// import org.springframework.cache.annotation.EnableCaching;
// import org.springframework.cache.ehcache3.Ehcache3CacheManager;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

//import java.time.Duration;


import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

import org.ehcache.config.builders.*;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public org.springframework.cache.CacheManager cacheManager() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();

        // Configure users cache
        org.ehcache.config.CacheConfiguration<String, Object> config =
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                String.class,
                Object.class,
                ResourcePoolsBuilder.newResourcePoolsBuilder()
                    .heap(200, EntryUnit.ENTRIES)
                    .offheap(10, MemoryUnit.MB)
            )
            .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(30)))
            .build();

        cacheManager.createCache("users",
            Eh107Configuration.fromEhcacheCacheConfiguration(config));

        return new JCacheCacheManager(cacheManager);
    }
}

// @Configuration
// @EnableCaching
// public class CacheConfig {
//     // That's it! Spring Boot auto-configures everything
// }

// import org.ehcache.config.builders.CacheConfigurationBuilder;
// import org.ehcache.config.builders.CacheManagerBuilder;
// import org.ehcache.config.builders.ExpiryPolicyBuilder;
// import org.ehcache.config.builders.ResourcePoolsBuilder;
// import org.ehcache.config.units.EntryUnit;
// import org.springframework.cache.annotation.EnableCaching;
// import org.springframework.context.annotation.Bean;
// @EnableCaching
// public class CacheConfig {

    // @Bean
    // public CacheManager cacheManager() {
    //     org.ehcache.config.Configuration xmlConfig =
    //             new XmlConfiguration(getClass().getResource("/ehcache.xml"));
    //     return new EhCacheCacheManager(org.ehcache.jsr107.EhcacheCachingProvider
    //             .getCachingProvider()
    //             .getCacheManager(null, getClass().getClassLoader(), xmlConfig));
    // }

    // @Bean
    // public CacheManager cacheManager() {
    //     // Define simple programmatic cache configuration
    //     var cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(
    //             String.class, Object.class,
    //             ResourcePoolsBuilder.heap(100)
    //     ).withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(java.time.Duration.ofMinutes(10)))
    //      .build();

    //     // Build the cache manager
    //     var config = CacheManagerBuilder.newCacheManagerBuilder()
    //             .withCache("users", cacheConfiguration)
    //             .build(true);

    //     return new org.springframework.cache.ehcache.EhCacheCacheManager(config);
    // }





//     @Bean
//     public SpringCacheManager cacheManager() {
//         // Create an Ehcache 3 CacheManager programmatically
//         CacheManager ehcacheManager = CacheManagerBuilder.newCacheManagerBuilder()
//                 .withCache(
//                         "users",
//                         CacheConfigurationBuilder.newCacheConfigurationBuilder(
//                                         String.class,
//                                         Object.class,
//                                         ResourcePoolsBuilder.newResourcePoolsBuilder()
//                                                 .heap(100, EntryUnit.ENTRIES)
//                                                 .offheap(10, MemoryUnit.MB)
//                                 )
//                                 .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(10)))
//                 )
//                 .build(true);

//         // Wrap the native Ehcache 3 manager in Spring’s cache abstraction
//         return new Ehcache3CacheManager(ehcacheManager);
//     }
// }





package com.dayone.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@RequiredArgsConstructor
@Configuration
public class CacheConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;


    //아래만든레디쉬설정 캐쉬에 적용시키기위한 캐쉬매니저생성
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration conf = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        //.entryTtl(특정시간동안 유효 설정)
        //키.벨류 각각 직렬화 해줘야함

        //시리얼라이져(직렬화) 지정 -> 안해주면 에러남
        //자바 오브젝트를 바이트화 직렬화하면 자바외부에서도 데이터 읽을수있음. (호환가능)
        //바이트 -> 오브젝트화 (역직렬화)
        //래디스는 자바외부 라이브러리...

        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(conf)
                .build();

        //빌더 써서 캐쉬매니져 초기화해줌.. 위아래에서 만들어준 설정을 넣어줘서
    }


    //connect 을 관리하기위한 빈 초기화
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
//        RedisStandaloneConfiguration -> 클러스터인경우 여기서 초기화하기
        //레디스가 싱글 인스턴스 서버임
        RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration();
        conf.setHostName(this.host);
        conf.setPort(this.port);
        return new LettuceConnectionFactory(conf);
    }
}

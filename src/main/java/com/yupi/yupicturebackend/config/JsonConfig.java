package com.yupi.yupicturebackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * JsonConfig 类用于配置 Jackson ObjectMapper，以解决序列化和反序列化过程中的精度丢失问题
 * 主要关注于 Long 类型数据的处理
 */
@JsonComponent
public class JsonConfig {

    /**
     * 添加 Long 转 json 精度丢失的配置
     *
     * @param builder Jackson2ObjectMapperBuilder 构建器，用于创建 ObjectMapper 实例
     * @return ObjectMapper 实例，用于 JSON 处理
     */
    @Bean
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        // 创建 ObjectMapper 实例，确保不启用 XML 映射
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();

        // 创建一个 SimpleModule 实例，用于自定义序列化器的注册
        SimpleModule module = new SimpleModule();

        // 为 Long 类型注册自定义序列化器，避免精度丢失
        module.addSerializer(Long.class, ToStringSerializer.instance);
        // 为 long 基本类型注册自定义序列化器，避免精度丢失
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);

        // 注册自定义模块到 ObjectMapper
        objectMapper.registerModule(module);

        // 返回配置好的 ObjectMapper 实例
        return objectMapper;
    }
}

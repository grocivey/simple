package com.simple.api.config;

import com.google.gson.Gson;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new MyInterceptor()).addPathPatterns("/api/**");12
        WebMvcConfigurer.super.addInterceptors(registry);
    }

    static class MyInterceptor implements HandlerInterceptor{
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            String url = "http://heart-recovery.rhb-tech.com:8001/api/token/verify/";
            Map<String,String> map = new HashMap<>();
            map.put("token",request.getHeader("token"));
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            MediaType mediaType = MediaType.parseMediaType("application/json; charset=UTF-8");
            headers.setContentType(mediaType);
            headers.add("Accept", "application/json");
            HttpEntity<String> entity = new HttpEntity<>(new Gson().toJson(map), headers);
            String resultData = restTemplate.postForObject(url, entity, String.class);
            Map<String,Object> rem = new Gson().fromJson(resultData,HashMap.class);
            if ("200".equals(String.valueOf(rem.get("code")))){
                System.out.println("校验通过");
                return true;
            }
            System.out.println("校验失败");
            return false;
        }


    }
}

package eu.sharedtravel.app.config;

import eu.sharedtravel.app.common.security.UserResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final UserResolver userResolver;

    @Override
    public void addArgumentResolvers(@NonNull List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userResolver);
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    }
}

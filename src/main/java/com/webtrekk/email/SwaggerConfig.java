package com.webtrekk.email;

import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

//    @Value("${swagger.email.title}")
//    private String title;
//    @Value("${swagger.email.description}")
//    private String description;
//    @Value("${swagger.email.version}")
//    private String version;
//    @Value("${swagger.email.contact.name}")
//    private String contactName;
//    @Value("${swagger.email.contact.url}")
//    private String contactURL;
//    @Value("${swagger.email.contact.email}")
//    private String contactEmail;
//    @Value("${swagger.email.license}")
//    private String license;
//    @Value("${swagger.email.licenseUrl}")
//    private String licenseUrl;

//    @Bean
//    public Docket newsApi() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .select()
//                .paths(paths())
//                .build()
//                .apiInfo(apiInfo());
//    }

//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                .title(title)
//                .description(description)
//                .termsOfServiceUrl(licenseUrl)
//                .contact(new Contact(contactName, contactURL, contactEmail))
//                .license(license)
//                .version(version)
//                .build();
//    }
//
//    private Predicate<String> paths() {
//        return not(regex("/error*"));
//    }
}

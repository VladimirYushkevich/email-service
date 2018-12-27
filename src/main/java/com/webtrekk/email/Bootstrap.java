package com.webtrekk.email;

import com.webtrekk.email.dto.EmailAvro;
import com.webtrekk.email.messaging.EmailChannels;
import com.webtrekk.email.services.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Component
@EnableBinding(EmailChannels.class)
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class Bootstrap implements ApplicationRunner {

    private final MessageService messageService;

    @Override
    public void run(ApplicationArguments args) {

        List<String> froms = Arrays.asList("v@a.b", "r@c.d", "e@f.g", "y@z.x", "t@y.d", "h@k.l");
        List<String> subjects = Arrays.asList("blog", "sitemap", "initializr", "news", "colophon", "about");

        AtomicLong counter = new AtomicLong(0L);
        final long limit = 10L;

        Runnable runnable = () -> {

            String rSubject = subjects.get(new Random().nextInt(subjects.size()));
            String rFrom = froms.get(new Random().nextInt(froms.size()));

            final EmailAvro message = EmailAvro.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setFrom(rFrom)
                    .setSubject(rSubject)
                    .build();
            try {
                if (counter.get() % limit == 0) {
                    log.info("check sum: {}/{}", limit, messageService.stats());
                    Thread.sleep(60000);
                }
                messageService.send(message);
                counter.incrementAndGet();
            } catch (Exception e) {
                log.error("can't sent, reason: ", e);
            }
        };

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(runnable, 1, 1, TimeUnit.SECONDS);
    }
}

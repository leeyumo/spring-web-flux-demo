package com.leeyumo.webfluxDemo.web.endpoints;

import com.leeyumo.webfluxDemo.model.CityInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
public class DemoController {

    @GetMapping("checkConnected")
    public Mono<String> checkConnected() {
        return Mono.just("本地Spring Boot应用连接成功！");
    }

    @GetMapping("cities")
    public Flux<CityInfo> getCities() {
        List<CityInfo> cityInfos = new ArrayList<>();
        cityInfos.add(new CityInfo("北京", "10万平方米", "中国"));
        cityInfos.add(new CityInfo("上海", "8万平方米", "中国"));
        cityInfos.add(new CityInfo("纽约", "6万平方米", "美国"));
        cityInfos.add(new CityInfo("伦敦", "5万平方米", "英国"));
        Flux<CityInfo> cityInfoFlux = Flux.fromIterable(cityInfos);
        cityInfoFlux.flatMap(cityInfo -> {
            return Flux.just(cityInfo.getCityName()+"modify").delayElements(Duration.ofSeconds(2));
        })
                .doOnComplete(() -> {log.info("finished");})
        .subscribe(log::info);
        return cityInfoFlux;

    }

    @GetMapping("checkCloudTSign")
    public Mono<Boolean> checkCloudTSign(@RequestParam String appId,
                                         @RequestParam String signStr,
                                         @RequestParam String signValue) {
        StringBuilder builder = new StringBuilder();
        builder.append("appKeyDepartmentA");
        builder.append(signStr);
        builder.append("appKeyDepartmentA");
        String s = DigestUtils.md5DigestAsHex(builder.toString().getBytes()).toUpperCase();
        return Mono.just(Objects.equals(s,signValue));
    }

}

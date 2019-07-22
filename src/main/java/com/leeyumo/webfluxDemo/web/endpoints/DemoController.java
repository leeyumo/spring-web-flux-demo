package com.leeyumo.webfluxDemo.web.endpoints;

import com.alibaba.fastjson.JSON;
import com.leeyumo.webfluxDemo.model.CityInfo;
import com.leeyumo.webfluxDemo.tool.ExecutorUtils;
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
import java.util.concurrent.*;

@Slf4j
@RestController
public class DemoController {

    @GetMapping("checkConnected")
    public Mono<String> checkConnected() {
        return Mono.just("本地Spring Boot应用连接成功！");
    }

    @GetMapping("cities")
    public Flux<CityInfo> getCities() throws InterruptedException {
//        CountDownLatch countDownLatch = new CountDownLatch(1);
        List<CityInfo> cityInfos = new ArrayList<>();
        cityInfos.add(new CityInfo("北京", "10万平方米", "中国"));
        cityInfos.add(new CityInfo("上海", "8万平方米", "中国"));
        cityInfos.add(new CityInfo("纽约", "6万平方米", "美国"));
        cityInfos.add(new CityInfo("伦敦", "5万平方米", "英国"));
        Flux<CityInfo> cityInfoFlux = Flux.fromIterable(cityInfos);
        cityInfoFlux.flatMap(cityInfo ->{
                    Mono<String> stringMono = Mono.just("modify"+cityInfo.getCityName())
                            .delayElement(Duration.ofSeconds(6));
                    stringMono.subscribe(s -> cityInfo.setCityName(s));
                    return stringMono;
                })
                .doOnNext(s -> log.info("Next:{}",s))
                .doOnComplete(() -> {
//                    countDownLatch.countDown();
                    log.info("Completed:\n{}", JSON.toJSONString(cityInfos));
                })
                .subscribe(s -> log.info("Subscribe:{}",s));
        //下面的完成日志会先打印，体验Flux非阻塞
        log.info("Finished");
        //加入，注释countDownLatch相关代码，体验结果阻塞
//        countDownLatch.await();
        return cityInfoFlux;
    }

    //对比上面的非阻塞Flux,执行此main方法体验异步的Future
    public static void main(String[] args) {
        List<Integer> intList = new ArrayList();
        List<CompletableFuture> futures = new ArrayList<>();
        for (int i=1;i<=10;i++){
            int finalI = i;
            futures.add(CompletableFuture.runAsync(() -> {
                ExecutorUtils.sleepTwoSeconds();
                System.out.println("adding:"+finalI);
                intList.add(finalI);
            }));

        }
        System.out.println("at there");
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        System.out.println("Finished");
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

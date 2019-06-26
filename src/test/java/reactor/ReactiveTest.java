package reactor;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class ReactiveTest {
    private Flux<String> getZipDescFlux() {
        String desc = "Zip two sources together, that is to say wait for all the sources to emit one element and combine these elements once into a Tuple2.";
//        String desc = "Flux";
        String[] split = desc.split("\\s+");
        return Flux.fromArray(split);  // 1
    }

    @Test
    public void testSimpleOperators() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);  // 2
        Flux.zip(
                getZipDescFlux(),
                Flux.interval(Duration.ofMillis(1000)))  // 3
                .subscribe(
                        t -> System.out.println(t.getT1()),
                        null,
                        countDownLatch::countDown);    // 4
        countDownLatch.await(10, TimeUnit.SECONDS);     // 5
    }

    @Test
    public void testFlatMap(){
        Flux.just("flux", "mono")
                .flatMap(s -> Flux.fromArray(s.split("\\s*"))   // 1
                        .delayElements(Duration.ofMillis(100))) // 2
                .doOnNext(System.out::print)
                .subscribe();
        Flux<Long> interval = Flux.interval(Duration.ofMillis(200));
        System.out.println("finished");
    }

    @Test
    public void testZip(){
        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5, 6);
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D");
        Flux.zip(stringFlux, integerFlux)
                .subscribe(obj -> System.out.println(obj.getT1()+obj.getT2()),null,()-> System.out.println("done"));
        System.out.println("finished");
    }

    @Test
    public void testCountDownLatch(){
        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.countDown();
        System.out.println("finished");
    }
}

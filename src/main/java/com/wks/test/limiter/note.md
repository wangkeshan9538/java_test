sleep 控制器 是不是也需要 进行序列化




##为什么 初始化后  nextTick 是1000左右？
这段代码  在setRate的时候调用 ，所以nextFreeTicketMicros直接等于nowMicros ，相当于直接放一个令牌
```angular2html
    void resync(long nowMicros) {
        // if nextFreeTicket is in the past, resync to now
        if (nowMicros > nextFreeTicketMicros) {
            double newPermits = (nowMicros - nextFreeTicketMicros) / coolDownIntervalMicros();
            storedPermits = min(maxPermits, storedPermits + newPermits);
            nextFreeTicketMicros = nowMicros;
        }
    }

```
##为甚么令牌数量出现小数？
还是上一段代码导致

## 考虑到元数据需要可被序列化 ，所以 sleepWatch的数据也要可以被序列化 
所以改写了SleepWatch，不再使用 
>Stopwatch stopwatch = Stopwatch.createStarted();

而是使用 System.nanoTime() 来计算
然后发现 直接使用nanoTime  初始时候的  "nextFreeTicketMicros":198
而使用StopWatch 大概在 1500 左右，本来这个值应该是越小越好，但出于好奇，测试了一下，发现
stopwatch.elapsed(MICROSECONDS); 这个方法的耗时竟然在1000多 ，就是说这个方法算的是逝去的时间 结果计算过程中还加上了自己方法执行的时间，这误差有点大
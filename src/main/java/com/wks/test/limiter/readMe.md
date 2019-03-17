# 主要思路

抽出 guava 中的RateLimiter的代码 ，并重写部分代码，使得 里面的主要元数据 可以被get出来 ，放入reids存储，

这样，如果是多台机器作为一个集群，那么就可以每次 从redis中拿出限流信息，然后在本机做处理之后在放回redis中，所以限流处理操作时需要分布式锁，



## 为什么 初始化后  nextTick 是1000左右？

这段代码  在setRate的时候调用 ，所以nextFreeTicketMicros直接等于nowMicros ，相当于直接放一个令牌
```
    void resync(long nowMicros) {
        // if nextFreeTicket is in the past, resync to now
        if (nowMicros > nextFreeTicketMicros) {
            double newPermits = (nowMicros - nextFreeTicketMicros) / coolDownIntervalMicros();
            storedPermits = min(maxPermits, storedPermits + newPermits);
            nextFreeTicketMicros = nowMicros;
        }
    }

```

## 为甚么令牌数量出现小数？

还是上一段代码导致


## 考虑到元数据需要可被序列化 ，所以 sleepWatch的数据也要可以被序列化 

所以改写了SleepWatch，不再使用 
>Stopwatch stopwatch = Stopwatch.createStarted();

而是使用 System.nanoTime() 来计算

然后发现 直接使用nanoTime  初始时候的  "nextFreeTicketMicros":198

而使用StopWatch 大概在 1500 左右，本来这个值应该是越小越好，但出于好奇，测试了一下，发现stopwatch.elapsed(MICROSECONDS); 这个方法的耗时竟然在1000多 ，就是说这个方法算的是逝去的时间 结果计算过程中还加上了自己方法执行的时间，这误差有点大


## 测试代码里发现 nextFreeTiket 初始化 会到4500 多
查看了很久，发现是中间有一个加载类的操作，优化后消失

# 存在问题

1. 因为限流的 思路是用当前时间-限流的开始时间，来计算这个时间间隔产生了多少令牌，如果 限流 的处理是在集群的某一个节点进行，那么就需要保证集群中每一台机子本地时间都是一致的，
这么做风险太大， 我觉得更好的做法应该是 ，将限流单独做成一个服务。限流的处理就只在服务中 进行，而不是在单个的节点，
2. 而且 锁 也可以做优化，只锁 想要限流的那一条，而不是将限流操作整个锁住，
3. 代码没整理， 
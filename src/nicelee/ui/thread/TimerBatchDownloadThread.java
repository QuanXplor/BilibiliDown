package nicelee.ui.thread;

import nicelee.bilibili.INeedLogin;
import nicelee.bilibili.push.IPush;
import nicelee.bilibili.push.PushFace;
import nicelee.bilibili.util.HttpCookies;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.TimeUtil;
import nicelee.ui.Global;

import java.net.HttpCookie;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.LongSupplier;

/**
 * 定时批量下载
 *
 * @author: QuanXplor
 * @date: 2023-11-28 14:16
 */
public class TimerBatchDownloadThread extends Thread {

    private IPush push=new PushFace();

    public TimerBatchDownloadThread() {
        this.setName("TimerBatchDownloadThread-" +  count.incrementAndGet());
    }

    private static AtomicInteger count =new AtomicInteger(0);

    /**
     * 5-15分钟之间的随机数
     * @return
     */
    private long sleepTime (){
        long result = new Random().nextInt(10 * 60);
        // 获取当前时间
        LocalDateTime currentTime = LocalDateTime.now();
        // 获取今天的0点0分0秒
        LocalDateTime midnight = LocalDateTime.of(currentTime.toLocalDate(), LocalTime.MIDNIGHT);
        // 计算当前时间距离今天0点0分0秒已经过了多少秒
        long secondsPassed = ChronoUnit.SECONDS.between(midnight, currentTime);
        // 早上8点到凌晨1点可以触发进行下载
        if (secondsPassed >= 8 * 60 * 60 || secondsPassed < 60 * 60) {
            // 5-15分钟随机
            result = (result + 5 * 60) * 1000;
            Logger.println("间隔时间:"+ TimeUtil.secondsToHMS(result/1000));
        }else{
            // 等到早上8点再开始下载
            result = (result + ChronoUnit.SECONDS.between(currentTime, LocalDateTime.of(currentTime.toLocalDate(), LocalTime.of(8,0)))) * 1000;
            Logger.println("开始休眠:"+ TimeUtil.secondsToHMS(result/1000));
        }
        return result;
    }

    // 间隔多少次检查一次
    private static Integer checkLoginInterval=6;
    private Long loopTime=1L;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void run() {
        while(true) {
            try {
                long sleepTime = sleepTime();
                // 免得时间写的不对卡死了
                if(sleepTime<500){
                    sleepTime=500;
                }
                Thread.sleep(sleepTime);
                // 大概一小时会校验一次cookie是否有效
                if (Global.isLogin && (loopTime % checkLoginInterval != 0 || isLoginCheck())) {
                    long startTime=System.currentTimeMillis();
                    BatchDownloadThread batchDownloadThread = new BatchDownloadThread(Global.selectBatchDownloadConfigName,false);
                    FutureTask<Map<String, Object>> task = new FutureTask<>(batchDownloadThread);
                    Thread thread=new Thread(task,batchDownloadThread.getName());
                    thread.start();
                    Map<String, Object> pushInfo = task.get();
                    if(Global.batchDownloadConfigPushSwitch) {
                        if(pushInfo!=null && !pushInfo.isEmpty()) {
                            pushInfo.put("startTime",sdf.format(new Date(startTime)));
                            pushInfo.put("cost",TimeUtil.secondsToHMS((System.currentTimeMillis()-startTime)/1000));
                            push.push(pushInfo);
                        }
                    }
                }
                loopTime++;
            } catch (InterruptedException e) {
                Logger.println(Thread.currentThread().getName() + " 睡眠故障");
            } catch (Exception e) {
                Logger.println(e.getMessage());
            }
        }
    }
    // 检查cookie是否有效
    private boolean isLoginCheck(){
        boolean result=false;
        List<HttpCookie> cookies = HttpCookies.getGlobalCookies();
        if(cookies!=null && cookies.size()>0) {
            INeedLogin inl = new INeedLogin();
            result=inl.getLoginStatus(cookies);
        }
        return result;
    }

}

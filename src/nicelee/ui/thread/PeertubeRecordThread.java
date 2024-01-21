package nicelee.ui.thread;

import nicelee.bilibili.INeedAV;
import nicelee.bilibili.downloaders.IDownloader;
import nicelee.bilibili.downloaders.impl.AudioDownloader;
import nicelee.bilibili.downloaders.impl.FLVDownloader;
import nicelee.bilibili.downloaders.impl.M4SDownloader;
import nicelee.bilibili.downloaders.impl.MP4Downloader;
import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.parsers.impl.FFmpegParser;
import nicelee.bilibili.parsers.impl.VersionParser;
import nicelee.bilibili.util.Logger;
import nicelee.ui.AcquireFilePath;
import nicelee.ui.Global;
import nicelee.ui.item.DownloadInfoPanel;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 记录下载内容
 *
 * @author: A11181121050450
 * @date: 2023-12-30 18:36
 */
public class PeertubeRecordThread extends Thread {
    private static final String CONFIG_UPLOADER_CONFIG = "config/uploader.config";
    private ConcurrentHashMap<DownloadInfoPanel, IDownloader> downloadList;
    private Set<DownloadInfoPanel> successSet = new HashSet<>();
    private boolean run=true;
    public PeertubeRecordThread(){
        setName("Peertube-Record-Thread");
        this.setDaemon(true);
        downloadList = Global.downloadTaskList;
    }

    // 因为上传是个比较慢的过程，为了保障可以上传成功，所以使用文件持久化需要上传内容
    @Override
    public void run() {
        if (!Paths.get(CONFIG_UPLOADER_CONFIG).toFile().exists()) {
            try {
                Files.createFile(Paths.get(CONFIG_UPLOADER_CONFIG));
            }catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException(e.getMessage(),e);
            }
        }
        while(run){
            try{
                downloadList.entrySet().stream()
                        .filter(o-> o.getKey().getAvid().startsWith("av") ||o.getKey().getAvid().startsWith("BV"))
                        .filter(o->o.getValue().currentStatus()== StatusEnum.SUCCESS)
                        .filter(o->!successSet.contains(o.getKey()))
                        .forEach(o->{
                            Logger.printf("record item: %s",o.getKey().getAvid());
                            if(appendInfo(o.getKey().getAvid(),AcquireFilePath.acquire(o.getKey()))){
                                successSet.add(o.getKey());
                            }
                        });
                Thread.sleep(500);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        synchronized (this) {
            this.notifyAll();
        }
    }

    public boolean close() {
        try {
            synchronized (this) {
                run = false;
                this.wait();
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private boolean appendInfo(String bvId,String filePath) {
        try {
            Path savePath=Paths.get(Global.savePath);
            Path fileP=Paths.get(filePath);
            Path relativize = savePath.relativize(fileP);
            String r = relativize.toString().replaceAll("\n","\\\\n").replaceAll("\r","\\\\r");
            String content = "0,"+bvId + "," + r + System.lineSeparator();
            Files.write(Paths.get(CONFIG_UPLOADER_CONFIG)
                    , content.getBytes(StandardCharsets.UTF_8)
                    , StandardOpenOption.APPEND, StandardOpenOption.SYNC);
            return true;
        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) throws IOException {
//        String child="E:\\project\\source\\tmp\\BilibiliDown\\workspace\\download\\abc\\iStoreOS如何设置NFS共享？附mac、windows、kodi客户端使用方法-p01-80.mp4";
//        String partent="E:\\project\\source\\tmp\\BilibiliDown\\workspace\\download";
//        Path childP = Paths.get(child);
//        Path parentP=Paths.get(partent);
//        Path relativize = parentP.relativize(childP);
//        System.out.println(relativize.toString());
//        File file=new File("E:\\project\\source\\tmp\\BilibiliDown\\workspace\\download\\123.txt");
//        boolean b = file.renameTo(new File("E:\\project\\source\\tmp\\BilibiliDown\\workspace\\download\\123\n456.txt"));
//        System.out.println(b);
//        String s = "123123\n456466".replaceAll("\n","\\\\n");
//        String first = "E:\\project\\source\\tmp\\BilibiliDown\\workspace\\download\\123.txt";
////        Files.write(Paths.get(first), s.getBytes("utf-8"),StandardOpenOption.APPEND);
//        byte[] bytes = Files.readAllBytes(Paths.get(first));
//        String x = new String(bytes, "utf-8");
//        x=x.replaceAll("\\\\n","\n");
//        System.out.println(x);
//        JSONObject jsonObject=new JSONObject();
//        jsonObject.put("key",x);
//        System.out.println(jsonObject.toString());
        PeertubeRecordThread peertubeRecordThread = new PeertubeRecordThread();
        peertubeRecordThread.appendInfo("123","123");
    }


}

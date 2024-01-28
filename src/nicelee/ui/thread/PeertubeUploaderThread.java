package nicelee.ui.thread;

import nicelee.bilibili.INeedAV;
import nicelee.bilibili.model.VideoInfo;
import nicelee.bilibili.util.Logger;
import nicelee.bilibili.util.Peertube;
import nicelee.bilibili.util.StrUtil;
import nicelee.ui.Global;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 上传信息
 *
 * @author: A11181121050450
 * @date: 2023-12-30 23:32
 */
public class PeertubeUploaderThread extends Thread {
    public PeertubeUploaderThread(){
        setName("Peertube-Uploader-Thread");
        this.setDaemon(true);
    }

    @Override
    public void run() {
        Long skip=0L;
        while(true){
            try{
                try(RandomAccessFile raf=new RandomAccessFile(Paths.get("config/uploader.config").toFile(),"rw")){
                    raf.seek(skip);
                    while(raf.getFilePointer()<raf.length()){
                        try {
                            String tmpLine = raf.readLine();
                            String line = new String(tmpLine.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                            line=line.replaceAll("\\\\n","\n").replaceAll("\\\\r","\r");
                            List<String>  split = readSeparator(new ArrayList<>(3), line, 0, 3);
                            if ("0".equals(split.get(0))) {
                                INeedAV iNeedAV = new INeedAV();
                                VideoInfo desc = iNeedAV.getVideoDetail(split.get(1), Global.downloadFormat, false);
                                String cmId = "cm-" + StrUtil.toggleCase(split.get(1));
                                VideoInfo comments = iNeedAV.getVideoDetail(cmId, Global.downloadFormat, false);
                                Path path = Paths.get(Global.savePath, split.get(2));
                                if (path.toFile().exists()) {
                                    String description=(StrUtil.isBlank(desc.getBrief())?"":desc.getBrief())+"\n视频来源:https://b23.tv/"+split.get(1);
                                    boolean upload = Peertube.PeertubeUploader.upload(path.toFile(), desc.getVideoName(), description, comments.getCommont());
                                    if (upload) {
                                        raf.seek(raf.getFilePointer() - tmpLine.length() - System.lineSeparator().length());
                                        String s = "1" + line.substring(1) + System.lineSeparator();
                                        raf.write(s.getBytes(StandardCharsets.UTF_8));
                                        Logger.printf("replace Str: %s",s);
                                    }
                                } else {
                                    Logger.printf("skip no:" + raf.getFilePointer() + " bvId:" + split.get(1) + " 不存在");
                                    System.out.println("文件不存在\n" + line);
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    skip=raf.getFilePointer();
                }
                sleep(5000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private List<String> readSeparator(List<String> result,String content,int index,int num){
        if(num>0) {
            int endIndex = content.indexOf(",", index);
            String substring = content.substring(index, num<2?content.length():endIndex);
            result.add(substring);
            readSeparator(result,content,index+substring.length()+1,--num);
        }
        return result;
    }


    public static void main(String[] args) throws IOException {
        Path path = Paths.get("test.txt");
        System.out.println(path.toFile().getAbsolutePath());
        System.out.println(path.toFile().exists());
        System.out.println(Files.readAllLines(path));
        try(RandomAccessFile raf=new RandomAccessFile(path.toFile(),"rw")) {
            while(raf.getFilePointer()<raf.length()){
                String x = raf.readLine();
                System.out.println(x);
                if(x.startsWith("7")){
                    raf.seek(raf.getFilePointer()-x.length()-1);
                    raf.write("121212\n".getBytes(StandardCharsets.UTF_8));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

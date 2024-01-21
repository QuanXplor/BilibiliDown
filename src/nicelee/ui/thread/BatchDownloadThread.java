package nicelee.ui.thread;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nicelee.bilibili.downloaders.Downloader;
import nicelee.bilibili.enums.StatusEnum;
import nicelee.bilibili.model.*;
import nicelee.bilibili.push.IPush;
import nicelee.bilibili.push.PushFace;
import nicelee.bilibili.util.*;
import nicelee.ui.AcquireFilePath;
import nicelee.ui.item.DownloadInfoPanel;
import nicelee.ui.item.JOptionPane;

import nicelee.bilibili.INeedAV;
import nicelee.bilibili.enums.VideoQualityEnum;
import nicelee.bilibili.exceptions.BilibiliError;
import nicelee.bilibili.util.batchdownload.BatchDownload;
import nicelee.bilibili.util.batchdownload.BatchDownload.BatchDownloadsBuilder;
import nicelee.ui.Global;
import nicelee.ui.item.JOptionPaneManager;

public class BatchDownloadThread extends Thread implements Callable<Map<String,Object>> {

	String configFileName;
	String configFilePath;
	boolean showAlert;

	public BatchDownloadThread(String configFileName) {
		this(configFileName,true);
	}

	public BatchDownloadThread(String configFileName,boolean showAlert) {
		this.setName("Thread-BatchDownload");
		this.configFileName = configFileName;
		configFilePath = "config/" + configFileName;
		this.showAlert=showAlert;
	}
	final Pattern pagePattern = Pattern.compile("p=[0-9]+$");

	@Override
	public void run() {
		this.call();
	}

	@Override
	public Map<String,Object> call() {
		Map<String,Object> pushInfo=new HashMap<>();
		try {
			Logger.println("一键下载进行中");
			File f = ResourcesUtil.search(configFilePath);
			checkValid(f);
			List<BatchDownload> bds = new BatchDownloadsBuilder(new FileInputStream(f)).Build();
			Logger.println("一键下载进行中。。。。。");
			Logger.println(bds);
			List<BatchDownloadInfo> list=new ArrayList<>();
			for (BatchDownload batch : bds) {
				Logger.printf("[url:%s] 任务开始", batch.getUrl());
				INeedAV ina = new INeedAV();
				String validStr = ina.getValidID(batch.getUrl());
				Logger.println(validStr);
				Matcher m = pagePattern.matcher(validStr);
				boolean isPageable = true;
				if (!m.find())
					isPageable = false;
				else
					validStr = validStr.replaceFirst("p=[0-9]+$", "");
				int page = batch.getStartPage();
				boolean stopFlag = false;
				while (!stopFlag) {
					if(!isPageable && page >= 2)
						break;
					String sp = validStr + " p=" + page;
					VideoInfo avInfo = ina.getVideoDetail(sp, Global.downloadFormat, false);
					Collection<ClipInfo> clips = avInfo.getClips().values();
					if (clips.size() == 0)
						break;
					Logger.printf("当前url: %s ,page: %d, 分页查询开始进行", batch.getUrl(), page);
					for (ClipInfo clip : clips) {
						// 判断是否要停止[url:{url}] 对应的任务
						if (batch.matchStopCondition(clip, page)) {
							// 判断边界BV是否要下载
							if (batch.isIncludeBoundsBV() && batch.matchDownloadCondition(clip, page)) {
								Callable<DownloadInfoPanel> downThread = new DownloadRunnable(avInfo, clip,
										VideoQualityEnum.getQN(Global.menu_qn),showAlert);
								Future<DownloadInfoPanel> future = Global.queryThreadPool.submit(downThread);
								list.add(new BatchDownloadInfo(clip,future));
							}
							stopFlag = true;
							break;
						}
						// 判断是否要下载
						if (batch.matchDownloadCondition(clip, page)) {
							Callable<DownloadInfoPanel> downThread = new DownloadRunnable(avInfo, clip,
									VideoQualityEnum.getQN(Global.menu_qn),showAlert);
							Future<DownloadInfoPanel> future = Global.queryThreadPool.submit(downThread);
							list.add(new BatchDownloadInfo(clip,future));
						}
					}
					Logger.printf("当前url: %s ,page: %d, 分页查询完毕", batch.getUrl(), page);
					page++;
					Thread.sleep(1500);
				}
				Thread.sleep(1000);
				Logger.printf("[url:%s] 任务完毕", batch.getUrl());
				if (showAlert || batch.isAlertAfterMissionComplete()) {
					JOptionPane.showMessageDialog(null, "url:" + batch.getUrl(), "任务完毕!! " + batch.getRemark(),
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
			// 消息推送
			List<Map<String,String>> subPushInfoList=new ArrayList<>();
			if(list.size()>0) {
				for (BatchDownloadInfo info : list) {
					while (true) {
						Map<String, String> subPushInfo = new HashMap<>();
						subPushInfo.put("title", info.getClip().getAvTitle());
						subPushInfo.put("pageUrl", "https://b23.tv/"+info.getClip().getAvId());
						DownloadInfoPanel downloadInfoPanel=null;
						try {
							downloadInfoPanel = info.getDownPanel().get();
						}catch (Exception e){
							e.printStackTrace();
							subPushInfo.put("result", "未知异常，请人工检查");
							continue;
						}

						if (downloadInfoPanel == null) {
							subPushInfo.put("result", "已下载过");
						} else {
							Downloader downloader = downloadInfoPanel.iNeedAV.getDownloader();
							if (downloader.currentStatus() == StatusEnum.SUCCESS) {
								subPushInfo.put("result", "下载成功");
								String path = AcquireFilePath.acquire(downloadInfoPanel);
								File file = new File(path);
								if (file.isFile() && file.exists()) {
									subPushInfo.put("fileSize", FileUtil.acquireDiskFileSize(file));
									VideoBaseInfo videoInfo = CmdUtil.getVideoInfo(file.getAbsolutePath());
									subPushInfo.put("duration", videoInfo.getDuration());
									subPushInfo.put("resolution", videoInfo.getResolution());
								}
							} else if (downloader.currentStatus() == StatusEnum.FAIL && downloadInfoPanel.getFailCnt() >= Global.maxFailRetry) {
								subPushInfo.put("result", "下载失败，请手动重试");
							} else if (downloader.currentStatus() == StatusEnum.STOP) {
								subPushInfo.put("result", StatusEnum.STOP.getDescription());
							} else if (downloader.currentStatus() == StatusEnum.DOWNLOADING
									|| downloader.currentStatus() == StatusEnum.NONE
									|| downloader.currentStatus() == StatusEnum.PROCESSING) {

							} else {
								subPushInfo.put("result", "未知异常，请认工检查");
							}
						}
						if (subPushInfo.containsKey("result")) {
							subPushInfoList.add(subPushInfo);
							break;
						}
						Thread.sleep(500);
					}
				}
				pushInfo.put("list",subPushInfoList);
				pushInfo.put("num",subPushInfoList.size());
			}

			if(showAlert) {
				JOptionPane.showMessageDialog(null, "一键下载完毕", "OK", JOptionPane.PLAIN_MESSAGE);
			}
		} catch (BilibiliError e) {
			if(showAlert) {
				JOptionPaneManager.alertErrMsgWithNewThread("发生了预料之外的错误", ResourcesUtil.detailsOfException(e));
			}
			pushInfo.put("num","系统异常,请人工检查");
		} catch (Exception e) {
            if(showAlert) {
                JOptionPaneManager.alertErrMsgWithNewThread("发生了预料之外的错误", ResourcesUtil.detailsOfException(e));
            }
			e.printStackTrace();
			pushInfo.put("num","系统异常,请人工检查");
		}

		Logger.println("一键下载运行完毕");
		return pushInfo;
	}

	public void checkValid(File f) throws IOException, URISyntaxException {
		if (f == null || !f.exists()) {
			String docsUrl = "https://nICEnnnnnnnLee.github.io/BilibiliDown/guide/advanced/quick-batch-download";
			String warning = "批量下载配置不存在`" + configFilePath + "`!\r\n请参考配置" + docsUrl;
			Object[] options = { "确认", "前往参考文档" };
			int m = JOptionPane.showOptionDialog(null, warning, "错误", JOptionPane.YES_NO_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
			if (m == 1) {
				if (Desktop.isDesktopSupported())
					Desktop.getDesktop().browse(new URI(docsUrl));
				else {
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					Transferable trans = new StringSelection(docsUrl);
					clipboard.setContents(trans, null);
					JOptionPane.showMessageDialog(null, "相关网页链接已复制到剪贴板");
				}
			}
			throw new RuntimeException("配置文件`" + configFilePath + "`不存在");
		}
	}

}

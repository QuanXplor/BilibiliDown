> 该分支提供无图形化界面系统，也能够正常使用bilibili-downloader项目

## 使用方法
1. docker-compose.yml配置实现config和download文件夹持久化
2. 修改映射端口
3. 修改访问密码
4. 通过`docker-compose up -d`拉起项目
5. 访问`http:ip:port/vnc.html`即可查看项目
6. 通过`docker-compose down`可以关闭项目

**注意事项**
1. 不要点击最小化按钮，不然应用会卡死，需要重启项目
2. 往`docker-compose.yml`增加`restart: "always"`关闭项目后会自动重启
3. 粘贴板很魔幻，有点耐心多试试就会好用了 
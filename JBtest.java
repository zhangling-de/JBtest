/**
 * FileName: JBtest
 * Author:   Xiao Mi
 * Date:     2019-11-09 19:14
 * Description: 脚本之家
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhang;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hssf.usermodel.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 〈一句话功能简述〉<br> 
 * 〈脚本之家〉
 *
 * @author Xiao Mi
 * @create 2019-11-09
 * @since 1.0.0
 */
public class JBtest
{
    static List<String> USER_AGENT = new ArrayList<String>() {
        {
            add("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Safari/537.36 Edge/13.10586");
            add("Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko");
            add("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)");
            add("Mozilla/5.0 (Windows NT 6.2; WOW64; rv:21.0) Gecko/20100101 Firefox/21.0");
            add("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
            add("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.94 Safari/537.36");
            add("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0)");
            add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 BIDUBrowser/8.3 Safari/537.36");
            add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 Safari/537.36 SE 2.X MetaSr 1.0");
            add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.87 Safari/537.36 OPR/37.0.2178.32");
            add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.57.2 (KHTML, like Gecko) Version/5.1.7 Safari/534.57.2");
            add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
        }
    };

    String mainUrl = "https://www.jb51.net/books/";
    static ExecutorService threadPool = new ThreadPoolExecutor(4,10 ,60,
            TimeUnit.SECONDS,new LinkedBlockingQueue<>());

    static LinkedBlockingQueue<List<JBContent>> lists;

    public void run(){
        lists = new LinkedBlockingQueue<>();
        Map<String, String> map = getUrl(mainUrl);
        Set<String> set = map.keySet();
        Iterator<String> it = set.iterator();
        while(it.hasNext()){
            String request = it.next();
            String keyValue = map.get(request);
            System.out.println("正在处理："+request+";该"+keyValue+"共计"+getpageSum(request)+"页。");
            int snum = Integer.parseInt(getpageSum(request));
            for(int sum = 0;sum<snum;sum++){
                String url = request.substring(0, request.lastIndexOf("_") + 1) + sum+1 + ".html";
                threadPool.execute(new Jthread(url));
                if(sum == 5)break;//测试
            }

            System.out.println("完成："+request+";所属类别为："+keyValue);
        }
        threadPool.shutdown();
    }

    class Jthread implements Runnable{
        String url;
        public Jthread(String url){
            this.url = url;
        }
        public void run(){
            if(!Thread.currentThread().isInterrupted()){
                synchronized (this){
                    System.out.println(Thread.currentThread().getName()+";正在处理："+url);
                    List<JBContent> list = parseUrl(url);
                    if(list!=null){
                        lists.add(list);
                    }else {

                    }//什么都不做
                    System.out.println(url+"解析已经完成，准备写入Excel");
                    writeToExcel();
                    System.out.println(url+";已经写入Excel");
                }
            }
            try{
                Thread.sleep(60);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    public static String getUserAgent()
    {
        Random random = new Random();
        int i = random.nextInt(USER_AGENT.size());
        String userAgent = USER_AGENT.get(i);
        return userAgent;
    }

    public static  Proxy randomProxy() //随机获得proxy
    {
        String proxyUrl = "http://127.0.0.1:5010/get/";
        Proxy randomProxy = null;
        HttpClient client = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(proxyUrl);
        HttpResponse response = null;
        try{
            response = client.execute(httpget);
            if(response.getStatusLine().getStatusCode()==200)
            {
                HttpEntity entity = response.getEntity();
                String content = EntityUtils.toString(entity,"UTF-8");

                JSONObject data = JSON.parseObject(content);
                String proxydata = data.getString("proxy");
                int pos = proxydata.indexOf(":");
                if(pos > 0){
                    String port = proxydata.substring(pos+1);
                    String proxyaddr = proxydata.substring(0,pos);
                    int proxyPort = Integer.parseInt(port);
                    SocketAddress socketAddress = new InetSocketAddress(proxyaddr,proxyPort);
                    randomProxy = new Proxy(Proxy.Type.HTTP,socketAddress);
                }

            }
        }catch(Exception e){
            System.out.println("处理:"+proxyUrl+"失败，返回状态码："+response.getStatusLine().getStatusCode());
            return null;
        }finally{
            client.getConnectionManager().shutdown();
        }
        return randomProxy;
    }

    public static Map<String,String> getUrl(String mainUrl)//采集热门导航下电子书种类的所有链接
    {
        Map<String,String> map = new HashMap<String,String>();
        try {
            Document doc = Jsoup.connect(mainUrl)
                    .header("User_Agent",getUserAgent())
                    .get();
            Elements elements1 = doc.select(".cate.app > p > a");
            for(int i= 0;i<elements1.size();i++)
            {
                String link1 = elements1.get(i).attr("abs:href");
                String linkText = elements1.get(i).text();
                map.put(link1,linkText);////电子书
            }

            Elements elements2 = doc.select(".cate.app > p > a");
            for(int i = 0;i<elements2.size();i++)
            {
                String link2 = elements2.get(i).attr("abs:href");
                String linkText = elements2.get(i).text();
                map.put(link2,linkText);//数据库xml
            }

            Elements elements3 = doc.select(".cate.info > p > a");
            for(int i = 0;i<elements3.size();i++)
            {
                String link3 = elements3.get(i).attr("abs:href");
                String linkText = elements3.get(i).text();
                map.put(link3,linkText);//编程开发
            }
        }catch(IOException e){
            System.out.println("处理："+mainUrl+";失败");
            return null;
        }
        return map;
    }

    public static List<JBContent> parseUrl(String request)
    {
        List<JBContent> list = new ArrayList<>();
        Proxy proxy = randomProxy();
        String userAgent = getUserAgent();
        long sleepMillis = 0;
        for(int i = 0;i<5;i++)//IP代理出现问题时，循环
        {
            if (sleepMillis != 0) {
                try {
                    Thread.sleep(sleepMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sleepMillis = 0;
            }

            try {
                Document doc = Jsoup.connect(request)
                        .proxy(proxy)
                        .userAgent(userAgent)
                        .validateTLSCertificates(false)//javax.net.ssl.SSLHandshakeException
                        .timeout(6000)
                        .get();
                Elements elements = doc.select(".c-list.clearfix > li");
                for (int j = 0; j < elements.size(); j++) {
                    String bookName = elements.get(j).select(".top-tit > .tit > a").text();
                    String bookDowlink = elements.get(j).select(".top-tit > .tit > a").attr("abs:href");
                    String panAddr = getPanaddr(bookDowlink,proxy,userAgent);
                    String bookBelong = elements.get(j).select(".item > .con > .rinfo > .other > .intro > a").text();
                    String size = elements.get(j).select(".item > .con > .rinfo > .other > span:nth-child(2)").text();
                    String bookSize = size.substring(size.lastIndexOf("：")+1);
                    String time = elements.get(j).select(".item > .con > .rinfo > .other > span:nth-child(3)").text();
                    String bookTime = time.substring(time.lastIndexOf("：")+1);
                    String bookDesc = elements.get(j).select(".item > .con > .rinfo > .desc").text();
                    String bookShare = bookDowlink.substring(bookDowlink.lastIndexOf("/") + 1, bookDowlink.lastIndexOf("."));
                    list.add(new JBContent(bookName, bookSize, bookDesc, bookBelong, bookShare, bookDowlink, bookTime,panAddr));
                }
            }catch(IOException e){
                System.out.println("处理:" + request + "失败，代理Proxy:" +proxy+";"+"\n"+"原因："+e.toString());
                proxy = randomProxy();
                sleepMillis = 1000;
                continue;
            }
            break;
        }
        if(list.size()==0)return null;
        return list;
    }
    public static String  getPanaddr(String bookDowlink,Proxy proxy,String userAgent){
        String panAddr;
        try{
            Document doc = Jsoup.connect(bookDowlink)
                    .validateTLSCertificates(false)//javax.net.ssl.SSLHandshakeException
                    .timeout(6000)
                    .userAgent(userAgent)
                    .proxy(proxy)
                    .get();
            Element element = doc.select("li.baidu > a").first();
            if(element != null){
                panAddr = element.attr("href");
            }else{
                return null;
            }
        }catch(IOException e){
            System.out.println("链接："+bookDowlink+";获取网盘地址失败");
            return null;
        }
        return panAddr;

    }

    public String getpageSum(String request) //获得页面总数
    {
        String snum ;
        try{
            Document doc = Jsoup.connect(request)
                    .userAgent(getUserAgent())
                    .get();
            Element element = doc.select(".plist > a[href]").last();
            if(element != null){
                String link = element.attr("href");
                snum = link.substring(link.lastIndexOf("_")+1,link.lastIndexOf("."));
            }else{
                snum = "1";
                System.out.println("请检查："+request+";是否为1页，已默认为1页");
            }
        }catch(IOException e){
            snum = "1";
            System.out.println("链接:"+request+"处理失败，已默认为1页");
        }
        return snum;
    }

    public void writeToExcel() {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("zhangling");
        HSSFRow row = sheet.createRow(0);
        //设置列宽
        sheet.setColumnWidth((short) 0, (short) (20 * 256));
        sheet.setColumnWidth((short) 2, (short) (15 * 256));
        sheet.setColumnWidth((short) 3, (short) (15 * 256));
        sheet.setColumnWidth((short) 5, (short) (30 * 256));
        sheet.setColumnWidth((short) 6, (short) (40 * 256));
        sheet.setColumnWidth((short) 7, (short) (30 * 256));

        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        HSSFCell cell2 = row.createCell(0);
        cell2.setCellValue("书名");
        cell2.setCellStyle(style);

        HSSFCell cell3 = row.createCell(1);
        cell3.setCellValue("大小");
        cell3.setCellStyle(style);

        HSSFCell cell4 = row.createCell(2);
        cell4.setCellValue("更新时间");
        cell4.setCellStyle(style);

        HSSFCell cell5 = row.createCell(3);
        cell5.setCellValue("类别");
        cell5.setCellStyle(style);

        HSSFCell cell6 = row.createCell(4);
        cell6.setCellValue("分享码");
        cell6.setCellStyle(style);

        HSSFCell cell7 = row.createCell(5);
        cell7.setCellValue("下载页面");
        cell7.setCellStyle(style);

        HSSFCell cell8 = row.createCell(6);
        cell8.setCellValue("描述");
        cell8.setCellStyle(style);

        HSSFCell cell9 = row.createCell(7);
        cell9.setCellValue("网盘地址");
        cell9.setCellStyle(style);


        if (!lists.isEmpty()) {
            FileOutputStream fos;
            HSSFCellStyle style1 = wb.createCellStyle();
            style1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中
            style1.setWrapText(true);//内容可换行
            for (List<JBContent> list : lists) {
                for (int j = 0; j < list.size(); j++) {
                    try {
                        row = sheet.createRow((short) sheet.getLastRowNum() + 1);
                        row.setHeight((short) (100 * 20));//设置行高，POI中的行高＝Excel的行高度*20

                        HSSFCell cella = row.createCell(0);
                        cella.setCellValue(list.get(j).getBookName());
                        cella.setCellStyle(style1);

                        HSSFCell cellb = row.createCell(1);
                        cellb.setCellValue(list.get(j).getBookSize());
                        cellb.setCellStyle(style1);

                        HSSFCell cellc = row.createCell(2);
                        cellc.setCellValue(list.get(j).getBookTime());
                        cellc.setCellStyle(style1);

                        HSSFCell celld = row.createCell(3);
                        celld.setCellValue(list.get(j).getBookBelong());
                        celld.setCellStyle(style1);

                        HSSFCell celle = row.createCell(4);
                        celle.setCellValue(list.get(j).getBookShare());
                        celle.setCellStyle(style1);

                        HSSFCell cellg = row.createCell(5);
                        cellg.setCellValue(list.get(j).getBookDowlink());
                        cellg.setCellStyle(style1);

                        HSSFCell cellh = row.createCell(6);
                        cellh.setCellValue(list.get(j).getBookDesc());
                        cellh.setCellStyle(style1);

                        HSSFCell celli = row.createCell(7);
                        celli.setCellValue(list.get(j).getPanAddr());
                        celli.setCellStyle(style1);
                        fos = new FileOutputStream(new File("C:Users/Xiao Mi/Desktop/zengext.xls"));
                        wb.write(fos);
                        fos.flush();
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
    public static void main (String[]args)
    {
       JBtest jb = new JBtest();
       jb.run();
    }
}
/**
 * FileName: JBContent
 * Author:   Xiao Mi
 * Date:     2019-11-10 13:17
 * Description: 脚本之家提取内容的封装
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhang;

/**
 * 〈一句话功能简述〉<br> 
 * 〈脚本之家提取内容的封装〉
 *
 * @author Xiao Mi
 * @create 2019-11-10
 * @since 1.0.0
 */
public class JBContent {
    String bookName;//书名
    String bookSize;//书大小
    String bookDesc;//书描述
    String bookBelong;//书类别
    String bookTime;//更新时间
    String bookShare;//书分享码
    String bookDowlink;//书下载页面链接
    String panAddr;

    public JBContent(String bookName, String bookSize, String bookDesc, String bookBelong,
                     String bookShare, String bookDowlink, String bookTime,String panAddr){
        this.bookName = bookName;
        this.bookSize = bookSize;
        this.bookDesc = bookDesc;
        this.bookBelong = bookBelong;
        this.bookShare = bookShare;
        this.bookDowlink = bookDowlink;
        this.bookTime = bookTime;
        this.panAddr = panAddr;

    }
    public String getPanAddr() {
        return panAddr;
    }

    public void setPanAddr(String panAddr) {
        this.panAddr = panAddr;
    }

    public String getBookTime() {
        return bookTime;
    }

    public void setBookTime(String bookTime) {
        this.bookTime = bookTime;
    }

    public String getBookDesc() {
        return bookDesc;
    }

    public void setBookDesc(String bookDesc) {
        this.bookDesc = bookDesc;
    }

    public String getBookBelong() {
        return bookBelong;
    }

    public void setBookBelong(String bookBelong) {
        this.bookBelong = bookBelong;
    }

    public String getBookShare() {
        return bookShare;
    }

    public void setBookShare(String bookShare) {
        this.bookShare = bookShare;
    }

    public String getBookDowlink() {
        return bookDowlink;
    }

    public void setBookDowlink(String bookDowlink) {
        this.bookDowlink = bookDowlink;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookSize() {
        return bookSize;
    }

    public void setBookSize(String bookSize) {
        this.bookSize = bookSize;
    }
}
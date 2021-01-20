package com.xlys.zypwhomepage.domain;

public class Article {

    // 文章ID
    private Integer id;

    // 文章标题
    private String title;

    // 文章阅读次数
    private Integer readTimes;

    // 文章收藏次数
    private  Integer collectTimes;

    // 文章内容，数据库为longtext类型
    private String content;

    public Article() {
    }

    public Article(Integer id, String title, Integer readTimes, Integer collectTimes, String content) {
        this.id = id;
        this.title = title;
        this.readTimes = readTimes;
        this.collectTimes = collectTimes;
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getReadTimes() {
        return readTimes;
    }

    public void setReadTimes(Integer readTimes) {
        this.readTimes = readTimes;
    }

    public Integer getCollectTimes() {
        return collectTimes;
    }

    public void setCollectTimes(Integer collectTimes) {
        this.collectTimes = collectTimes;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", readTimes=" + readTimes +
                ", collectTimes=" + collectTimes +
                ", content='" + content + '\'' +
                '}';
    }
}

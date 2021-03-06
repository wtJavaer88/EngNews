package com.wnc.news.api.common;

public class Comment
{
	private long articleId;
	private String content;
	private String userId;
	private String userName;
	private int up;
	private int down;
	private String newsId;
	private int priority;// 评论的优先级(或者说等级),自定义排序时很重要
	private String createTime;

	public Comment()
	{
		setPriority(100);
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public int getUp()
	{
		return up;
	}

	public void setUp(int up)
	{
		this.up = up;
	}

	public int getDown()
	{
		return down;
	}

	public void setDown(int down)
	{
		this.down = down;
	}

	public String getNewsId()
	{
		return newsId;
	}

	public void setNewsId(String newsId)
	{
		this.newsId = newsId;
	}

	@Override
	public String toString()
	{
		return content;
	}

	public int getPriority()
	{
		return priority;
	}

	public void setPriority(int priority)
	{
		this.priority = priority;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public long getArticleId()
	{
		return articleId;
	}

	public void setArticleId(long articleId)
	{
		this.articleId = articleId;
	}

	public String getCreateTime()
	{
		return createTime;
	}

	public void setCreateTime(String createTime)
	{
		this.createTime = createTime;
	}
}

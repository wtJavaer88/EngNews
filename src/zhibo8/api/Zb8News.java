package zhibo8.api;

public class Zb8News {
	private String url;
	private String from_url;
	private String title;
	private String day;
	private String keyword;
	private String chs_content;
	private String eng_content;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFrom_url() {
		return from_url;
	}

	public void setFrom_url(String from_url) {
		this.from_url = from_url;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getChs_content() {
		return chs_content;
	}

	public void setChs_content(String chs_content) {
		this.chs_content = chs_content;
	}

	public void setEng_content(String eng_content) {
		this.eng_content = eng_content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getEng_content() {
		return eng_content;
	}
}

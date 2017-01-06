package zhibo8.api;

public enum SportType {
	Zuqiu("zuqiu"), NBA("nba");
	String desc;

	public String getDesc() {
		return desc;
	}

	SportType(String desc) {
		this.desc = desc;
	}
}

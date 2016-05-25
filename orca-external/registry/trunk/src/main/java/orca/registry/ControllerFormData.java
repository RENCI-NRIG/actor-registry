package orca.registry;

public class ControllerFormData {
	String name, url, desc, action;
	
	
	public void setAction(String a) {
		action = a;
	}
	
	public String getAction() {
		return action;
	}
	
	public void setName(String n) {
		name = n;
	}
	
	public String getName() {
		return name;
	}
	
	public void setUrl(String u){
		url = u;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setDesc(String d) {
		desc = d;
	}
	
	public String getDesc() {
		return desc;
	}
}

package orca.registry;

public class ImageFormData {
	String guid, action;
	
	public void setHash(String g) {
		guid = g;
	}
	
	public String getHash() {
		return guid;
	}
	
	public void setAction(String a) {
		action = a;
	}
	
	public String getAction() {
		return action;
	}
}

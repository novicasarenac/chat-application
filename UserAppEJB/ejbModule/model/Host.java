package model;

import java.io.Serializable;

public class Host implements Serializable {

	private static final long serialVersionUID = 1L;
	private String address;
	private String alias;
	
	public Host() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
}

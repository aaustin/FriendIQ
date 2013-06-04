package com.friendiq.android;

public class Contact {
	public int index;	
	public String firstname;
	public String lastname;
	public String datasourceid;
	
	public Contact() {
		this.index = -1;
		this.firstname = "none";
		this.lastname = "none";
		this.datasourceid = "none";
	}
	
	public Contact(int index, String firstname, String lastname, String datasourceid) {
		this.index = index;		
		this.firstname = firstname;
		this.lastname = lastname;		
		this.datasourceid = datasourceid;
		
	}
	
	public Contact clone() {
		Contact con = new Contact();
		con.index = this.index;
		con.firstname = this.firstname;
		con.lastname = this.lastname;
		con.datasourceid = this.datasourceid;		
		return con;
	}
	
	
}

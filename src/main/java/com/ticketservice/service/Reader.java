package com.ticketservice.service;

import java.util.Scanner;

public class Reader {

	private  Scanner scanner = new Scanner(System.in);

	public  Scanner getScanner() {
		return scanner;
	}

	public  void setScanner(Scanner scanner) {
		this.scanner = scanner;
	}
	
	public String read(){
		return this.scanner.nextLine();
	}
	
	public int readInt(){
		return this.scanner.nextInt();
	}
}

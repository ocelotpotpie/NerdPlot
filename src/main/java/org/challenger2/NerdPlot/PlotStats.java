package org.challenger2.NerdPlot;

public class PlotStats {
	private String area;
	private int free = 0;
	private int used = 0;
	
	public PlotStats(String area) {
		this.area = area;
	}
	
	public String getArea() {
		return area;
	}
	
	public void addUsed() {
		used++;
	}
	
	public void addFree() {
		free++;
	}
	
	public int getFree() {
		return free;
	}
	
	public int getUsed() {
		return used;
	}
	
	public int getTotal() {
		return free + used;
	}
}

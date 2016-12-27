package cacm;

public class CacmItem {

	public String id;
	public String[] authors;
	public String title;
	public String summary;
	public String content;
	
	public CacmItem(String line) {
		
		String[] splitted = line.split("\t");
		this.id = splitted[0];

		String authorList = splitted[1];
		this.authors = authorList.split(";");
		if(this.authors[0].equals(""))
			this.authors = null;
		
		this.title = splitted[2];
		if (splitted.length == 4) {
			this.summary = splitted[3];
		} else {
			this.summary = null;
		}

		this.content = this.title+" "+this.summary;
	}
}

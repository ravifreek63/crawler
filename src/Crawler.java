/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
 
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.*;

import javax.imageio.ImageIO;

/**
*
* @author Ravi
*/
public class Crawler {
	private List<String> globalURls;
	private List<String> seenImages;
	private List<String> tempURLs;
	private DatabaseStore databaseStore;
	
	private void printSeenURLs (){
		System.out.println("URLs seen");
		for (String url : this.globalURls){
			System.out.println(url);
		}
	} 
	
	private void printSeenImages (){
		System.out.println("Images seen");
		for (String image : this.seenImages){
			System.out.println(image);
		}
	}
	
    private void parseDocument (String documentURL){
    	try {
            URL currentURL = new URL(documentURL);
            BufferedReader br = new BufferedReader(new InputStreamReader(currentURL.openStream()));
            String strTemp = "";
            while(null != (strTemp = br.readLine())){
            	Document doc = Jsoup.parse(strTemp);
            	Elements links = doc.select("a[href]"); // a with href
            	Elements media = doc.select("[src]");            	
            	for (Element element : links) {
            		String linkStr = element.attr("abs:href");            	                		
            			if (linkStr.length()!=0){
            				this.tempURLs.add(linkStr);            	    		
            	    	}            	    
            	}            	
            	for (Element image : media) {
                    if (image.tagName().equals("img")){
                    	for(Attribute attribute : image.attributes())
                        {
                            if(attribute.getKey().equalsIgnoreCase("src"))
                            {
                            	String linkStr = new URL(currentURL, attribute.getValue()).toString();  
		                	    if (!(this.seenImages.contains(linkStr) || linkStr.length()==0)){
		                	    	this.seenImages.add (linkStr);
		                	    }
                            }
                        }
                    }
                }
        }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    	
    }
	public Crawler () throws SQLException{
		this.globalURls = new ArrayList<String>();
		this.seenImages = new ArrayList<String>();	
		this.tempURLs = new ArrayList<String>();
		this.databaseStore = new DatabaseStore();
    }
	
	public void storeImages () throws MalformedURLException, IOException, SQLException{
		for (String image : this.seenImages){
			BufferedImage bufferImage = ImageIO.read(new URL(image));
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(bufferImage, "gif", os);
			InputStream is = new ByteArrayInputStream(os.toByteArray());
			this.databaseStore.storeImage(is, image, os.size());
			break;
		}
	}
	

	public static void main(String[] args) throws SQLException, MalformedURLException, IOException  {
		int count = 0;
    	Crawler crawler = new Crawler();
    	crawler.globalURls.add("http://www.princeton.edu");
    	while (true && count < 1){
    		for (String url : crawler.globalURls){
    			crawler.parseDocument(url);
    			count++;
    		}
    		if (crawler.tempURLs.size() == 0){
    			break;
    		} else {
    			crawler.globalURls.addAll(crawler.tempURLs);
    			crawler.tempURLs = new ArrayList<String>();
    		}	
    	}
    	crawler.printSeenURLs();
    	crawler.printSeenImages();    	
    	crawler.storeImages();
    }
}




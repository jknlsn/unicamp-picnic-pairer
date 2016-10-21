package application;

import java.net.*;
import java.io.*;
import java.util.*;

 /*
 * @author Alex Emery
 */

public class googleLookUp{
	
	private static String key;
	
	public googleLookUp(String APIkey){
		key = APIkey;
	}

  public ArrayList<Double> main(Volunteer male, ArrayList<Volunteer> females){

    String origin = male.address;
    ArrayList<Double> travelTime = new ArrayList<Double>();

    origin = replace(origin);
    //Need to make destination address html friendly
    ArrayList<String> parseDestination = new ArrayList<String>();
    int length = females.size();
    for(int i = 0; i<length;i++){
      String address = females.get(i).address;
      parseDestination.add(replace(address));
    }
    int remaining = length;
    //get results from http request
    int index = 0;

    while(remaining > 25)
    {
      ArrayList<String> temp = new ArrayList<String>();
      for(int i = index; i < (index+25); i++){
        // System.err.println(i);
        temp.add(parseDestination.get(i));
      }
      remaining = remaining - 25;
      index = index + 25;
      String result = "";
      try {
        result = distanceMatrix(origin,temp);
      }
      catch(Exception e){
        e.printStackTrace();
      }

      System.out.println(result);

      String argument[] = result.split("</element>");

      for(int i = 0; i < argument.length-1; i++){
        double time = 0;
        try {
            time = getTime(argument[i]);
        }
        catch (Exception e){
          time = 10000;
          System.out.println(females.get(i).name + " has an error and is invalid.");
          System.err.println(females.get(i).name + " has an error and is invalid.");
        }
        finally {
            travelTime.add(time);
        }

      }
    }

    // System.err.println("Index : "+index);
    // System.err.println("Remaining : "+remaining);

    ArrayList<String> temp = new ArrayList<String>();
    for(int i = index; i < (index + remaining);i++){
      temp.add(parseDestination.get(i));
    }
    String result = "";
    try { result = distanceMatrix(origin,temp);
    }
    catch(Exception e){
      e.printStackTrace();
    }

    String argument[] = result.split("</element>");

    for(int i = 0; i < argument.length-1; i++){
      double time = 0;
      try {
          time = getTime(argument[i]);
      }
      catch (Exception e){
        time = 10000;
        System.out.println(females.get(i).name + " has an error and is invalid.");
        System.err.println(females.get(i).name + " has an error and is invalid.");
      }
      finally {
          travelTime.add(time);
      }
    }


    return travelTime;
  }

  public static String replace(String input){
    return input.replace(" ","%20");
  }

  public static double getTime(String input){
    double minutes = 0.0;
    double hours = 0.0;
    int start = input.indexOf("<text>");
    int finish = input.indexOf("</text>");
    String parse = input.trim().substring(start,finish);
    parse = parse.replace("<text>","");
    parse = parse.replace("xt>","");
    parse = parse.replace("</t","");
    String arguments[] = parse.split(" ");
    if(arguments[1].equals("hour") || arguments[1].equals("hours")){
      hours = Double.parseDouble(arguments[0]);
      if(arguments[3].equals("mins")||arguments[3].equals("min")){
        minutes = Double.parseDouble(arguments[2]);
      }
    }
    else if (arguments[1].equals("mins")||arguments[1].equals("min")){
      minutes = Double.parseDouble(arguments[0]);
    }
    return hours*60+minutes;
  }

  public static String distanceMatrix(String origin, ArrayList<String> destinations) throws Exception{
    StringBuilder result = new StringBuilder();
    StringBuilder urlString = new StringBuilder();
//    String key = "AIzaSyCv8ZlORqUQ-bktwH7C9ah5pvWo238e-vE";
    urlString.append("https://maps.googleapis.com/maps/api/distancematrix/xml?origins=");
    urlString.append(origin);
    urlString.append("&destinations=");
    urlString.append(destinations.get(0));
    for(int i = 1;i<destinations.size();i++){
      urlString.append("|");
      urlString.append(destinations.get(i));
    }

    urlString.append("&key=");
    urlString.append(key);
    String finalUrl = urlString.toString();
    // System.out.println(finalUrl);
    URL url = new URL(finalUrl);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String line;
    while((line = rd.readLine())!=null){
      result.append(line);
      result.append('\n');
    }
    rd.close();
    return result.toString();
  }
}

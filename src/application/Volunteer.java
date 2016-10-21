package application;

//Class to contain representations of students

//TODO: Add mobile number, email etc?

public class Volunteer {

public String name;
public String email;
public String phone;
public int exp;
public boolean driver;
public String address;
public double lat;
public double lng;

public Volunteer(String fullName, String emailAddress, String phoneNumber, int experienced, boolean isDriver, String streetAddress, double latitude, double longitude)
{
this.name = fullName;
this.email = emailAddress;
this.phone = phoneNumber;
this.exp = experienced;
this.driver = isDriver;
this.address = streetAddress;
this.lat = latitude;
this.lng = longitude;
}

public void print()
{
System.out.println("NAME: " + name);
}

}

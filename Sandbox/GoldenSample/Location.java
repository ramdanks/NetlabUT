package Sandbox.GoldenSample;

// as a device under test
public class Location
{
    private String city;
    private String province;
    private String country;

    public Location() {}
    public Location(String city, String province, String country)
    {
        this.city = city;
        this.province = province;
        this.country = country;
    }

    public String getCity() { return city; }
    public String getProvince() { return province; }

    private boolean isIndonesia(String country) { return country != null && country.equalsIgnoreCase("indonesia"); }
}
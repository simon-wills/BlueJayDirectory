public class Pitcher extends Player{
    public String ip;
    public String era;
    public String k; 
    public String whip;
    public Pitcher(String position, String name, String age, String jersey, String ab, String avg, String rbi, String hr, String ip, String era, String k, String whip){
        super(position, name, age, jersey, ab, avg, rbi, hr); 
        this.ip = ip; 
        this.era = era; 
        this.k = k;
        this.whip = whip; 
    }
}

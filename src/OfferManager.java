import java.util.ArrayList;
public class OfferManager {
    ArrayList<Offer> offers = new ArrayList<Offer>();
    public void makeOffer(){

    }
    public void listOffers(ArrayList<Offer>offers){
        for(int i = 0 ; i < offers.size() ; i++){
            System.out.println( (i + 1) + "st Offer: " + offers.get(i) );
        }
    }
    public boolean findOffer(){
        return false;
    }
    public void closeOffer(){

    }
    public boolean acceptOffer(){
        return false;
    }
    public boolean declineOffer(){
        return false;
    }
}

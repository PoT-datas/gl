package api.pot.gl.xiv.tools;

import java.util.ArrayList;
import java.util.List;

public class StarSystemMng {
    private List<StarInfo> starInfos = new ArrayList<StarInfo>();

    public List<StarInfo> getStarInfos() {
        return starInfos;
    }

    public void addStarInfo(StarInfo starInfo){
        for(int i=0;i<starInfos.size();i++){
            if(starInfos.get(i).starNumber==starInfo.starNumber){
                starInfos.get(i).addNbr_inscription(starInfo.nbr_inscription);
                return;
            }
        }
        starInfos.add(starInfo);
    }

    int nbr_starer = 0;
    List<Float> listStarInfos = new ArrayList<>();

    public void generateStatDatas() {
        listStarInfos.clear();
        for(int i=0;i<=5;i++){
            listStarInfos.add(0f);
        }
        nbr_starer = 0;
        for(StarInfo starInfo : starInfos){
            nbr_starer += starInfo.nbr_inscription;
        }
        for(StarInfo starInfo : starInfos){
            listStarInfos.set(starInfo.starNumber, Float.valueOf(starInfo.nbr_inscription)/nbr_starer);
        }
    }

    public int getNbrStarer() {
        return nbr_starer;
    }

    public List<Float> getListStarInfos() {
        return listStarInfos;
    }

    public static class StarInfo{
        public int starNumber = -1;
        public int nbr_inscription = 0;

        public void setStarNumber(int starNumber) {
            this.starNumber = starNumber;
        }

        public void setNbr_inscription(int nbr_inscription) {
            this.nbr_inscription = nbr_inscription;
        }

        public void addNbr_inscription(int nbr_inscription) {
            this.nbr_inscription += nbr_inscription;
        }

        public StarInfo(int starNumber, int nbr_inscription) {
            this.starNumber = starNumber;
            this.nbr_inscription = nbr_inscription;
        }
    }

}

 package K_MEANS;

import java.util.LinkedList;

/**
 *
 * @author user
 */


public class Cluster {
   public LinkedList<Point> dta_points;
   
   public Cluster(){
       dta_points=new LinkedList<>();
   }
   
   public Point compute_cluster_centroid(){
       Point centroid=new Point(0,0);
       for(int i=0;i<dta_points.size();i++){
           centroid.x+=dta_points.get(i).x;
           centroid.y+=dta_points.get(i).y;
       }
       centroid.x=centroid.x/dta_points.size();
       centroid.y=centroid.y/dta_points.size();
       return centroid;
   }
}

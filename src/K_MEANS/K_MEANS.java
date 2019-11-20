
package K_MEANS;



/**
 *
 * @author user
 */


public class K_MEANS {
    public  int num_of_clusters;
    public  float tolerance;
    public  float max_iterations;
    public Point[] dta;
    public  Point[] centroid; 
    public Cluster[] clusters;
    
    /**
     * Constructor.
     * @param no_clusters total number of clusters
     * @param toler the tolerance value for convergence
     * @param max_iter maximum number of iterations the simulation goes through.
     * @param no_of_points total number of points to be generated.
     */
    public K_MEANS(int no_clusters,float toler, float max_iter,int no_of_points){
        num_of_clusters=no_clusters;
        tolerance=toler;
        max_iterations=max_iter;
        generate_XY_data(no_of_points);
        clusters=new Cluster[num_of_clusters];
        for(int i=0;i<num_of_clusters;i++){
            clusters[i]=new Cluster();
        }
    }
    
    public  void generate_XY_data(int num_of_points){
            dta=new Point[num_of_points];
            for(int i=0;i<dta.length;i++){
                dta[i]=new Point();
            }
    }
    
    public float euclidean_distance(Point p1, Point p2){
        return (float) Math.sqrt(Math.pow(p1.x-p2.x,2)+Math.pow(p1.y-p2.y,2));
    }
    
    public int find_closest_centroid_index(Point dta_pt){
        int closest_centroid_index=0;
        float dist_min=euclidean_distance(dta_pt, centroid[0]);
        for(int i=1;i<centroid.length;i++){
            float dist_curr=euclidean_distance(dta_pt, centroid[i]);
            if(dist_curr<dist_min){
                closest_centroid_index=i;
                dist_min=dist_curr;
            }
        }
        return closest_centroid_index;
    }
    
    public void init_centroids(){
        centroid= new Point[num_of_clusters];
        for(int i=0;i<num_of_clusters;i++){
            centroid[i]=dta[i];
        }
    }
    
    public  void recalculate_centroids(Cluster[] c){
        for(int i=0;i<c.length;i++){
            centroid[i]=c[i].compute_cluster_centroid();
        } 
    }
    
    public  float compute_centroid_gradient(Point[] previous, Point[] current){
        float gradient=0;
        for(int i=0;i<previous.length;i++){
            gradient+=euclidean_distance(previous[i], current[i]);
        }
        return gradient;
    }
    
    public void cluster(){
        //initialize centroids
        init_centroids();
        
        //initialize clusters
      
        
        for(int i=0;i<max_iterations;i++){
            //
            for(int j=0;j<dta.length;j++){
                int closest=find_closest_centroid_index(dta[j]);
                clusters[closest].dta_points.add(dta[j]);
            }
            
            //backup the previous centroidal data.
            Point [] previous=new Point[centroid.length];
            for(int k=0;k<previous.length;k++){
                previous[k]=new Point(centroid[k].x,centroid[k].y);
            }
            
            
            recalculate_centroids(clusters);
            
            //check the difference between the current and previous total gradients
            //if the gradient is small enough, then exit the loop.
            float diff=compute_centroid_gradient(previous,centroid);
            System.out.println("grad: "+diff);
            if(diff<tolerance){
                break;
            }
        }
    }
    
  
            
}

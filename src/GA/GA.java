/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GA;

import java.awt.Graphics2D;

/**
 *
 * @author user
 */
public class GA {

    public double mutationProbability = 0.01;
    public double crossoverProbability = 0.5;
    public int tournamentSize = 5;
    public float selection_size = 0.8f;
    public int population_size = 100;
    public String target_function;
    public Population p;
    public float minX,minZ, maxX,maxZ;
    public boolean maximize;

    public GA(int pop_size, float mutation_prob, float crossover_prob, float selection_prob,float minX,float maxX,float minZ,float maxZ,String function_type,boolean maximize) {
        mutationProbability = mutation_prob;
        crossoverProbability = crossover_prob;
        selection_size = selection_prob;
        population_size = pop_size;
        this.minX=minX;
        this.maxX=maxX;
        this.minZ=minZ;
        this.maxZ=maxZ;
        this.target_function=function_type;
        this.maximize=maximize;
        p = new Population(population_size,minX,maxX,minZ,maxZ,target_function,true,maximize);
    }

    /**
     * Implements the uniform crossover algorithm, which swaps gene indices at
     * random places based on the crossover probability
     *
     * @param i1 First Individual
     * @param i2 Second Individual
     * @return
     */
    public Individual crossover(Individual i1, Individual i2) {
        Individual offspring = new Individual(minX,maxX,minZ,maxZ,target_function);
       // offspring.init_genes();
        //crossover logic here
        for (int i = 0; i < i1.gene_length; i++) {
            if (Math.random() <= crossoverProbability) {
                offspring.setGene(i, i1.getGene(i));
            } else {
                offspring.setGene(i, i2.getGene(i));
            }
        }
        offspring.decode(target_function);
        offspring.fitness(target_function);
        return offspring;
    }

    /**
     * Randomly modify gene information of an individual at random positions
     *
     * @param i1 individual to be mutated
     */
    public void mutate(Individual i1) {
        //mutation logic here
        for (int i = 0; i < i1.size(); i++) {
            if (Math.random() <= mutationProbability) {
                byte mutation = (byte) Math.round(Math.random());
                i1.setGene(i, mutation);
            }
        }

    }

    /**
     * Implements tournament selection algorithm, where n individuals from the
     * population are selected and compared based on their fitness value.
     *
     * @param p1 the population of individuals
     * @return
     */
    public Individual tournamentSelection(Population p1) {
        Individual fit_ind;
        //initialize population size, but do not initialize it
        //since the tournament population is composed of random 
        //individuals(with already initialized genes) and not new
        //guys.
        Population tournament = new Population(tournamentSize,minX,maxX,minZ,maxZ,target_function, false,maximize);
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * p1.getSize());
            tournament.saveIndividual(i, p1.getIndividual(randomId));
        }
        //from the tournament population select the fittest
        fit_ind = tournament.getFittest(target_function);
        return fit_ind;
    }

    /**
     * Performs evolutionary computations(crossover, mutation and selection) on
     * a population of individuals.
     *
     * @param p1 the population that needs to evolve
     * @return
     */
    public void Evolve(int NumberOfGenerations) {

        for (int j = 0; j < NumberOfGenerations; j++) {
            step();
            System.out.println("generation: " + j + " best fitness:  " + p.getFittest(target_function).fit_val);
        }

    }

    public void step() {
        Population Descendants = new Population(p.getSize(),minX,maxX,minZ,maxZ,target_function, false,maximize);
        //elite selection: save the fittest indiviudal 
        Descendants.saveIndividual(0, p.getFittest(target_function));

        for (int i = 1; i < p.getSize(); i++) {
            //select fit individual though tournament selection
            Individual parent1 = tournamentSelection(p);
            Individual parent2 = tournamentSelection(p);
            //crossover
            Individual offspring = crossover(parent1, parent2);
            Descendants.saveIndividual(i, offspring);
        }
        //mutate each individual
        for (int i = 1; i < Descendants.getSize(); i++) {
            mutate(Descendants.getIndividual(i));
        }
        p = Descendants;
    }
    
//    public void draw_world(Graphics2D g2d){
//        for(int i=0;i<p.getSize();i++){
//            Individual ind=p.getIndividual(i);
//            ind.draw_individual();
//        }
//        
//        draw_target(g2d);
//    }
    

    public static void main(String[] args) {
     //   GA g = new GA(20, 0.05f, 0.5f, 0.8f,-6f,6f,-6f,6f,"x^2+z^2");
     //   g.Evolve(100);
//        System.out.println("best fitness:  " + g.p.getFittest().fit_val);
    }
}

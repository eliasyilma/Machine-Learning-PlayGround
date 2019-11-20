/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GA;

/**
 *
 * @author Elias Yilma
 */
public class Population {

    //individuals
    Individual[] individuals;
    Individual fittest;
    public boolean maximum;

    /**
     * Population constructor
     *
     * @param populationSize number of individuals in the population
     * @param minX
     * @param maxX
     * @param minZ
     * @param maxZ
     * @param function
     * @param initialize if true, initializes the chromosomes(gene info) of each
     * individual
     */
    public Population(int populationSize, float minX, float maxX, float minZ, float maxZ, String function, boolean initialize,boolean maximize) {
        //initialization of population
        individuals = new Individual[populationSize];
        if (initialize) {
            for (int i = 0; i < populationSize; i++) {
                Individual newIndividual = new Individual(minX, maxX, minZ, maxZ, function);
                // newIndividual.init_genes();
                saveIndividual(i, newIndividual);
            }
        }
        maximum=maximize;
    }

    /**
     * find the fittest individual within the population
     *
     * @param target_function
     * @return
     */
    public Individual getFittest(String target_function) {
        Individual fittest = individuals[0];
        if (!maximum) {
            for (int i = 0; i < individuals.length; i++) {
                if (fittest.fitness(target_function) >= getIndividual(i).fitness(target_function)) {
                    fittest = getIndividual(i);
                }
            }
        } else {
            for (int i = 0; i < individuals.length; i++) {
                if (fittest.fitness(target_function) <= getIndividual(i).fitness(target_function)) {
                    fittest = getIndividual(i);
                }
            }
        }
        this.fittest = fittest;
        return fittest;
    }

    /**
     * get individual at index.
     *
     * @param index
     * @return
     */
    public Individual getIndividual(int index) {
        return individuals[index];
    }

    /**
     * get the population size
     *
     * @return
     */
    public int getSize() {
        return individuals.length;
    }

    public void print_population() {
        for (int i = 0; i < individuals.length; i++) {
            System.out.println(" x: " + individuals[i].pt.x + " y: " + individuals[i].pt.y + " z: " + individuals[i].pt.z);

        }
    }
    
    public float average_fitness() {
        float sum=0;
        for (int i = 0; i < individuals.length; i++) {
           sum+=individuals[i].fit_val;

        }
        return sum/(float) individuals.length;
    }

    /**
     * insert the individual at the target index in the individuals array.
     *
     * @param index position of insertion.
     * @param indiv the individual to be inserted.
     */
    public void saveIndividual(int index, Individual indiv) {
        individuals[index] = indiv;
    }

}

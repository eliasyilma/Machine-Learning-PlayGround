/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GA;

import K_MEANS.Point;
import java.math.BigInteger;

/**
 *
 * @author Elias Yilma
 */
public class Individual {

    int gene_length = 30;
    float[] genes;
    float fit_val;
    String function_typ;
    Point3 pt;

    public Individual(float minX, float maxX, float minZ, float maxZ, String function_type) {
        init_genes(minX, maxX, minZ, maxZ, function_type);
        decode(function_type);
        function_typ = function_type;
    }

    public void init_genes(float minX, float maxX, float minZ, float maxZ, String function_type) {
        genes = new float[gene_length];
        for (int i = 0; i < gene_length / 2; i++) {
            //reduce a generated number into smaller pieces
            genes[i] = (float) (minX + Math.random() * (maxX - minX)) / 12.0f;
        }
        for (int i = gene_length / 2; i < gene_length; i++) {
            //reduce a generated number into smaller pieces
            genes[i] = (float) (minZ + Math.random() * (maxZ - minZ)) / 12.0f;
        }
    }

    public void decode(String function_type) {
        float xd = 0, yd = 0, zd = 0;
        for (int i = 0; i < gene_length / 2; i++) {
            xd += genes[i];
        }
        for (int i = gene_length / 2; i < gene_length; i++) {
            zd += genes[i];
        }

        pt = new Point3(xd, yd, zd);
        fitness(function_type);
        pt.y = fit_val;
    }

    /**
     * Compute the fitness value of an individual by comparing it to the gene
     * information of the "solution" individual.
     *
     * @return the fitness value of the individual.
     */
    public float fitness(String function_type) {
        float fit = 0.0f;
        switch (function_type) {
            case "x^2+z^2":
                fit = (float) ((float) (pt.x * pt.x + pt.z * pt.z));
                break;
            case "sin(x)+sin(z)":
                fit = (float) ((float) Math.sin(pt.z) + Math.sin(pt.x));
                break;
            case "sin(z)":
                fit = (float) ((float) Math.sin(pt.z));
            case "9*(x^2+y^2)*e^(-x^2-y^2)":
                fit = (float) ((float) 4 * (pt.x * pt.x + pt.z * pt.z) * Math.exp((-pt.x * pt.x - pt.z * pt.z) / 6f));
                break;
            case "e^{-(x^2+y^2)^{0.5}} cos(4x) cos(4y)":
                fit =  (float) ((Math.exp(-Math.sqrt(pt.x*pt.x/12f+pt.z*pt.z/12f)*2f)*Math.cos(2*pt.x)*Math.cos(2*pt.z))*2f);
        }
        fit_val = fit;
        return fit;
    }

    public static byte[] int_to_bin(int decimal) {
        byte[] bin = new byte[20];
        int quot = decimal;
        int rem;
        int i = 0;
        while (quot >= 1) {
            rem = quot % 2;
            quot = quot / 2;
            bin[bin.length - 1 - i] = (byte) rem;
            i++;
        }

        return bin;
    }


    /**
     * set the value at the specified point in the chromosome to the value.
     *
     * @param index the position to be altered
     * @param value the new value that is placed at the index.
     */
    public void setGene(int index, float value) {
        genes[index] = value;
    }

    /**
     * get the gene information at the specified index.
     *
     * @param index the position of interest in the chromosome.
     * @return the value of the gene(either a 1 or a 0.)
     */
    public float getGene(int index) {
        return genes[index];
    }

    /**
     * returns the size of the chromosome.
     *
     * @return the size of the genes array.
     */
    public int size() {
        return genes.length;
    }

    /**
     * display the genetic information of an individual.
     */
    public void print_gene_info() {
        for (int x = 0; x < this.gene_length; x++) {
            System.out.print(this.genes[x] + " ");
        }
    }



}

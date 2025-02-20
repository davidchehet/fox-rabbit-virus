package FoxRabbitProject;/*
@author DavidChehet
@version 2.7.2024

This class is meant to represent a virus
that has been released into the environment of
Foxes and Rabbits.
 */

import java.awt.*;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


/**
 * FoxRabbitProject.Virus works in a way where it does not necessarily reproduce,
 * rather if it does not grab onto a host in a certain amount of
 * time, it will die.
 */

/**
 * Would call setInfectedColor on object after infecting it
 */
public class Virus extends Animal {

    // Characteristics of the CoronaVirus (class variables).

    // The age at which the FoxRabbitProject.Virus can start to mutate.
    private static final int MUTATION_AGE = 10;

    // The age to which the FoxRabbitProject.Virus can live without infecting any animal.
    private static final int MAX_AGE = 100;

    private static final Random rand = Randomizer.getRandom();

    // The likelihood of the virus mutating.
    private static double MUTATION_PROBABILITY = 0.03;

    // The maximum number of mutations.
    private static final int MAX_LITTER_SIZE = 7;

    private static final int RABBIT_FOOD_VALUE = 9;
    //the age of the virus
    private int age;

    private double victimHunger;

    private static final Color color = Color.RED;

    //Create a new virus
    public Virus(boolean randomAge, Field field, Location location)
    {
        super(field, location, false);
        if(randomAge) {
            age = rand.nextInt((int) MAX_AGE);
            victimHunger = rand.nextInt(RABBIT_FOOD_VALUE);
        }
        else {
            age = 0;
            victimHunger = RABBIT_FOOD_VALUE;
        }
    }

    /**
     * What the FoxRabbitProject.Virus does :
     * Mainly it uses its time alive to try to infect other animals.
     * If it is not able to infect anyone during its entire lifespan
     * it will die.
     * @param newViruses A list to receive newly born animals.
     */
    @Override
    public void act(List<Animal> newViruses) {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            createMutation(newViruses);
            Location attempt = findVictim();
            if(attempt == null) {
                // No food found - try to move to a free location.
               attempt = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
            if(attempt != null) {
                setLocation(attempt);
                victimHunger = victimHunger + 0.61;
            }
            else {
                // Overcrowding. -> Maybe remove this once we start testing
                setDead();
            }
        }


    }

    //Increase the age of the FoxRabbitProject.Virus.
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    //Increase the victim hunger of the FoxRabbitProject.Virus
    private void incrementHunger() {
        victimHunger--;
        if(victimHunger <= 0) {
            setDead();
        }
    }

    private Location findVictim() {

        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Rabbit) {
                Rabbit rabbit = (Rabbit) animal;
                if(rabbit.isAlive()) {
                    rabbit.getInfected();
                    return where;
                }
            }
            else if(animal instanceof Fox) {
                Fox fox = (Fox) animal;
                if(fox.isAlive()) {
                    fox.getInfected();
                    return where;
                }
            }
        }
        return null;
    }


    /**
     * Check whether or not this virus is to mutate at this step.
     * New births will be made into free adjacent locations.
     * @param babyViruses A list to return newly mutated viruses.
     */
    private void createMutation(List<Animal> babyViruses)
    {
        // New viruses are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = mutate();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Virus young = new Virus(false, field, loc);
            babyViruses.add(young);
        }
    }

    /**
     * Generate a number representing the number of mutations,
     * if it can breed.
     * @return The number of mutations (maybe zero).
     */
    private int mutate()
    {
        int mutations = 0;
        if(canMutate() && rand.nextDouble() <= MUTATION_PROBABILITY) {
            mutations = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return mutations;
    }

    /**
     * A virus can mutate if it has reached the mutation age.
     */
    private boolean canMutate()
    {
        return age >= MUTATION_AGE;
    }

    public Color getColor() { return color; }


}

